package ru.nsu.fit.towerdefense.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.nsu.fit.towerdefense.model.map.*;
import ru.nsu.fit.towerdefense.model.util.Vector2;
import ru.nsu.fit.towerdefense.model.world.types.EnemyType;
import ru.nsu.fit.towerdefense.model.world.types.ProjectileType;
import ru.nsu.fit.towerdefense.model.world.types.TowerType;

import java.io.File;
import java.util.*;

public class GameMetaData {

    private static GameMetaData instance = null;

    private final HashMap<String, GameMap> loadedMaps;
    private final HashMap<String, EnemyType> loadedEnemies;
    private final HashMap<String, TowerType> loadedTowers;
    private final HashMap<String, ProjectileType> loadedProjectiles;

    private final String mapRoot = "src/test/resources/Maps/";
    private final String enemiesRoot = "src/test/resources/Enemies/";
    private final String towersRoot = "src/test/resources/Towers/";
    private final String projectileRoot = "src/test/resources/Projectiles/";
    private final String imageRoot = "src/main/resources/ru/nsu/fit/towerdefense/images/";

    private final File mapDir;
    private final File enemiesDir;
    private final File towersDir;
    private final File projectilesDir;
    private final File imagesDir;

    private GameMetaData()
    {
        loadedMaps = new HashMap<>();
        loadedEnemies = new HashMap<>();
        loadedProjectiles = new HashMap<>();
        loadedTowers = new HashMap<>();
        mapDir = new File(mapRoot);
        enemiesDir = new File(enemiesRoot);
        towersDir = new File(towersRoot);
        projectilesDir = new File(projectileRoot);
        imagesDir = new File(imageRoot);
    }

    public static GameMetaData getInstance()
    {
        if (instance == null)
        {
            instance = new GameMetaData();
        }
        return instance;
    }

    public Collection<String> getGameMapNames()
    {
        File file = new File(mapRoot);
        if (!file.isDirectory())
        {
            return new ArrayList<>();
        }

        File[] files = file.listFiles();
        ArrayList<String> names = new ArrayList<>();
        for (var x : files)
        {
            if (x.isFile())
            {
                String path = x.getName();
                String[] name = new String[2];
                name[0] = path.substring(0, path.length() - 5);
                name[1] = path.substring(path.length() - 5, path.length());
                if (name[1].compareTo(".json") == 0) {
                    names.add(name[0]);
                }
            }
        }
        return names;
    }

    /**
     * Tries to get map description by it's name
     * @param name - name of a map
     * @return map reference if such exists, null otherwise
     */
    public GameMap getMapDescription(String name) throws NoSuchElementException
    {
        if (loadedMaps.containsKey(name))
        {
            return loadedMaps.get(name);
        }
        GameMap map = loadMap(mapRoot + name + ".json");
        if (map != null)
        {
            loadedMaps.put(name, map);
            return map;
        }
        throw new NoSuchElementException("Invalid map description or name");
    }

    /**
     * Tries to get enemy type by it's name.
     * @param name - name of type
     * @return enemy type reference, it's exist
     */
    public EnemyType getEnemyType(String name) throws NoSuchElementException
    {
        if (loadedEnemies.containsKey(name))
        {
            return loadedEnemies.get(name);
        }
        EnemyType type = loadEnemyType(name);
        if (type != null)
        {
            loadedEnemies.put(name, type);
            return type;
        }
        throw new NoSuchElementException("Invalid enemy type or file");
    }

    public TowerType getTowerType(String name) throws NoSuchElementException
    {
        if (loadedTowers.containsKey(name))
            return loadedTowers.get(name);
        TowerType type = loadTowerType(name);
        if (type != null)
        {
            loadedTowers.put(name, type);
            return type;
        }
        throw new NoSuchElementException("Invalid enemy type or file");
    }

