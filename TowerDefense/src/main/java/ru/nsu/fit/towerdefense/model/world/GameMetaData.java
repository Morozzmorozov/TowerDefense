package ru.nsu.fit.towerdefense.model.world;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.nsu.fit.towerdefense.model.world.gameobject.Base;
import ru.nsu.fit.towerdefense.model.world.map.*;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameMetaData {

    private static GameMetaData instance = null;

    private HashMap<String, GameMap> loadedMaps;

    private String root = "src/test/resources/";

    private GameMetaData()
    {
        loadedMaps = new HashMap<>();
    }

    public static GameMetaData getInstance()
    {
        if (instance == null)
        {
            instance = new GameMetaData();
        }
        return instance;
    }

    /**
     * Tries to get map description by it's name
     * @param name - name of a map
     * @return map reference if such exists, null otherwise
     */
    public GameMap loadMapDescription(String name)
    {
        if (loadedMaps.containsKey(name))
        {
            return loadedMaps.get(name);
        }
        GameMap map = loadMap(root + name + ".json");
        if (map != null)
        {
            loadedMaps.put(name, map);
        }
        return map;
    }

    private GameMap loadMap(String name)
    {
        try {
            File json = new File(name);
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode node = objectMapper.readValue(json, ObjectNode.class);
            GameMap.Builder builder = new GameMap.Builder();

            int sizeX = node.get("SizeX").asInt();
            int sizeY = node.get("SizeY").asInt();

            builder.setSize(sizeX, sizeY);

            int scienceReward = node.get("Reward").asInt();

            builder.setScienceReward(scienceReward);

            BaseDescription base = readBase(node.get("Base"));
            builder.setBaseDescription(base);

            RoadDescription road = readRoads(node.get("Roads"));
            builder.setRoads(road);

            TowerBuildingPositions buildingPositions = readBuildingPositions(node.get("BuildingPositions"));
            builder.setPositions(buildingPositions);

            ArrayNode wavesNode = node.get("Waves").deepCopy();
            for (int i = 0; wavesNode.get(i) != null; i++)
            {
                WaveDescription waveDescription =  readWaveDescription(wavesNode.get(i));
                builder.addWave(waveDescription);
            }

            return builder.build();
        }
        catch (Exception e)
        {
            System.err.print(e.getMessage());
            return null;
        }
    }

    private BaseDescription readBase(JsonNode root)
    {
        try
        {
            //ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = root.deepCopy();//(ObjectNode) mapper.readTree(root.asText());
            BaseDescription.Builder builder = new BaseDescription.Builder();
            builder.setHealth(node.get("Health").asInt());
            builder.setPosition(node.get("PosX").asInt(), node.get("PosY").asInt());
            builder.setImage(node.get("Image").asText());
            return builder.build();
        }
        catch (Exception e)
        {
            System.err.print(e.getMessage());
            return null;
        }
    }

    private RoadDescription readRoads(JsonNode root)
    {
        try
        {
            ArrayNode node = root.deepCopy();
            RoadDescription.Builder builder = new RoadDescription.Builder();

            for (int i = 0; node.get(i) != null; i++)
            {
                JsonNode cnode = node.get(i);
                builder.addRoad();
                parseRoad(cnode, builder);
            }

            return builder.build();
        }
        catch (Exception e)
        {
            System.err.print(e.getMessage());
            return null;
        }
    }

    private void parseRoad(JsonNode root, RoadDescription.Builder builder)
    {
        ArrayNode node = root.deepCopy();

        for (int i = 0; node.get(i) != null; i++)
        {
            ArrayNode node1 = node.get(i).deepCopy();
            Double x = node1.get(0).asDouble();
            Double y = node1.get(1).asDouble();
            builder.addTile(new Vector2<>(x, y));
        }
    }

    private TowerBuildingPositions readBuildingPositions(JsonNode root)
    {
        try
        {
            ArrayNode node = root.deepCopy();
            TowerBuildingPositions.Builder builder = new TowerBuildingPositions.Builder();

            for (int i = 0; node.get(i) != null; i++)
            {
                ArrayNode cnode = node.get(i).deepCopy();
                int x = cnode.get(0).asInt();
                int y = cnode.get(1).asInt();
                builder.addPosition(x, y);
            }

            return builder.build();
        }
        catch (Exception e)
        {
            System.err.print(e.getMessage());
            return null;
        }
    }

    private WaveDescription readWaveDescription(JsonNode root)
    {
        try
        {
            WaveDescription.Builder builder = new WaveDescription.Builder();
            builder.setScaleFactor(root.get("ScaleFactor").asDouble());
            builder.setSpawnInterval(root.get("SpawnInterval").asDouble());
            builder.setTimeTillNextWave(root.get("TimeTillNextWave").asDouble());
            builder.setMoneyReward(root.get("MoneyReward").asInt());
            ArrayNode enemiesNode = root.get("Enemies").deepCopy();

            for (int i = 0; enemiesNode.get(i) != null; i++)
            {
                WaveEnemies enemy = readEnemies(enemiesNode.get(i));
                builder.addEnemy(enemy);
            }

            return builder.build();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private WaveEnemies readEnemies(JsonNode root)
    {
        WaveEnemies.Builder builder = new WaveEnemies.Builder();
        builder.setSpawnPosition(root.get("SpawnPosition").asInt());
        builder.setCount(root.get("Count").asInt());
        builder.setType(root.get("Type").asText());
        return builder.build();
    }
}
