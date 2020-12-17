package ru.nsu.fit.towerdefense.model.map;

public class WaveEnemies {

    private Integer spawnPosition;

    private Integer count;

    private String type;

    private Integer moneyReward;


    public WaveEnemies()
    {
        type = null;
    }

    public String getType() {
        return type;
    }

    public Integer getCount() {
        return count;
    }

    public Integer getMoneyReward() {return moneyReward; }

    public Integer getSpawnPosition() {
        return spawnPosition;
    }

    public static class Builder
    {
        private int spawnPosition;
        private int count;
        private int moneyReward;
        private String type;
        public Builder(){}

        public void setType(String type) {
            this.type = type;
        }

        public void setSpawnPosition(int spawnPosition)
        {
            this.spawnPosition = spawnPosition;
        }

        public void setCount(int count)
        {
            this.count = count;
        }

        public void setMoneyReward(int moneyReward) {this.moneyReward = moneyReward;}

        public WaveEnemies build()
        {
            WaveEnemies enemies = new WaveEnemies();
            enemies.type = type;
            enemies.count = count;
            enemies.spawnPosition = spawnPosition;
            enemies.moneyReward = moneyReward;
            return enemies;
        }
    }

    /*public void setCount(Integer count) {
        this.count = count;
    }

    public void setSpawnPosition(Integer spawnPosition) {
        this.spawnPosition = spawnPosition;
    }

    public void setType(EnemyType type) {
        this.type = type;
    }*/
}
