package com.fengzhixuan.timoc.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fengzhixuan.timoc.game.enemies.Orc;
import com.fengzhixuan.timoc.game.enums.PokerHand;
import com.fengzhixuan.timoc.game.enums.RoundPhase;
import com.fengzhixuan.timoc.game.enums.TargetingMode;
import com.fengzhixuan.timoc.webcontroller.messagetemplate.DiscardCardMessage;
import com.fengzhixuan.timoc.webcontroller.messagetemplate.PlayCardMessage;
import com.fengzhixuan.timoc.websocket.message.game.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.*;

public class Game
{
    // value passed by GameController via gameStart(), used for sending messages to players
    private SimpMessageSendingOperations messagingTemplate;

    private static Map<Integer, Game> games = new HashMap<>();

    private int code;
    private String codeString;
    private Map<String, Player> players = new HashMap<>(); // username is key
    private Map<Player, Boolean> playerOnlineStatuses = new HashMap<>(); // true means online/connected
    private Display display;  // emulates the state of displays
    private boolean isDisplayConnected = false;  // is there a connected display?
    private RoundPhase phase;
    private String[] playerOrder;  // stores all players' names, represents the order of players(who's player1, who plays first) in attack phase
    private int currentPlayer;  // current index to playerOrder array, represents whose turn it is now
    private Map<Integer, Enemy> enemies = new HashMap<>();
    private int enemyCount;  // increments each time an enemy is spawn, then is given to the spawned enemy as id
    private boolean gameStarted;  // whether the game has started
    private int roundNum;  // current round number
    private Random random;  // random number generator
    private long seed;  // seed of the random

    private Game(int code, Map<String, Player> players)
    {
        this.code = code;
        codeString = GameCodeGenerator.intToString(code);
        display = new Display();
        this.players = players;

        playerOrder = new String[players.size()];
        int count = 0;
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            // fill in the status map, initialize to all false
            playerOnlineStatuses.put(playerEntry.getValue(), false);
            // fill in the playerOrder array with all the names
            playerOrder[count] = playerEntry.getValue().getName();
            count++;
        }
        enemyCount = 0;
        gameStarted = false;
        roundNum = 0;

        seed = System.currentTimeMillis();
        random = new Random(seed);
    }

    // use this method which calls the constructor to create a game object
    public static Game createGame(int code, List<Player> playerList)
    {
        Map<String, Player> players = new HashMap<>();
        for (Player player : playerList)
        {
            players.put(player.getName(), player);
        }
        Game game = new Game(code, players);
        games.put(code, game);
        return game;
    }

    // called by GameController, starts the game
    public void gameStart(SimpMessageSendingOperations messagingTemplate)
    {
        gameStarted = true;
        this.messagingTemplate = messagingTemplate;
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            playerEntry.getValue().onGameStart(messagingTemplate);
        }
        messagingTemplate.convertAndSend("/topic/controller/" + codeString, new GameMessage(MessageType.GameStart));

        // send game, player, enemy information to display
        List<GameMessage> messages = new ArrayList<>();
        messages.add(new GameInfoMessage(this));
        messages.add(new GamePlayerInfoMessage(MessageType.PlayerInfo, players.values().toArray(new Player[0])));
        messages.add(new GameEnemyInfoMessage(MessageType.EnemyInfo, enemies.values().toArray(new Enemy[0])));
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            messages.add(new GameDeckMessage(MessageType.PlayerDeck, playerEntry.getValue().getName(), playerEntry.getValue().getDeck()));
        }
        messagingTemplate.convertAndSend("/topic/display/" + codeString, messages);

