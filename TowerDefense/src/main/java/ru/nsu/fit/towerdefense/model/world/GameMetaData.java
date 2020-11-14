package ru.nsu.fit.towerdefense.model.world;

import ru.nsu.fit.towerdefense.model.world.map.GameMap;

public class GameMetaData {

    private static GameMetaData instance = null;

    private GameMetaData()
    {

    }

    public static GameMetaData getInstance()
    {
        if (instance == null)
        {
            instance = new GameMetaData();
        }
        return instance;
    }

}
