package com.fengzhixuan.timoc.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fengzhixuan.timoc.game.enemies.Orc;
import com.fengzhixuan.timoc.game.enums.PokerHand;
import com.fengzhixuan.timoc.game.enums.RoundPhase;
import com.fengzhixuan.timoc.websocket.message.game.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.*;

public class Game
{
    private MessageSender messageSender;

    private static Map<Integer, Game> games = new HashMap<>();

    private int code;
    private String codeString;
    private Map<String, Player> players = new HashMap<>(); // username is key
    private Map<Player, Boolean> playerOnlineStatuses = new HashMap<>(); // true means online/connected
    private Display display;  // emulates the state of displays
    private boolean isDisplayConnected = false;  // is there a connected display?
    private RoundPhase phase;
    private String[] playerOrder;  // stores all players' names, represents the order of players(who's player1, who plays first) in attack phase, is also ids of the players
    private int currentPlayer;  // id of current player / current index to playerOrder array, represents whose turn it is now
    private Map<Integer, Enemy> enemies = new HashMap<>();  // mapped by id
    private Enemy[] aliveEnemies = new Enemy[4];  // stores enemies by position (0-3)
    private int enemyCount;  // increments each time an enemy is spawn, then is given to the spawned enemy as id
    private boolean gameStarted;  // whether the game has started
    private boolean gameOver = false;
    private int roundNum;  // current round number
    private Random random;  // random number generator
    private long seed;  // seed of the random

    private Game(int code, Map<String, Player> players)
    {
        this.code = code;
        codeString = GameCodeGenerator.intToString(code);
        display = new Display(this);
        this.players = players;

        playerOrder = new String[players.size()];
        int count = 0;
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            Player player = playerEntry.getValue();
            // fill in the status map, initialize to all false
            playerOnlineStatuses.put(player, false);
            // fill in the playerOrder array with all the names
            playerOrder[count] = player.getName();
            player.setId(count);
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
        messageSender = new MessageSender(messagingTemplate, codeString);
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            playerEntry.getValue().onGameStart(this);
        }
        addControllerMessage(new GameMessage(MessageType.GameStart));