//        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameMessage(MessageType.GameStart));
//        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GamePlayerInfoMessage(MessageType.PlayerInfo, players.values().toArray(new Player[0])));

        // start first round
        roundStartPhase();
    }

    private void roundStartPhase()
    {
        phase = RoundPhase.RoundStart;
        roundNum++;
//        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameMessage(MessageType.RoundStart));
        messagingTemplate.convertAndSend("/topic/display/" + codeString, new GameMessage(MessageType.RoundStart));

        // deal with all players
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            playerEntry.getValue().onRoundStart();
        }

        // spawn enemies
        spawnEnemy();

        // deal with all enemies
        for (Map.Entry<Integer, Enemy> enemyEntry : enemies.entrySet())
        {
            enemyEntry.getValue().onRoundStart();
        }

        startAttackPhase();
    }

    private void spawnEnemy()
    {
        if (roundNum < 5)
        {
            Enemy newEnemy = new Orc(this, codeString, enemyCount, messagingTemplate);
            enemies.put(newEnemy.getId(), newEnemy);
            enemyCount++;
//            messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameEnemyMessage(MessageType.NewEnemy, newEnemy));
            messagingTemplate.convertAndSend("/topic/display/" + codeString, new GameEnemyMessage(MessageType.EnemyInfo, newEnemy));
        }
    }

    // players' turns
    private void startAttackPhase()
    {
        phase = RoundPhase.Attack;
//        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameMessage(MessageType.AttackPhase));
        currentPlayer = 0;
        startPlayerTurn();
    }

    private void startPlayerTurn()
    {
        Player player = getCurrentPlayer();
//        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GamePlayerMessage(MessageType.PlayerStartsTurn, player.getName()));
        messagingTemplate.convertAndSend("/topic/display/" + codeString, new GamePlayerMessage(MessageType.PlayerStartsTurn, player.getName()));
    }

    public void playerPlaysCard(Player player, PlayCardMessage message)
    {
        int[] indecks = message.getCards();
        Card[] cards = new Card[indecks.length];
        for (int i = 0; i < cards.length; i++)
        {
            // get cards
            cards[i] = player.getCardByIndecks(indecks[i]);

            // discard cards from hand
            player.discardCard(indecks[i]);
        }

        PokerHand pokerHand = Hand.identifyHand(cards);  // TODO: apply poker hand effect
        TargetingMode targetingMode = TargetingMode.values()[message.getMode()];

        // consume mana
        int manaCost = 0;
        for (Card card : cards)
        {
            manaCost += card.getRank();
        }
        player.consumeMana(manaCost);

        int attack = 0, heal = 0, mana = 0, draw = 0, revive = 0, taunt = 0;
        switch (targetingMode)
        {
            case player:
                Player targetPlayer = players.get(message.getTarget());

                // get card effect summary
                for (Card card : cards)
                {
                    heal += card.getHeal();
                    mana += card.getMana();
                    revive += card.getRevive();
                }

                // no need to apply effects if they are all zero
                if (heal == 0 && mana == 0 && revive == 0) break;

                // apply effects
                int healed = 0, manaRestored = 0, revived = 0;
                revived += targetPlayer.revive(revive);
                healed += targetPlayer.heal(heal);
                healed += revived;
                manaRestored += targetPlayer.restoreMana(mana);

                // record effect
                player.recordDamageHealed(healed);
                player.recordManaRestored(manaRestored);

                // update front end
                if (healed > 0 || manaRestored > 0)
                {
//                    messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameUpdatePlayerMessage(targetPlayer));
                }

                break;
            case enemy:
                Enemy targetEnemy = enemies.get(Integer.parseInt(message.getTarget()));

                // get card effect summary
                for (Card card : cards)
                {
                    attack += card.getAttack();
                }

                // no need to apply effects if they are all zero
                if (attack == 0) break;

                // apply effect
                int damageDealt = 0;
                damageDealt += targetEnemy.takeDamage(attack, player);

                // record effect
                player.recordDamageDealt(damageDealt);

                // update front end
                if (attack > 0)
                {
//                    messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameEnemyMessage(MessageType.EnemyUpdate, targetEnemy));
                }
                break;
            case allPlayers:
                // get card effect summary
                for (Card card : cards)
                {
                    heal += card.getHeal();
                    mana += card.getMana();
                    revive += card.getRevive();
                }

                // no need to apply effects if they are all zero
                if (heal == 0 && mana == 0 && revive == 0) break;

                // effect is halved due to aoe deduction
                heal = Math.round((float) heal / 2);
                mana = Math.round((float) mana / 2);
                revive = Math.round((float) revive / 2);

                // apply effects
                healed = 0; manaRestored = 0; revived = 0;
                for (Map.Entry<String, Player> playerEntry : players.entrySet())
                {
                    targetPlayer = playerEntry.getValue();
                    revived += targetPlayer.revive(revive);
                    healed += targetPlayer.heal(heal);
                    healed += revived;
                    manaRestored = targetPlayer.restoreMana(mana);

                    // update front end
                    if (healed > 0 || manaRestored > 0)
                    {
//                        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameUpdatePlayerMessage(targetPlayer));
                    }
                }

                // record effects
                player.recordDamageHealed(healed);
                player.recordManaRestored(manaRestored);
                break;
            case allEnemies:
                // get card effect summary
                for (Card card : cards)
                {
                    attack += card.getAttack();
                }

                // no need to apply effects if they are all zero
                if (attack == 0) break;

                // effect is halved due to aoe deduction
                attack = Math.round((float) attack / 2);

                // apply effects
                damageDealt = 0;
                for (Map.Entry<Integer, Enemy> enemyEntry : enemies.entrySet())
                {
                    targetEnemy = enemyEntry.getValue();
                    damageDealt += targetEnemy.takeDamage(attack, player);

                    // update front end
                    if (attack > 0)
                    {
//                        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameEnemyMessage(MessageType.EnemyUpdate, targetEnemy));
                    }
                }

                // record effects
                player.recordDamageDealt(damageDealt);
                break;
        }

        // taunt effect
        for (Card card : cards)
        {
            taunt += card.getTaunt();
        }
        if (taunt > 0)
        {
            player.increaseHate(taunt, "taunt");
        }

        // draw effect
        for (Card card : cards)
        {
            draw += card.getDraw();
        }
        if (draw > 0)
        {
            int[] cardsDrawn = player.drawCards(draw);
//            messagingTemplate.convertAndSendToUser(player.getName(), "/topic/game/" + codeString, new GamePlayerDrawCardMessage(cardsDrawn));
            messagingTemplate.convertAndSend("/topic/display/" + codeString, new GameIntMessage(MessageType.PlayerDrawCard, cardsDrawn.length));
        }

        // generates hate
        if (attack > 0)
        {
            player.increaseHate(2, "attack");
        }
        else if (heal > 0 || mana > 0 || revive > 0)
        {
            player.increaseHate(1, "heal");
        }

        player.updateBlock();

        // update the current player
//        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameUpdatePlayerMessage(player));
    }

    public void playerDiscardsCard(Player player, DiscardCardMessage message)
    {
        int[] indecks = message.getCards();
        Card[] cards = new Card[indecks.length];
        for (int i = 0; i < cards.length; i++)
        {
            // get cards
            cards[i] = player.getCardByIndecks(indecks[i]);

            // discard cards from hand
            player.discardCard(indecks[i]);
        }

        player.updateBlock();

        // if discarding more than replaceAllowance, replace the cards with lowest rank and restore mana based on the cards with highest rank
        if (cards.length > player.getReplaceAllowance())
        {
            cards = Hand.sortCards(cards);

            // find how much mana can be generated
            // cards with index<replaceAllowance are the cards with lowest ranks and to be replaced
            int manaGeneration = 0;
            for (int i = player.getReplaceAllowance(); i < cards.length; i++)
            {
                manaGeneration += cards[i].getRank();
            }

            // restore mana and record amount restored
            int manaRestored = player.restoreMana(manaGeneration);
            player.recordManaRestored(manaRestored);

            // update front end
            if (manaRestored > 0)
            {
//                messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameUpdatePlayerMessage(player));
            }

            // replace with new cards
            if (player.getReplaceAllowance() > 0)
            {
                int[] cardsDrawn = player.drawCards(player.getReplaceAllowance());
//                messagingTemplate.convertAndSendToUser(player.getName(), "/topic/game/" + codeString, new GamePlayerDrawCardMessage(cardsDrawn));
                messagingTemplate.convertAndSend("/topic/display/" + codeString, new GameIntMessage(MessageType.PlayerDrawCard, cardsDrawn.length));
                player.setReplaceAllowance(0);
            }
        }
        else
        {
            // replace with new cards
            int[] cardsDrawn = player.drawCards(cards.length);
//            messagingTemplate.convertAndSendToUser(player.getName(), "/topic/game/" + codeString, new GamePlayerDrawCardMessage(cardsDrawn));
            messagingTemplate.convertAndSend("/topic/display/" + codeString, new GameIntMessage(MessageType.PlayerDrawCard, cardsDrawn.length));
            player.setReplaceAllowance(player.getReplaceAllowance() - cards.length);
        }
    }

    public void finishPlayerTurn()
    {
        currentPlayer++;
//        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameMessage(MessageType.PlayerEndsTurn));
        messagingTemplate.convertAndSend("/topic/display/" + codeString, new GameMessage(MessageType.PlayerEndsTurn));

        // if all players have finished their turns
        if (currentPlayer == playerOrder.length)
        {
            startDefendPhase();
        }
        else
        {
            startPlayerTurn();
        }
    }

    // enemies' turns
    private void startDefendPhase()
    {
        phase = RoundPhase.Defend;
//        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameMessage(MessageType.DefendPhase));

        List<GameMessage> messages = new ArrayList<>();
        for (Map.Entry<Integer, Enemy> enemyEntry : enemies.entrySet())
        {
            if (!enemyEntry.getValue().isDead())
            {
                messages.addAll(enemyEntry.getValue().onTurnStart());
            }
        }
        // combine all enemy actions into a single message and send
//        messagingTemplate.convertAndSend("/topic/game/" + codeString, messages);

        roundEndPhase();
    }

    private void roundEndPhase()
    {
        phase = RoundPhase.RoundEnd;
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            // make sure front end is in sync
//            messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameUpdatePlayerMessage(playerEntry.getValue()));
        }

        roundStartPhase();
    }

    public Player findPlayerWithMostHate()
    {
        Player result = null;
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            if (result == null)
            {
                result = playerEntry.getValue();
            }
            else
            {
                if (playerEntry.getValue().getHate() > result.getHate() && !playerEntry.getValue().isDown())
                {
                    result = playerEntry.getValue();
                }
                else if (playerEntry.getValue().getHate() == result.getHate() && !playerEntry.getValue().isDown())
                {
                    result = random.nextInt() < 0 ? playerEntry.getValue() : result;
                }
            }
        }
        return result;
    }

    @JsonIgnore
    public static Game getGameByCode(int code)
    {
        return games.getOrDefault(code, null);
    }

    @JsonIgnore
    public static Game getGameByCode(String code)
    {
        return getGameByCode(GameCodeGenerator.stringToInt(code));
    }

    @JsonIgnore
    public Map<String, Player> getPlayers()
    {
        return players;
    }

    @JsonIgnore
    public Map<Integer, Enemy> getEnemies()
    {
        return enemies;
    }

    public static boolean gameCodeExist(int code)
    {
        return games.containsKey(code);
    }

    public static boolean gameCodeExist(String code) { return games.containsKey(GameCodeGenerator.stringToInt(code)); }

    public boolean isPlayerInThisGame(String name)
    {
        return players.containsKey(name);
    }

    public boolean isEnemyTargetable(int id)
    {
        return enemies.containsKey(id);
    }

    public void setPlayerOnlineStatus(String name, boolean status)
    {
        playerOnlineStatuses.put(players.get(name), status);
    }

    public boolean areAllPlayersConnected()
    {
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            if (!playerOnlineStatuses.containsKey(playerEntry.getValue()) || !playerOnlineStatuses.get(playerEntry.getValue()))
            {
                return false;
            }
        }
        return true;
    }

    @JsonIgnore
    public Display getDisplay()
    {
        return display;
    }

    @JsonIgnore
    public boolean isDisplayConnected()
    {
        return isDisplayConnected;
    }

    public void setDisplayConnected()
    {
        isDisplayConnected = true;
    }

    public boolean isGameStarted()
    {
        return gameStarted;
    }

    public RoundPhase getPhase()
    {
        return phase;
    }

    @JsonIgnore
    public Player getCurrentPlayer()
    {
        return players.get(playerOrder[currentPlayer]);
    }

    @JsonIgnore
    public long getSeed()
    {
        return seed;
    }

    public String toString()
    {
        return "Game Session " + GameCodeGenerator.intToString(code) + " - " + code;
    }
}
