package ru.nsu.fit.towerdefense.multiplayer;

import ru.nsu.fit.towerdefense.multiplayer.entities.SResult;

import java.util.List;

public class Message {

    public enum Type {
        READY, START, EVENT, STATE, RESULTS
    }

    private Type type;
    private List<String> playerNames;
    private String serializedEvent;
    private String serializedWorld;
    private List<SResult> results;

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

    public List<SResult> getResults() {
        return results;
    }

    public void setResults(List<SResult> results) {
        this.results = results;
    }
}
