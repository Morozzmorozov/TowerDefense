package ru.nsu.fit.towerdefense.multiplayer.entities;

import ru.nsu.fit.towerdefense.multiplayer.GameType;

import java.util.List;

public class SLobby {

    private String id;
    private List<SPlayer> players;
    private int maxPlayers;
    private String levelName;
    private GameType gameType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<SPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<SPlayer> players) {
        this.players = players;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }
}
