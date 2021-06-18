package ru.nsu.fit.towerdefense.multiplayer.entities;

public class SResult {

    String playerName;
    int researchPoints;
    int multiplayerPoints;
    int money;
    int baseHealth;
    int enemiesKilled;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getResearchPoints() {
        return researchPoints;
    }

    public void setResearchPoints(int researchPoints) {
        this.researchPoints = researchPoints;
    }

    public int getMultiplayerPoints() {
        return multiplayerPoints;
    }

    public void setMultiplayerPoints(int multiplayerPoints) {
        this.multiplayerPoints = multiplayerPoints;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getBaseHealth() {
        return baseHealth;
    }

    public void setBaseHealth(int baseHealth) {
        this.baseHealth = baseHealth;
    }

    public int getEnemiesKilled() {
        return enemiesKilled;
    }

    public void setEnemiesKilled(int enemiesKilled) {
        this.enemiesKilled = enemiesKilled;
    }
}
