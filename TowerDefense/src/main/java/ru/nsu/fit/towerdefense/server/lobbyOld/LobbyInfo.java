package ru.nsu.fit.towerdefense.server.lobbyOld;


public class LobbyInfo {
    private final Long id;
    private String levelName;
    private int playersNumber;

    public LobbyInfo(long id){
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