    public ProjectileType getProjectileType(String name) throws NoSuchElementException
    {
        if (loadedProjectiles.containsKey(name))
        {
            return loadedProjectiles.get(name);
        }
        ProjectileType projectileType = loadProjectileType(name);
        if (projectileType != null)
        {
            loadedProjectiles.put(name, projectileType);
            return projectileType;
        }
        throw new NoSuchElementException("Invalid enemy type or file");
    }

    public String getImagePath(String imageName) // todo
    {
        File files[] = imagesDir.listFiles();
        if (files == null) {
            throw new NoSuchElementException();
        }

        for (var x : files)
        {
            if (x.getName().substring(0, imageName.length() + 1).compareTo(imageName + '.') == 0)
            {
                return imageRoot + x.getName();
            }
        }
        throw new NoSuchElementException();
        /*return (imageRoot + imageName);
        if (imageName.equals("triangle")) return "/ru/nsu/fit/towerdefense/images/triangle.png";
        if (imageName.equals("circle")) return "/ru/nsu/fit/towerdefense/images/circle.png";*/
    }

    public void forceLoadMap(String mapName) throws NoSuchElementException
    {
        GameMap map = getMapDescription(mapName);
        var it = map.getDescriptionIterator();
        while (it.hasNext())
        {
            var x = it.next();
            var it1 = x.getEnemiesList().iterator();
            while (it1.hasNext())
            {
                getEnemyType(it1.next().getType());
            }
        }
    }

    private ProjectileType loadProjectileType(String name)
    {
        try
        {
            File json = new File(projectileRoot + name + ".json");
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode node = objectMapper.readValue(json, ObjectNode.class);
            ProjectileType.Builder builder = new ProjectileType.Builder(name);
            builder.setSpeed((float)node.get("Speed").asDouble());
            builder.setSelfGuided(node.get("SelfGuided").asBoolean());
            builder.setBasicDamage(node.get("BasicDamage").asInt());
            ArrayNode upgrades = node.get("EnemyTypeDamage").deepCopy();
            for (int i = 0; upgrades.get(i) != null; i++)
            {
                ArrayNode upgrade = upgrades.get(i).deepCopy();
                builder.add(upgrade.get(0).asText(), upgrade.get(0).asInt());
            }
            builder.setHitBox(node.get("HitBox").asInt());
            builder.setSize(node.get("SizeX").asDouble(), node.get("SizeY").asDouble());
            builder.setImage(node.get("Image").asText());
            return builder.build();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private TowerType loadTowerType(String name)
    {
        try
        {
            File json = new File(towersRoot + name + ".json");
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode node = objectMapper.readValue(json, ObjectNode.class);
            TowerType.Builder builder = new TowerType.Builder(name);
            builder.setPrice(node.get("Price").asInt());
            ArrayNode upgrades = node.get("Upgrades").deepCopy();
            for (int i = 0; upgrades.get(i) != null; i++)
            {
                ArrayNode upgrade = upgrades.get(i).deepCopy();
                builder.addUpgrade(new TowerType.Upgrade(upgrade.get(0).asText(), upgrade.get(1).asInt()));
            }
            builder.setRange(node.get("Range").asInt());
            builder.setFireRate(node.get("FireRate").asInt());
            builder.setFireType(node.get("FireType").asText());
            builder.setProjectileType(node.get("ProjectileType").asText());
            builder.setImage(node.get("Image").asText());
            return builder.build();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private EnemyType loadEnemyType(String name)
    {
        try
        {
            File json = new File(enemiesRoot + name + ".json");
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode node = objectMapper.readValue(json, ObjectNode.class);
            EnemyType.Builder builder = new EnemyType.Builder(name);
            builder.setHealth(node.get("Health").asInt());
            builder.setSpeed((float)node.get("Speed").asDouble());
            builder.setHitBox((float)node.get("HitBox").asInt());
            builder.setSize(node.get("SizeX").asDouble(), node.get("SizeY").asDouble());
            builder.setImage(node.get("Image").asText());
            builder.setDamage(node.get("Damage").asInt());
            return builder.build();
        }
        catch (Exception e)
        {
            System.err.print(e.getMessage());
            return null;
        }
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
