package ru.nsu.fit.towerdefense.multiplayer;

public enum GameType {

    COOPERATIVE, COMPETITIVE;

    public static String capitalize(GameType gameType) {
        String gameTypeLowered = gameType.toString().toLowerCase();
        return gameTypeLowered.substring(0, 1).toUpperCase() + gameTypeLowered.substring(1);
    }
}
