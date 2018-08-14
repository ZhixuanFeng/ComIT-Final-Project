package com.fengzhixuan.timoc.game;

import com.fengzhixuan.timoc.websocket.message.game.GameEnemyMessage;
import com.fengzhixuan.timoc.websocket.message.game.GameMessage;
import com.fengzhixuan.timoc.websocket.message.game.GamePlayerMessage;
import com.fengzhixuan.timoc.websocket.message.game.MessageType;
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
    private List<Enemy> enemies = new ArrayList<>();
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
        for (Enemy enemy : enemies)
        {
            enemy.onRoundStart();
        }

        startAttackPhase();
    }

    // players' turns
    private void startAttackPhase()
    {
        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameMessage(MessageType.AttackPhase));
        currentPlayer = 0;
        starterPlayerTurn();
    }

    private void starterPlayerTurn()
    {
        Player player = players.get(playerOrder[currentPlayer]);
        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GamePlayerMessage(MessageType.PlayerStartsTurn, player.getName()));
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
            starterPlayerTurn();
        }
    }

    // enemies' turns
    private void startDefendPhase()
    {
        messagingTemplate.convertAndSend("/topic/game/" + codeString, new GameMessage(MessageType.DefendPhase));
        for (Enemy enemy : enemies)
        {
            // enemy action
        }
        // combine all enemy actions into a single message and send

        roundEndPhase();
    }

    private void roundEndPhase()
    {
        roundStartPhase();
    }

    private void spawnEnemy()
    {
        if (roundNum == 1)
        {
            Enemy newEnemy = new Enemy(codeString, "goblin", enemyCount, messagingTemplate);
            enemies.add(newEnemy);
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

    public String toString()
    {
        return "Game Session " + GameCodeGenerator.intToString(code) + " - " + code;
    }
}
