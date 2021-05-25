package ru.nsu.fit.towerdefense.server.lobby;

public class Lobby {
    private final Long id;
    private String levelName;
    private int playersNumber;


    public Lobby(long id){
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public int getPlayersNumber() {
        return playersNumber;
    }


    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public void setPlayersNumber(int playersNumber) {
        this.playersNumber = playersNumber;
    }

}
