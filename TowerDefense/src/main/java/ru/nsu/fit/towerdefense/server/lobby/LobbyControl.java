package ru.nsu.fit.towerdefense.server.lobby;

import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;

import java.util.HashSet;
import java.util.List;

public class LobbyControl
{

    private static final int AWAITINGLIMIT = 10;
    private Lobby lobby;
    private HashSet<String> joinedTokens;
    private HashSet<String> awaitingTokens;

    public LobbyControl(long id)
    {
        lobby = new Lobby(id);
        joinedTokens = new HashSet<>();
        awaitingTokens = new HashSet<>();
        this.setLevel("Level 1_4");
    }

    public int getPlayersNumber()
    {
        return lobby.getPlayersNumber();
    }

    public List<Long> getUserIds()
    {
        return lobby.getUserIds();
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
        var users = lobby.getUserIds();
        while (users.size() > lobby.getPlayersNumber()){
            users.remove(users.get(users.size() - 1));
        }
    }


    public synchronized void join(String token){
        if (awaitingTokens.contains(token)){
            awaitingTokens.remove(token);
            joinedTokens.add(token);
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

}
