package com.fengzhixuan.timoc.game;

import com.fengzhixuan.timoc.game.enums.PokerHand;
import com.fengzhixuan.timoc.game.enums.TargetingMode;
import com.fengzhixuan.timoc.webcontroller.messagetemplate.PlayCardMessage;
import com.fengzhixuan.timoc.websocket.message.game.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game
{
    // value passed by GameController via gameStart(), used for sending messages to players
    private SimpMessageSendingOperations messagingTemplate;

    private static Map<Integer, Game> games = new HashMap<>();

    private int code;
    private String codeString;
    private Map<String, Player> players = new HashMap<>(); // username is key
    private Map<Player, Boolean> playerOnlineStatuses = new HashMap<>(); // true means online/connected
    private String[] playerOrder;  // stores all players' names, represents the order of players(who's player1, who plays first) in attack phase
    private int currentPlayer;  // current index to playerOrder array, represents whose turn it is now
    private Map<Integer, Enemy> enemies = new HashMap<>();
    private int enemyCount;  // increments each time an enemy is spawn, then is given to the spawned enemy as id
    private boolean gameStarted;  // whether the game has started
    private int roundNum;  // current round number

    private Game(int code, Map<String, Player> players)
    {
        this.code = code;
        codeString = GameCodeGenerator.intToString(code);
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
        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameMessage(MessageType.GameStart));
        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GamePlayerInfoMessage(players.values().toArray(new Player[0])));

        // start first round
        roundStartPhase();
    }

    private void roundStartPhase()
    {
        roundNum++;
        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameMessage(MessageType.RoundStart));

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

    // players' turns
    private void startAttackPhase()
    {
        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameMessage(MessageType.AttackPhase));
        currentPlayer = 0;
        startPlayerTurn();
    }

    private void startPlayerTurn()
    {
        Player player = getCurrentPlayer();
        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GamePlayerMessage(MessageType.PlayerStartsTurn, player.getName()));
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
        Player cardPlayer = getCurrentPlayer();

        // consume mana
        int manaCost = 0;
        for (Card card : cards)
        {
            manaCost += card.getRank();
        }
        cardPlayer.consumeMana(manaCost);

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
                int healed = 0, manaRestored = 0;
                healed += targetPlayer.revive(revive);
                healed += targetPlayer.heal(heal);
                manaRestored += targetPlayer.restoreMana(mana);

                // record effect
                cardPlayer.recordDamageHealed(healed);
                cardPlayer.recordManaRestored(manaRestored);

                // update front end
                messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameUpdatePlayerMessage(targetPlayer));
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
                damageDealt += targetEnemy.takeDamage(attack, cardPlayer);

                // record effect
                cardPlayer.recordDamageDealt(damageDealt);

                // update front end
                messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameEnemyMessage(MessageType.EnemyUpdate, targetEnemy));
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
                healed = 0; manaRestored = 0;
                for (Map.Entry<String, Player> playerEntry : players.entrySet())
                {
                    targetPlayer = playerEntry.getValue();
                    healed += targetPlayer.revive(revive);
                    healed += targetPlayer.heal(heal);
                    manaRestored = targetPlayer.restoreMana(mana);

                    // update front end
                    messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameUpdatePlayerMessage(targetPlayer));
                }

                // record effects
                cardPlayer.recordDamageHealed(healed);
                cardPlayer.recordManaRestored(manaRestored);
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
                    damageDealt += targetEnemy.takeDamage(attack, cardPlayer);

                    // update front end
                    messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameEnemyMessage(MessageType.EnemyUpdate, targetEnemy));
                }

                // record effects
                cardPlayer.recordDamageDealt(damageDealt);
                break;
        }

        // taunt effect
        for (Card card : cards)
        {
            taunt += card.getTaunt();
        }
        if (taunt > 0)
        {
            cardPlayer.increaseHate(taunt, "taunt");
        }

        // draw effect
        for (Card card : cards)
        {
            draw += card.getDraw();
        }
        if (draw > 0)
        {
            Card[] cardsDrawn = cardPlayer.drawCards(draw);
            messagingTemplate.convertAndSendToUser(cardPlayer.getName(), "/topic/game/" + codeString, new GamePlayerDrawCardMessage(cardsDrawn));
        }

        // generates hate
        if (attack > 0)
        {
            cardPlayer.increaseHate(2, "attack");
        }
        else if (heal > 0 || mana > 0 || revive > 0)
        {
            cardPlayer.increaseHate(1, "heal");
        }

        // update the current player
        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameUpdatePlayerMessage(cardPlayer));
    }

    public void finishPlayerTurn()
    {
        currentPlayer++;
        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameMessage(MessageType.PlayerEndsTurn));

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
        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameMessage(MessageType.DefendPhase));
        for (Map.Entry<Integer, Enemy> enemyEntry : enemies.entrySet())
        {
            // enemy action
        }
        // combine all enemy actions into a single message and send

        roundEndPhase();
    }

    private void roundEndPhase()
    {
        Player player = players.get("haha");
        player.setHp(player.getHp() - 10);
        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameUpdatePlayerMessage(player));
        roundStartPhase();
    }

    private void spawnEnemy()
    {
        if (roundNum == 1)
        {
            Enemy newEnemy = new Enemy(codeString, "goblin", enemyCount, messagingTemplate);
            enemies.put(newEnemy.getId(), newEnemy);
            enemyCount++;
            messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameEnemyMessage(MessageType.NewEnemy, newEnemy));
        }
    }




    public static Game getGameByCode(int code)
    {
        return games.getOrDefault(code, null);
    }

    public static Game getGameByCode(String code)
    {
        return getGameByCode(GameCodeGenerator.stringToInt(code));
    }

    public static boolean gameCodeExist(int code)
    {
        return games.containsKey(code);
    }

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

    public boolean isGameStarted()
    {
        return gameStarted;
    }

    public Player getCurrentPlayer()
    {
        return players.get(playerOrder[currentPlayer]);
    }

    public String toString()
    {
        return "Game Session " + GameCodeGenerator.intToString(code) + " - " + code;
    }
}
