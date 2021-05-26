package ru.nsu.fit.towerdefense.multiplayer;

import java.util.List;

public class Message {

    public enum Type {
        READY, START, CHOOSE_PLATFORM, EVENT, STATE
    }

    private Type type;
    private List<String> playerNames;
    private Integer platformNumber;
    private String serializedEvent;
    private String serializedWorld;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<String> getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = playerNames;
    }

    public Integer getPlatformNumber() {
        return platformNumber;
    }

    public void setPlatformNumber(Integer platformNumber) {
        this.platformNumber = platformNumber;
    }

    public String getSerializedEvent() {
        return serializedEvent;
    }

    public void setSerializedEvent(String serializedEvent) {
        this.serializedEvent = serializedEvent;
    }

    public String getSerializedWorld() {
        return serializedWorld;
    }

    public void setSerializedWorld(String serializedWorld) {
        this.serializedWorld = serializedWorld;
    }
}
