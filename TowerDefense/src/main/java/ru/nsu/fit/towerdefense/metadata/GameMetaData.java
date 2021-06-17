package ru.nsu.fit.towerdefense.metadata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.EffectType;
import ru.nsu.fit.towerdefense.metadata.map.BaseDescription;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.metadata.map.RoadDescription;
import ru.nsu.fit.towerdefense.metadata.map.TowerBuildingPositions;
import ru.nsu.fit.towerdefense.metadata.map.WaveDescription;
import ru.nsu.fit.towerdefense.metadata.map.WaveEnemies;
import ru.nsu.fit.towerdefense.metadata.techtree.Research;
import ru.nsu.fit.towerdefense.metadata.techtree.TechTree;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Effect;
import ru.nsu.fit.towerdefense.util.Vector2;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.EnemyType;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.ProjectileType;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType;

import java.io.File;
import java.lang.reflect.Array;
import java.util.*;

public class GameMetaData {

    private static GameMetaData instance = null;

    private final HashMap<String, GameMap> loadedMaps;
    private final HashMap<String, EnemyType> loadedEnemies;
    private final HashMap<String, TowerType> loadedTowers;
    private final HashMap<String, ProjectileType> loadedProjectiles;
    private final HashMap<String, EffectType> loadedEffects;
    private TechTree tree = null;

    private final String mapRoot = "/Maps";//"src/test/resources/Maps/";
    private final String enemiesRoot = "/Enemies";//"src/test/resources/Enemies/";
    private final String towersRoot = "/Towers";//"src/test/resources/Towers/";
    private final String projectileRoot = "/Projectiles";//"src/test/resources/Projectiles/";
    private final String imageRoot = "src/main/resources/ru/nsu/fit/towerdefense/images/";
    private final String techRoot = "/TechTree";
    private final String effectRoot = "/Effects";

    //private final File mapDir;
    //private final File enemiesDir;
    //private final File towersDir;
    //private final File projectilesDir;
    //private final File imagesDir;