        // send game, player, enemy information to display
        addDisplayMessage(new GameInfoMessage(this));
        addDisplayMessage(new GamePlayerInfoMessage(MessageType.PlayerInfo, players.values().toArray(new Player[0])));
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            addDisplayMessage(new GameDeckMessage(MessageType.PlayerDeck, playerEntry.getValue().getId(), playerEntry.getValue().getDeck()));
        }

        flushMessages();

        // start first round
        roundStartPhase();
    }

    private void roundStartPhase()
    {
        phase = RoundPhase.RoundStart;
        roundNum++;
        addDisplayMessage(new GameMessage(MessageType.RoundStart));

        // deal with all players
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            Player player = playerEntry.getValue();
            player.onRoundStart();
        }

        // spawn enemies
        spawnEnemy();

        // deal with all enemies
        for (Enemy enemy : aliveEnemies)
        {
            if (enemy != null)
                enemy.onRoundStart();
        }

        addDisplayMessage(new GamePlayerInfoMessage(MessageType.PlayerUpdateAll, players.values().toArray(new Player[0])));
        addDisplayMessage(new GameEnemyInfoMessage(MessageType.EnemyUpdateAll, getAliveEnemiesWithoutNulls()));

        flushMessages();

        if (!gameOver) startAttackPhase();
    }

    private void spawnEnemy()
    {
        if (roundNum < 5)
        {
            int positionId;
            for (positionId = 0; aliveEnemies[positionId] != null && positionId < aliveEnemies.length; positionId++);
            Enemy newEnemy = new Orc(this, enemyCount, positionId);
            enemies.put(newEnemy.getId(), newEnemy);
            enemyCount++;
            aliveEnemies[positionId] = newEnemy;
            addDisplayMessage(new GameEnemySpawnMessage(MessageType.EnemyInfo, newEnemy));
        }
    }

    // players' turns
    private void startAttackPhase()
    {
        phase = RoundPhase.Attack;
        currentPlayer = 0;
        if (!gameOver) startPlayerTurn();
    }

    private void startPlayerTurn()
    {
        phase = RoundPhase.PlayerTurnStart;

        // find the first player who's not dead
        Player player = getCurrentPlayer();
        while (player.isDown() && currentPlayer < playerOrder.length-1){
            currentPlayer++;
            player = getCurrentPlayer();
        }

        if (player.isDown())
        {
            startDefendPhase();
        }
        else if (!gameOver)
        {
            display.reset(player.getDrawNum(), playerOrder.length, aliveEnemies.length);
            player.onTurnStart();  // sends PlayerStartsTurn message and PlayerDeck message

            flushMessages();
            phase = RoundPhase.PlayerTurn;
        }
    }

    public void playerPlaysCard(Player player, TotalSelectedEffects totalSelectedEffects)
    {
        Card[] cards = totalSelectedEffects.getSelectedCards();
        player.removeCards(cards);

        // consume mana
        player.consumeMana(totalSelectedEffects.getManaCost());

        int attack, heal, mana, draw, revive, bonus;
        int healed = 0, manaRestored = 0, revived = 0, damageDealt = 0;
        PokerHand pokerHand = totalSelectedEffects.getPokerHand();
        switch (totalSelectedEffects.getTargetingMode())
        {
            case Self:
                // send play card information for front end to animate
                addDisplayMessage(new GamePlayerPlayCardMessage(MessageType.PlayerPlaysCard_Player, player.getId(), player.getId(), cards));

            case Player:
                Player targetPlayer = getPlayerByPosition(totalSelectedEffects.getTargetPosition());

                // send play card information for front end to animate
                addDisplayMessage(new GamePlayerPlayCardMessage(MessageType.PlayerPlaysCard_Player, player.getId(), targetPlayer.getId(), cards));

                // get card effect summary
                heal = totalSelectedEffects.getHeal();
                mana = totalSelectedEffects.getMana();
                revive = totalSelectedEffects.getRevive();

                // no need to apply effects if they are all zero
                if (heal == 0 && mana == 0 && revive == 0) break;

                // apply INT bonus
                bonus = player.getINT();
                heal += bonus;
                mana += bonus;
                revive += bonus;

                // apply effects
                revived += targetPlayer.revive(revive);
                healed += targetPlayer.heal(heal);
                healed += revived;
                manaRestored += targetPlayer.restoreMana(mana);

                // record effect
                player.recordDamageHealed(healed);
                player.recordManaRestored(manaRestored);

                break;

            case Enemy:
                Enemy targetEnemy = getEnemyByPosition(totalSelectedEffects.getTargetPosition());

                // send play card information for front end to animate
                addDisplayMessage(new GamePlayerPlayCardMessage(MessageType.PlayerPlaysCard_Enemy, player.getId(), targetEnemy.getId(), cards));

                // get card effect summary
                attack = totalSelectedEffects.getAttack();

                // no need to apply effects if they are all zero
                if (attack == 0) break;

                // apply STR bonus
                attack += player.getSTR();

                // apply effect
                damageDealt += targetEnemy.takeDamage(attack, player);

                // record effect
                player.recordDamageDealt(damageDealt);

                break;

            case AllPlayers:
                targetPlayer = getPlayerByPosition(totalSelectedEffects.getTargetPosition());
                // send play card information for front end to animate
                addDisplayMessage(new GamePlayerPlayCardMessage(MessageType.PlayerPlaysCard_Player, player.getId(), targetPlayer.getId(), cards));

                // get card effect summary
                heal = totalSelectedEffects.getHeal();
                mana = totalSelectedEffects.getMana();
                revive = totalSelectedEffects.getRevive();

                // no need to apply effects if they are all zero
                if (heal == 0 && mana == 0 && revive == 0) break;

                // apply INT bonus
                bonus = Math.round((float) player.getINT() / 2);
                heal += bonus;
                mana += bonus;
                revive += bonus;

                // apply effects
                for (Map.Entry<String, Player> playerEntry : players.entrySet())
                {
                    targetPlayer = playerEntry.getValue();
                    revived += targetPlayer.revive(revive);
                    healed += targetPlayer.heal(heal);
                    healed += revived;
                    manaRestored = targetPlayer.restoreMana(mana);
                }

                // record effects
                player.recordDamageHealed(healed);
                player.recordManaRestored(manaRestored);
                break;

            case AllEnemies:
                targetEnemy = getEnemyByPosition(totalSelectedEffects.getTargetPosition());
                // send play card information for front end to animate
                addDisplayMessage(new GamePlayerPlayCardMessage(MessageType.PlayerPlaysCard_Enemy, player.getId(), targetEnemy.getId(), cards));

                // get card effect summary
                attack = totalSelectedEffects.getAttack();

                // no need to apply effects if they are all zero
                if (attack == 0) break;

                // apply STR bonus
                attack += Math.round((float) player.getSTR() / 2);

                // apply effects
                for (Enemy enemy : aliveEnemies)
                {
                    if (enemy != null) damageDealt += enemy.takeDamage(attack, player);
                }

                // record effects
                player.recordDamageDealt(damageDealt);
                break;
        }

        // block
        if (totalSelectedEffects.getBlock() > 0)
        {
            player.increaseBlock(totalSelectedEffects.getBlock() + player.getVIT());
        }

        // taunt effect
        if (totalSelectedEffects.getTaunt() > 0)
        {
            player.increaseHate(totalSelectedEffects.getTaunt(), "taunt");
        }

        // draw effect
        if (totalSelectedEffects.getDraw() > 0)
        {
            player.drawCards(totalSelectedEffects.getDraw());
        }

        // generates hate
        if (damageDealt > 0)
        {
            player.increaseHate(2, "attack");
        }
        else if (healed > 0 || manaRestored > 0)
        {
            player.increaseHate(1, "heal");
        }

        // if a flush is played, player gain STR, VIT or INT
        int statIncrease = 0;
        if (pokerHand == PokerHand.Flush) statIncrease = 1;
        else if (pokerHand == PokerHand.StraightFlush) statIncrease = 2;
        else if (pokerHand == pokerHand.RoyalFlush) statIncrease = 3;
        if (statIncrease > 0)
        {
            switch (cards[0].getSuit())
            {
                case 0:  // diamond flush increases STR
                    player.increaseSTR(statIncrease);
                    break;
                case 1:  // club flush increases STR or VIT
                    if (random.nextInt(2) == 0)
                        player.increaseSTR(statIncrease);
                    else
                        player.increaseVIT(statIncrease);
                    break;
                case 2:  // heart flush increases INT
                    player.increaseINT(statIncrease);
                    break;
                case 3:  // spade flush increases VIT
                    player.increaseVIT(statIncrease);
                    break;
            }
        }

        // update display states
        display.reset(player.getHandPile().size());
        sendDisplayStates();

        flushMessages();
    }

    public void playerDiscardsCards(Player player, Card[] cards)
    {
        // remove cards from hand
        player.removeCards(cards);

        // find how much mana can be generated
        int manaGeneration = 0;
        for (Card card : cards)
        {
            manaGeneration += card.getRank();
        }

        // restore mana and record amount restored
        int manaRestored = player.restoreMana(manaGeneration);
        player.recordManaRestored(manaRestored);

        // update front end
        display.reset(player.getHandPile().size());
        sendDisplayStates();

        flushMessages();
    }

    public void playerReplacesCards(Player player, Card[] cards)
    {
        // remove cards from hand
        player.removeCards(cards);

        // replace with new cards
        player.drawCards(cards.length);
        player.setReplaceAllowance(player.getReplaceAllowance() - cards.length);

        // update front end
        display.reset(player.getHandPile().size());
        sendDisplayStates();

        flushMessages();
    }

    public void finishPlayerTurn()
    {
        currentPlayer++;
        addDisplayMessage(new GameMessage(MessageType.PlayerEndsTurn));

        flushMessages();

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

        for (Enemy enemy : aliveEnemies)
        {
            if (enemy != null && !enemy.isDead())
            {
                enemy.onTurnStart();
            }
        }

        flushMessages();

        if (!gameOver) roundEndPhase();
    }

    private void roundEndPhase()
    {
        phase = RoundPhase.RoundEnd;

        // remove dead enemies
        for (int i = 0; i < aliveEnemies.length; i++)
        {
            if (aliveEnemies[i] != null && aliveEnemies[i].isDead())
            {
                aliveEnemies[i] = null;
                addDisplayMessage(new GameEnemyMessage(MessageType.RemoveEnemy, i));
            }
        }

        flushMessages();

        if (!gameOver) roundStartPhase();
    }

    public void defeat()
    {
        gameOver = true;
        display.pauseControl();
        addDisplayMessage(new GameMessage(MessageType.GameOverDefeat));
        addControllerMessage(new GameMessage(MessageType.GameOverDefeat));

//        // reward
//        for (Map.Entry<String, Player> playerEntry : players.entrySet())
//        {
//            Player player = playerEntry.getValue();
//            messageSender.addTargetedControllerMessage(new GameRewardMessage(null, player.getGoldRewards()), player.getName());
//        }

        flushMessages();
    }

    public void victory()
    {
        gameOver = true;
        display.pauseControl();
        addDisplayMessage(new GameMessage(MessageType.GameOverVictory));
        addControllerMessage(new GameMessage(MessageType.GameOverDefeat));

        flushMessages();
    }

    public void addGoldRewardAllPlayers(int gold)
    {
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            playerEntry.getValue().addGoldReward(gold);
        }
    }

    public void sendDisplayStates()
    {
        addDisplayMessage(new DisplayStateMessage(display.toInteger()));
    }

    public Player findPlayerWithMostHate()
    {
        Player result = null;
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            Player player = playerEntry.getValue();
            if (result == null)
            {
                result = player;
            }
            else if (!player.isDown())
            {
                if (player.getHate() > result.getHate())
                {
                    result = player;
                }
                else if (player.getHate() == result.getHate())
                {
                    result = random.nextInt() < 0 ? player : result;
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

    public void addDisplayMessage(GameMessage message)
    {
        if (messageSender != null) messageSender.addDisplayMessage(message);
    }

    public void addControllerMessage(GameMessage message)
    {
        if (messageSender != null) messageSender.addControllerMessage(message);
    }

    public void flushMessages()
    {
        if (messageSender != null) messageSender.flush();
    }

    @JsonIgnore
    public Map<String, Player> getPlayers()
    {
        return players;
    }

    @JsonIgnore
    public Player getPlayerByPosition(int position)
    {
        return players.get(playerOrder[position]);
    }

    public boolean areAllPlayersDown()
    {
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            if (!playerEntry.getValue().isDown()) return false;
        }
        return true;
    }

    @JsonIgnore
    public Map<Integer, Enemy> getEnemies()
    {
        return enemies;
    }

    @JsonIgnore
    public Enemy[] getAliveEnemies()
    {
        return aliveEnemies;
    }

    @JsonIgnore
    public Enemy[] getAliveEnemiesWithoutNulls()
    {
        int count = 0;
        for (Enemy enemy : aliveEnemies) if (enemy != null) count++;
        Enemy[] enemies = new Enemy[count];
        count = 0;
        for (int i = 0; i < aliveEnemies.length; i++)
        {
            if (aliveEnemies[i] != null)
            {
                enemies[count] = aliveEnemies[i];
                count++;
            }
        }
        return enemies;
    }

    @JsonIgnore
    public Enemy getEnemyByPosition(int position)
    {
        return aliveEnemies[position];
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

    public boolean isAnyPlayerConnected()
    {
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            if (playerOnlineStatuses.containsKey(playerEntry.getValue()) && playerOnlineStatuses.get(playerEntry.getValue()))
            {
                return true;
            }
        }
        return false;
    }

    public static void removeGame(int code)
    {
        Game game = games.remove(code);
        if (game == null) return;
        for (String playerName : game.playerOrder)
        {
            Player.removePlayer(playerName);
        }
        game.addDisplayMessage(new GameMessage(MessageType.DisconnectDisplay));
    }

    public Integer processControllerInput(int buttonCode)
    {
        Integer output = display.controllerInput(buttonCode);
        flushMessages();
        return output;
    }

    @JsonIgnore
    public Integer getDisplayStates()
    {
        return display.toInteger();
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

    public boolean isGameOver()
    {

        return gameOver;
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
    public Player getPlayerById(int id)
    {
        return players.get(playerOrder[id]);
    }

    @JsonIgnore
    public long getSeed()
    {
        return seed;
    }

    public int nextInt(int bound)
    {
        return random.nextInt(bound);
    }

    public String toString()
    {
        return "Game Session " + GameCodeGenerator.intToString(code) + " - " + code;
    }
}
