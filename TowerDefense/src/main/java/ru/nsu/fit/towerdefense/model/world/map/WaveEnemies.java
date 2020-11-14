package ru.nsu.fit.towerdefense.model.world.map;

import ru.nsu.fit.towerdefense.model.world.types.EnemyType;

public class WaveEnemies {

    private Integer spawnPosition;

    private Integer count;

    private EnemyType type;

    public WaveEnemies()
    {
        type = null;
    }

    public EnemyType getType() {
        return type;
    }

    public Integer getCount() {
        return count;
    }

    public Integer getSpawnPosition() {
        return spawnPosition;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setSpawnPosition(Integer spawnPosition) {
        this.spawnPosition = spawnPosition;
    }

    public void setType(EnemyType type) {
        this.type = type;
    }
}
