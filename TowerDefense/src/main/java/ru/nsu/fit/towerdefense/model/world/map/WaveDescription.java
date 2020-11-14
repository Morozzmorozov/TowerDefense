package ru.nsu.fit.towerdefense.model.world.map;

import java.util.LinkedList;
import java.util.List;

public class WaveDescription {
    private Double scaleFactor;
    private Double spawnInterval;
    private Double timeTillNextWave;
    private Integer moneyReward;

    private List<WaveEnemies> enemiesList;

    public WaveDescription()
    {
        enemiesList = new LinkedList<>();
    }

    public Integer getMoneyReward() {
        return moneyReward;
    }

    public Double getTimeTillNextWave() {
        return timeTillNextWave;
    }

    public Double getSpawnInterval() {
        return spawnInterval;
    }

    public Double getScaleFactor() {
        return scaleFactor;
    }

    public List<WaveEnemies> getEnemiesList() {
        return enemiesList;
    }

    public void setEnemiesList(List<WaveEnemies> enemiesList) {
        this.enemiesList = enemiesList;
    }

    public void setMoneyReward(Integer moneyReward) {
        this.moneyReward = moneyReward;
    }

    public void setScaleFactor(Double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public void setSpawnInterval(Double spawnInterval) {
        this.spawnInterval = spawnInterval;
    }

    public void setTimeTillNextWave(Double timeTillNextWave) {
        this.timeTillNextWave = timeTillNextWave;
    }
}
