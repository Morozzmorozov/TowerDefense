package ru.nsu.fit.towerdefense.server.lobby;

import com.google.gson.Gson;
import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.multiplayer.Message;
import ru.nsu.fit.towerdefense.multiplayer.MessageType;
import ru.nsu.fit.towerdefense.server.sockets.UserConnection;
import ru.nsu.fit.towerdefense.server.sockets.receivers.MessageReceiver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class LobbyControl
{

    public enum State {
        PREGAME,
        INGAME,
        POSTGAME
    }

    private static final int AWAITINGLIMIT = 10;
    private static final int TIMEOUT = 30000 * 100;
    private final Lobby lobby;
    private final HashSet<String> joinedTokens;
    private final HashMap<String, String> tokenToUsername;
    private final HashSet<String> awaitingTokens;

    private final HashMap<MessageReceiver, String> userToToken;
    private final HashMap<String, MessageReceiver> tokenToUser;

    private Thread gameThread;
    private HashSet<String> readyUsers;
    private State currentState;


    public LobbyControl(long id)
    {
        lobby = new Lobby(id);
        joinedTokens = new HashSet<>();
        awaitingTokens = new HashSet<>();
        userToToken = new HashMap<>();
        tokenToUser = new HashMap<>();
        tokenToUsername = new HashMap<>();
        readyUsers = new HashSet<>();
        currentState = State.PREGAME;
        this.setLevel("Level 1_4");
        gameThread = new Thread(() -> {
            try
            {
                Thread.sleep(TIMEOUT);
            }
            catch (Exception e) {}
            checkIfEmpty();
        });
        gameThread.start();
    }


    public ru.nsu.fit.towerdefense.multiplayer.entities.Lobby serialize()
    {
        ru.nsu.fit.towerdefense.multiplayer.entities.Lobby lobby = new ru.nsu.fit.towerdefense.multiplayer.entities.Lobby();
        lobby.setId(Long.toString(getId()));
        lobby.setLevelName(getLevelName());
        lobby.setMaxPlayers(getPlayersNumber());
        lobby.setPlayers(getPlayers());
        return lobby;
    }



    public List<String> getPlayers()
    {
        List<String> result = new LinkedList<>();
        synchronized (tokenToUsername)
        {
            tokenToUsername.forEach((k, v) -> result.add(v));
        }
        return result;
    }


    public int getPlayersNumber() {
        return lobby.getPlayersNumber();
    }

    public long getId()
    {
        return lobby.getId();
    }

    public String getLevelName()
    {
        return lobby.getLevelName();
    }

    private void setLevel(String levelName)
    {
        GameMap level = GameMetaData.getInstance().getMapDescription(levelName);
        lobby.setLevelName(levelName);
        lobby.setPlayersNumber(level.getPlayersNumber());

        var iter = joinedTokens.iterator();
        while (joinedTokens.size() > lobby.getPlayersNumber())
        {
            iter.remove();
        }
    }

    public synchronized boolean userLeaves(String token)
    {
        awaitingTokens.remove(token);
        joinedTokens.remove(token);
        return joinedTokens.size() == 0;
    }

    public synchronized boolean addAwaitingToken(String token){
        if (awaitingTokens.size() < AWAITINGLIMIT)
        {
            awaitingTokens.add(token);
            return true;
        }
        return false;
    }

    public boolean canJoin() {
        return joinedTokens.size() < lobby.getPlayersNumber();
    }

    public int getJoined(){
        return joinedTokens.size();
    }

    public void connectedUserLeaves(MessageReceiver receiver)
    {
        String token = userToToken.get(receiver);

        userToToken.remove(receiver, token);
        tokenToUser.remove(token, receiver);
    }

    public void switchReady(MessageReceiver receiver) {
        if (currentState == State.PREGAME)
        {
            String token = userToToken.get(receiver);
            if (readyUsers.contains(token))
            {
                readyUsers.remove(token);
            }
            else
            {
                readyUsers.add(token);
                if (readyUsers.size() == getPlayersNumber())
                {
                    Message message = new Message();
                    message.setType(MessageType.START);
                    sendMessageToClients(new Gson().toJson(message));
                    gameThread = new Thread(this::gameRun);
                    currentState = State.INGAME;
                    gameThread.start();
                }
            }
        }
    }

    public void gameRun()
    {
        Message message = new Message();
        message.setType(MessageType.STATE);
        message.setMessage("Game is running, ping!");
        String res = new Gson().toJson(message);
        while (true)
        {
            try
            {
                Thread.sleep(500);
            }
            catch (Exception e){}

            sendMessageToClients(res);
        }
    }


    private void checkIfEmpty()
    {
        if (joinedTokens.size() == 0)
        {
            LobbyManager.getInstance().removeLobby(this);
        }
    }

    public boolean isTokenValid(String token)
    {
        synchronized (this)
        {
            return awaitingTokens.contains(token) || joinedTokens.contains(token);
        }
    }

    public boolean addMessageReceiver(String token, MessageReceiver receiver)
    {
        synchronized (this)
        {
            if (joinedTokens.contains(token))
            {
                if (tokenToUser.containsKey(token))
                {
                    System.err.println("Connection already exists");
                    return false;
                }
                System.err.println("Connected!");
                tokenToUser.put(token, receiver);
                userToToken.put(receiver, token);
//                gameThread.interrupt();
//                gameThread = new Thread(this::notifyConnections);
//                gameThread.start();
                return true;
            }
            else if (awaitingTokens.contains(token))
            {
                if (joinedTokens.size() < getPlayersNumber())
                {
                    awaitingTokens.remove(token);
                    joinedTokens.add(token);
                    tokenToUser.put(token, receiver);
                    userToToken.put(receiver, token);
                    System.err.println("Connected!");
//                    gameThread.interrupt();
//                    gameThread = new Thread(this::notifyConnections);
//                    gameThread.start();
                    return true;
                }
                System.err.println("Lobby is full");
            }
            return false;
        }
    }

//    private void notifyConnections()
//    {
//        while (true)
//        {
//            System.out.println("notifyConnections");
//            synchronized (userToToken)
//            {
//
//            }
//            try
//            {
//                Thread.sleep(500);
//            }
//            catch (Exception e){}
//        }
//    }

    public void sendMessageToClients(String message)
    {
        for (var x : userToToken.entrySet())
        {
            try
            {
                x.getKey().sendMessage(message);
            }
            catch (Exception e){}
        }
    }


}