    private GameMetaData()
    {
        loadedMaps = new HashMap<>();
        loadedEnemies = new HashMap<>();
        loadedProjectiles = new HashMap<>();
        loadedTowers = new HashMap<>();
        loadedEffects = new HashMap<>();
        //mapDir = new File (this.getClass().getClassLoader().getResource(mapRoot));
        //mapDir = new File(mapRoot);
        //enemiesDir = new File(enemiesRoot);
        //towersDir = new File(towersRoot);
        //projectilesDir = new File(projectileRoot);
        //imagesDir = new File(imageRoot);
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
        File file = null;
        try
        {
            var t = GameMetaData.class.getResource(mapRoot);
            file = new File(t.toURI());
        }
        catch (Exception e)
        {
        }
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



    public Collection<String> getTowerNames()
    {
        File file = null;
        try
        {
            var t = GameMetaData.class.getResource(towersRoot);
            file = new File(t.toURI());
        }
        catch (Exception e)
        {
        }
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
        synchronized (loadedMaps)
        {
            if (loadedMaps.containsKey(name))
            {
                return loadedMaps.get(name);
            }
        }
        GameMap map = loadMap(mapRoot + "/" + name + ".json");
        synchronized (loadedMaps)
        {
            if (map != null)
            {
                loadedMaps.put(name, map);
                return map;
            }
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
        synchronized (loadedEnemies)
        {
            if (loadedEnemies.containsKey(name))
            {
                return loadedEnemies.get(name);
            }
        }
        EnemyType type = loadEnemyType(name);
        synchronized (loadedEnemies)
        {
            if (type != null)
            {
                loadedEnemies.put(name, type);
                return type;
            }
        }
        throw new NoSuchElementException("Invalid enemy type or file");
    }

    public TowerType getTowerType(String name) throws NoSuchElementException
    {
        synchronized (loadedTowers)
        {
            if (loadedTowers.containsKey(name))
                return loadedTowers.get(name);
        }
        TowerType type = loadTowerType(name);
        synchronized (loadedTowers)
        {
            if (type != null)
            {
                loadedTowers.put(name, type);
                return type;
            }
        }
        throw new NoSuchElementException("Invalid enemy type or file");
    }

    public ProjectileType getProjectileType(String name) throws NoSuchElementException
    {
        synchronized (loadedProjectiles)
        {
            if (loadedProjectiles.containsKey(name))
            {
                return loadedProjectiles.get(name);
            }
        }
        ProjectileType projectileType = loadProjectileType(name);
        synchronized (loadedProjectiles)
        {
            if (projectileType != null)
            {
                loadedProjectiles.put(name, projectileType);
                return projectileType;
            }
        }
        throw new NoSuchElementException("Invalid enemy type or file");
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

    public TechTree getTechTree() throws  NoSuchElementException
    {
        if (tree == null)
        {
            tree = loadTree();
            tree.loadUnlocked();
        }
        return tree;
    }

    public EffectType getEffectType(String name) throws NoSuchElementException
    {
        synchronized (loadedEffects)
        {
            if (loadedEffects.containsKey(name))
            {
                return loadedEffects.get(name);
            }
        }
        EffectType type = loadEffect(name);
        synchronized (loadedEffects)
        {
            if (type != null)
            {
                loadedEffects.put(name, type);
                return type;
            }
        }
        throw new NoSuchElementException("Invalid name or configuration");
    }

    private EffectType loadEffect(String name)
    {
        try
        {
            File json = new File(GameMetaData.class.getResource(effectRoot + "/" + name + ".json").toURI());
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode node = objectMapper.readValue(json, ObjectNode.class);
            EffectType.Builder builder = new EffectType.Builder(name);

            builder.setSpeedMultiplier(node.get("Speed").asDouble());
            builder.setDamagePerTick(node.get("DamagePerTick").asInt());
            builder.setDuration(node.get("Duration").asInt());
            builder.setImage(node.get("Image").asText());

            return builder.build();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private TechTree loadTree()
    {
        try
        {
            File json = new File(GameMetaData.class.getResource(techRoot + "/tech.json").toURI());
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode node = objectMapper.readValue(json, ObjectNode.class);
            HashMap<String, Research> nameToNode = new HashMap<>();
            HashSet<String> types = new HashSet<>();
            ArrayList<Research> research = new ArrayList<>();


            for (var x : node.get("Basic"))
            {
                types.add(x.asText());
            }

            ArrayNode arrayNode = node.get("Researches").deepCopy();

            for (var x : arrayNode)
            {
                Research r = parseNode(x);
                research.add(r);
                nameToNode.put(r.getName(), r);
            }


            arrayNode = node.get("Dependencies").deepCopy();

            for (var x : arrayNode)
            {
                String r1 = x.get(0).asText();
                String r2 = x.get(1).asText();
                nameToNode.get(r1).addDependency(nameToNode.get(r2));
            }

            ArrayList<Research> available = new ArrayList<>();

            for (var x : research)
            {
                if (x.getLeft() == 0)
                    available.add(x);
            }

            TechTree tree = new TechTree(nameToNode);
            tree.setAvailableResearches(available);
            tree.setResearch(research);
            tree.setAvailableTypes(types);
            tree.process();
            return tree;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            throw new NoSuchElementException();
        }
    }

    private Research parseNode(JsonNode root)
    {
        String name = root.get("Name").asText();
        String info = root.get("Info").asText();
        String image = root.get("Image").asText();
        int cost = root.get("Cost").asInt();

        ArrayNode node = root.get("Types").deepCopy();
        ArrayList<String> types = new ArrayList<>();
        for (var x : node)
        {
            types.add(x.asText());
        }
        return new Research(name, info, types, cost, image);
    }


    private ProjectileType loadProjectileType(String name)
    {
        try
        {
            File json = new File(GameMetaData.class.getResource(projectileRoot + "/" + name + ".json").toURI());
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode node = objectMapper.readValue(json, ObjectNode.class);
            ProjectileType.Builder builder = new ProjectileType.Builder(name);
            builder.setDisplayInfo(node.get("DisplayInfo").asText());
            builder.setSpeed((float)node.get("Speed").asDouble());
            builder.setSelfGuided(node.get("SelfGuided").asBoolean());
            builder.setBasicDamage(node.get("BasicDamage").asInt());
            ArrayNode upgrades = node.get("EnemyTypeDamage").deepCopy();
            for (int i = 0; upgrades.get(i) != null; i++)
            {
                ArrayNode upgrade = upgrades.get(i).deepCopy();
                builder.add(upgrade.get(0).asText(), upgrade.get(1).asInt());
            }
            builder.setHitBox(node.get("HitBox").asInt());
            builder.setSize(node.get("SizeX").asDouble(), node.get("SizeY").asDouble());
            builder.setImage(node.get("Image").asText());
            if (node.has("AngularVelocity"))
                builder.setAngularVelocity(node.get("AngularVelocity").asDouble());
            upgrades = node.get("Effects").deepCopy();
            for (var x : upgrades)
            {
                builder.addEffect(x.asText());
            }
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
            File json = new File(GameMetaData.class.getResource(towersRoot + "/"  + name + ".json").toURI());
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode node = objectMapper.readValue(json, ObjectNode.class);
            TowerType.Builder builder = new TowerType.Builder(name);
            builder.setDisplayInfo(node.get("DisplayInfo").asText());
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
            builder.setRotationSpeed(node.get("RotationSpeed").asDouble());
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
            File json = new File(GameMetaData.class.getResource(enemiesRoot + "/" + name + ".json").toURI());
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode node = objectMapper.readValue(json, ObjectNode.class);
            EnemyType.Builder builder = new EnemyType.Builder(name);
            builder.setDisplayInfo (node.get("DisplayInfo").asText());
            builder.setHealth(node.get("Health").asInt());
            builder.setSpeed((float)node.get("Speed").asDouble());
            builder.setHitBox((float)node.get("HitBox").asDouble());
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
            File json = new File(GameMetaData.class.getResource(name).toURI());
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

            if (node.get("Players") != null){
                builder.setPlayersNumber(node.get("Players").asInt());
            }

            if (node.get("PlayerPositions") != null)
            {
                List<List<Integer>> t = new ArrayList<>();
                for (var x : node.get("PlayerPositions"))
                {
                    List<Integer> t1 = new ArrayList<>();
                    for (var y : x)
                    {
                        t1.add(y.asInt());
                    }
                    t.add(t1);
                }
                builder.setPlayerPlatforms(t);
            }

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
            builder.setScienceReward(root.get("ScienceReward").asInt());
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
        builder.setMoneyReward(root.get("MoneyReward").asInt());
        builder.setSpawnPosition(root.get("SpawnPosition").asInt());
        builder.setCount(root.get("Count").asInt());
        builder.setType(root.get("Type").asText());
        return builder.build();
    }

}
