package ru.nsu.fit.towerdefense.simulator.map;

import java.util.LinkedList;
import java.util.List;

public class WaveDescription {
    private Double scaleFactor;
    private Double spawnInterval;
    private Double timeTillNextWave;
    private Integer scienceReward;


    private List<WaveEnemies> enemiesList;

    private WaveDescription()
    {
        enemiesList = new LinkedList<>();
    }

    public static class Builder
    {
        private Double scaleFactor;
        private Double spawnInterval;
        private Double timeTillNextWave;
        private Integer scienceReward;

        private List<WaveEnemies> enemiesList;
        public Builder()
        {
            enemiesList = new LinkedList<>();
        }

        public void setScaleFactor(double scaleFactor)
        {
            this.scaleFactor = scaleFactor;
        }

        public void setSpawnInterval(double spawnInterval)
        {
            this.spawnInterval = spawnInterval;
        }

        public void setTimeTillNextWave(double timeTillNextWave)
        {
            this.timeTillNextWave = timeTillNextWave;
        }

        public void setScienceReward(int scienceReward)
        {
            this.scienceReward = scienceReward;
        }

        public void addEnemy(WaveEnemies enemy)
        {
            enemiesList.add(enemy);
        }

        public WaveDescription build()
        {
            WaveDescription description = new WaveDescription();
            description.enemiesList = enemiesList;
            description.scienceReward = scienceReward;
            description.timeTillNextWave = timeTillNextWave;
            description.spawnInterval = spawnInterval;
            description.scaleFactor = scaleFactor;
            return description;
        }
    }

    public Integer getScienceReward() {
        return scienceReward;
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

    /*public void setEnemiesList(List<WaveEnemies> enemiesList) {
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
    */
}
