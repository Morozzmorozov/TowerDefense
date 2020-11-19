package ru.nsu.fit.towerdefense.model.world.map;

import ru.nsu.fit.towerdefense.model.world.types.EnemyType;

public class WaveEnemies {

    private Integer spawnPosition;

    private Integer count;

    private String type;

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

    public Integer getSpawnPosition() {
        return spawnPosition;
    }

    public static class Builder
    {
        private int spawnPosition;
        private int count;
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

        public WaveEnemies build()
        {
            WaveEnemies enemies = new WaveEnemies();
            enemies.type = type;
            enemies.count = count;
            enemies.spawnPosition = spawnPosition;
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
