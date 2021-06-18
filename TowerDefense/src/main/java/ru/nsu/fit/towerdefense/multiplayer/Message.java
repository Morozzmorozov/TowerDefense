package ru.nsu.fit.towerdefense.multiplayer;

import java.util.List;

public class Message {

    public enum Type {
        READY, START, EVENT, STATE, RESULT
    }

    private Type type;
    private List<String> playerNames;
    private String serializedEvent;
    private String serializedWorld;
    private String serializedResult;
    private List<String> serializedResults;

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

    public String getSerializedResult() {
        return serializedResult;
    }

    public void setSerializedResult(String serializedResult) {
        this.serializedResult = serializedResult;
    }

    public List<String> getSerializedResults() {
        return serializedResults;
    }

    public void setSerializedResults(List<String> serializedResults) {
        this.serializedResults = serializedResults;
    }
}
