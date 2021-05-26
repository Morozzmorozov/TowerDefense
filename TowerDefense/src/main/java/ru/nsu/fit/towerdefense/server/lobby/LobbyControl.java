package ru.nsu.fit.towerdefense.server.lobby;

import com.google.gson.Gson;
import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.multiplayer.Message;
import ru.nsu.fit.towerdefense.server.database.UserManager;
import ru.nsu.fit.towerdefense.server.sockets.receivers.MessageReceiver;
import ru.nsu.fit.towerdefense.simulator.ServerSimulator;
import ru.nsu.fit.towerdefense.simulator.WorldObserver;
import ru.nsu.fit.towerdefense.simulator.events.Event;

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
    private final HashSet<String> awaitingTokens;

    private final HashMap<MessageReceiver, String> userToToken;
    private final HashMap<String, MessageReceiver> tokenToUser;

    private final HashMap<String, String> tokenToName;
    private final HashMap<String, String> nameToToken;


    private Thread gameThread;
    private HashSet<String> readyUsers;
    private State currentState;


    private WorldObserver observer;
    private ServerSimulator simulator;

    public LobbyControl(long id, String levelName)
    {
        lobby = new Lobby(id);
        joinedTokens = new HashSet<>();
        awaitingTokens = new HashSet<>();
        userToToken = new HashMap<>();
        tokenToUser = new HashMap<>();

        nameToToken = new HashMap<>();
        tokenToName = new HashMap<>();

//        tokenToId = new HashMap<>();
//        idToToken = new HashMap<>();

        readyUsers = new HashSet<>();
        currentState = State.PREGAME;
//        this.setLevel("Level 1_4");
        this.setLevel(levelName);
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
        synchronized (tokenToName)
        {
            tokenToName.forEach((k, v) -> result.add(UserManager.getInstance().getNameByToken(v)));
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

    public boolean userLeaves(String token, String userToken)
    {
        synchronized (this)
        {
            tokenToName.remove(token, userToken);
            nameToToken.remove(userToken, token);
            awaitingTokens.remove(token);
            joinedTokens.remove(token);
            return joinedTokens.size() == 0;
        }
    }

    public boolean addAwaitingToken(String token, String userToken){
        synchronized (this)
        {
            if (nameToToken.containsKey(userToken)) return false;
            if (awaitingTokens.size() < AWAITINGLIMIT)
            {
                awaitingTokens.add(token);
                nameToToken.put(userToken, token);
                tokenToName.put(token, userToken);
                return true;
            }
            return false;
        }
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
                    message.setType(Message.Type.START);
                    message.setPlayerNames(getPlayers());
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
                    return true;
                }
                System.err.println("Lobby is full");
            }
            return false;
        }
    }

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

    public void sendEvent(String message)
    {
        if (currentState == State.INGAME)
        {
            Event event = new Gson().fromJson(message, Event.class);
            simulator.submitEvent(event);
        }
    }

    public void setPlatform(int id)
    {
        if (currentState == State.PREGAME)
        {
//            if (idToToken)
        }
    }
}
