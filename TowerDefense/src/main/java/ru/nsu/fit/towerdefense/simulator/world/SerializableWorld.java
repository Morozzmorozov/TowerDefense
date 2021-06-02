package ru.nsu.fit.towerdefense.simulator.world;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType.FireType;
import ru.nsu.fit.towerdefense.metadata.map.WaveDescription;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Base;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Effect;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Portal;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.RoadTile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower.Mode;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.TowerPlatform;
import ru.nsu.fit.towerdefense.util.Vector2;

public class SerializableWorld {

  public static class WorldDeserializer implements JsonDeserializer<SerializableWorld> {

    @Override
    public SerializableWorld deserialize(JsonElement json, Type typeOfT,
        JsonDeserializationContext context) throws JsonParseException {

      JsonObject jsonObject = json.getAsJsonObject();
      Gson gson = new Gson();

      var serializableWorld = gson.fromJson(json, SerializableWorld.class);

      for (var entry : jsonObject.get("ideffectMap").getAsJsonObject().entrySet()) {
        var effect = new Gson().fromJson(entry.getValue(), SerializableEffect.class);
        effect.serializableWorld = serializableWorld;
        serializableWorld.ideffectMap.put(Integer.parseInt(entry.getKey()), effect);
      }
      for (var entry : jsonObject.get("idenemyMap").getAsJsonObject().entrySet()) {
        var effect = new Gson().fromJson(entry.getValue(), SerializableEnemy.class);
        effect.serializableWorld = serializableWorld;
        serializableWorld.idenemyMap.put(Integer.parseInt(entry.getKey()), effect);
      }
      for (var entry : jsonObject.get("idportalMap").getAsJsonObject().entrySet()) {
        var effect = new Gson().fromJson(entry.getValue(), SerializablePortal.class);
        effect.serializableWorld = serializableWorld;
        serializableWorld.idportalMap.put(Integer.parseInt(entry.getKey()), effect);
      }
      for (var entry : jsonObject.get("idprojectileMap").getAsJsonObject().entrySet()) {
        var effect = new Gson().fromJson(entry.getValue(), SerializableProjectile.class);
        effect.serializableWorld = serializableWorld;
        serializableWorld.idprojectileMap.put(Integer.parseInt(entry.getKey()), effect);
      }
      for (var entry : jsonObject.get("idroadTileMap").getAsJsonObject().entrySet()) {
        var effect = new Gson().fromJson(entry.getValue(), SerializableRoadTile.class);
        effect.serializableWorld = serializableWorld;
        serializableWorld.idroadTileMap.put(Integer.parseInt(entry.getKey()), effect);
      }
      for (var entry : jsonObject.get("idtowerMap").getAsJsonObject().entrySet()) {
        var effect = new Gson().fromJson(entry.getValue(), SerializableTower.class);
        effect.serializableWorld = serializableWorld;
        serializableWorld.idtowerMap.put(Integer.parseInt(entry.getKey()), effect);
      }
      for (var entry : jsonObject.get("idplatformMap").getAsJsonObject().entrySet()) {
        var effect = new Gson().fromJson(entry.getValue(), SerializableTowerPlatform.class);
        effect.serializableWorld = serializableWorld;
        serializableWorld.idplatformMap.put(Integer.parseInt(entry.getKey()), effect);
      }
      for (var entry : jsonObject.get("idwaveMap").getAsJsonObject().entrySet()) {
        var effect = new Gson().fromJson(entry.getValue(), SerializableWave.class);
        effect.serializableWorld = serializableWorld;
        serializableWorld.idwaveMap.put(Integer.parseInt(entry.getKey()), effect);
      }

      return serializableWorld;
    }
  }

  public SerializableWorld() {
  }

  // todo
  private transient int currentId = 1;
 // private transient Map<Integer, SerializableEntity> idMap = new HashMap<>();
  transient Map<Object, Integer> idMap = new HashMap<>();

  private int countdown;
  private int currentWaveNumber;
  private long tick;
  private int killedEnemies;

  private Map<Integer, SerializableEffect> ideffectMap = new HashMap<>();
  private Map<Integer, SerializableEnemy> idenemyMap = new HashMap<>();
  private Map<Integer, SerializablePortal> idportalMap = new HashMap<>();
  private Map<Integer, SerializableProjectile> idprojectileMap = new HashMap<>();
  private Map<Integer, SerializableRoadTile> idroadTileMap = new HashMap<>();
  private Map<Integer, SerializableTower> idtowerMap = new HashMap<>();
  private Map<Integer, SerializableTowerPlatform> idplatformMap = new HashMap<>();
  private Map<Integer, SerializableWave> idwaveMap = new HashMap<>();

  // ids
  private List<Integer> enemies = new ArrayList<>();
  private List<Integer> towers = new ArrayList<>();
  private List<Integer> projectiles = new ArrayList<>();
  private List<Integer> towerPlatforms = new ArrayList<>();
  private List<Integer> roadTiles = new ArrayList<>();
  private List<Integer> portals = new ArrayList<>();
  private SerializableBase base;
  private int currentWave;
  private Map<Integer, Integer> waveMap = new HashMap<>();
  private Map<String, Integer> moneyMap;

  public World generateWorld() {
    World world = new World();
    world.setKilledEnemies(killedEnemies);
    world.setCountdown(countdown);
    world.setCurrentWaveNumber(currentWaveNumber);
    world.setTick(tick);

    world.setBase(new Base(base.health, base.image, new Vector2<>(base.x, base.y)));

    world.setEnemies(enemies.stream().map(id -> idenemyMap.get(id).getEnemy()).collect(Collectors.toList()));
    world.setTowers(towers.stream().map(id -> idtowerMap.get(id).getTower()).collect(Collectors.toList()));
    world.setProjectiles(projectiles.stream().map(id -> idprojectileMap.get(id).getProjectile()).collect(Collectors.toList()));
    world.setTowerPlatforms(towerPlatforms.stream().map(id -> idplatformMap.get(id).getTowerPlatform()).collect(Collectors.toList()));
    world.setRoadTiles(roadTiles.stream().map(id -> idroadTileMap.get(id).getRoadTile()).collect(Collectors.toList()));
    world.setPortals(portals.stream().map(id -> idportalMap.get(id).getPortal()).collect(Collectors.toList()));

    world.setCurrentWave(idwaveMap.get(currentWave).getWave());
    world.waveMap = new HashMap<>();
    for (var entry : waveMap.entrySet()) {
      world.waveMap.put(entry.getKey(), idwaveMap.get(entry.getValue()).getWave());
    }

    world.moneyMap = new HashMap<>(moneyMap);
    return world;
  }


  public SerializableWorld(World world) {
    killedEnemies = world.getKilledEnemies();
    countdown = world.getCountdown();
    currentWaveNumber = world.getCurrentWaveNumber();
    tick = world.getTick();

    for (Enemy enemy : world.getEnemies()) {
      if (idMap.containsKey(enemy)) {
        enemies.add(idMap.get(enemy));
      } else {
        var sEnemy = new SerializableEnemy(enemy, this);
        enemies.add(sEnemy.id);
        idenemyMap.put(sEnemy.id, sEnemy);
      }
    }
    for (Tower tower : world.getTowers()) {
      if (idMap.containsKey(tower)) {
        towers.add(idMap.get(tower));
      } else {
        var sTower = new SerializableTower(tower, this);
        towers.add(sTower.id);
        idtowerMap.put(sTower.id, sTower);
      }
    }
    for (Projectile projectile : world.getProjectiles()) {
      if (idMap.containsKey(projectile)) {
        projectiles.add(idMap.get(projectile));
      } else {
        var sProjectile = new SerializableProjectile(projectile, this);
        projectiles.add(sProjectile.id);
        idprojectileMap.put(sProjectile.id, sProjectile);
      }
    }
    for (TowerPlatform towerPlatform : world.getTowerPlatforms()) {
      if (idMap.containsKey(towerPlatform)) {
        towerPlatforms.add(idMap.get(towerPlatform));
      } else {
        var sPlatform = new SerializableTowerPlatform(towerPlatform, this);
        towerPlatforms.add(sPlatform.id);
        idplatformMap.put(sPlatform.id, sPlatform);
      }
    }
    for (RoadTile roadTile : world.getRoadTiles()) {
      if (idMap.containsKey(roadTile)) {
        roadTiles.add(idMap.get(roadTile));
      } else {
        var sRoadTile = new SerializableRoadTile(roadTile, this);
        roadTiles.add(sRoadTile.id);
        idroadTileMap.put(sRoadTile.id, sRoadTile);
      }
    }
    for (Portal portal : world.getPortals()) {
      if (idMap.containsKey(portal)) {
        portals.add(idMap.get(portal));
      } else {
        var sPortal = new SerializablePortal(portal, this);
        portals.add(sPortal.id);
        idportalMap.put(sPortal.id, sPortal);
      }
    }

    base = new SerializableBase(world.getBase(), this);

    if (idMap.containsKey(world.getCurrentWave())) {
      currentWave = idMap.get(world.getCurrentWave());
    } else {
      var sWave = new SerializableWave(world.getCurrentWave(), this);
      currentWave = sWave.id;
      idwaveMap.put(sWave.id, sWave);
    }

    for (var entry : world.waveMap.entrySet()) {
      if (idMap.containsKey(entry.getValue())) {
        waveMap.put(entry.getKey(), idMap.get(entry.getValue()));
      } else {
        var sWave = new SerializableWave(entry.getValue(), this);
        idwaveMap.put(sWave.id, sWave);
        waveMap.put(entry.getKey(), sWave.id);
      }
    }

    moneyMap = new HashMap<>(world.moneyMap);

  }

  static class SerializableEntity {
    int id;
    transient SerializableWorld serializableWorld;
    SerializableEntity(Object object, SerializableWorld serializableWorld) {
      id = serializableWorld.currentId++;
      serializableWorld.idMap.put(object, id);
      this.serializableWorld = serializableWorld;
    }
  }


  static class SerializableEnemy extends SerializableEntity {
    private transient Enemy enemy = null;

    Enemy getEnemy() {
      if (enemy != null) {
        return enemy;
      }
      Enemy enemy = new Enemy(GameMetaData.getInstance().getEnemyType(type), waveNumber,
          new Vector2<>(x, y), moneyReward);

      enemy.setHealth(health);
      enemy.setVelocity(velocity);
      enemy.setDead(isDead);

      List<Vector2<Double>> trajectory = new ArrayList<>();
      for (int i = 0; i < xTrajectory.size(); ++i) {
        trajectory.add(new Vector2<>(xTrajectory.get(i), yTrajectory.get(i)));
      }
      enemy.setTrajectory(trajectory);

      enemy.setEffects(effects.stream().map(id -> serializableWorld.ideffectMap.get(id).getEffect()).collect(
          Collectors.toList()));

      enemy.setDamageMap(new HashMap<>(damageMap));
      this.enemy = enemy;
      return enemy;
    }

    SerializableEnemy(Enemy enemy, SerializableWorld serializableWorld) {
      super(enemy, serializableWorld);
      health = enemy.getHealth();
      type = enemy.getType().getTypeName();
      velocity = enemy.getVelocity();
      waveNumber = enemy.getWaveNumber();
      isDead = enemy.isDead();
      x = enemy.getPosition().getX();
      y = enemy.getPosition().getY();
      xTrajectory = enemy.getTrajectory().stream().map(Vector2::getX).collect(Collectors.toList());
      yTrajectory = enemy.getTrajectory().stream().map(Vector2::getY).collect(Collectors.toList());
      moneyReward = enemy.getMoneyReward();
      for (Effect effect : enemy.getEffects()) {
        if (serializableWorld.idMap.containsKey(effect)) {
          effects.add(serializableWorld.idMap.get(effect));
        } else {
          var sEffect = new SerializableEffect(effect, serializableWorld);
          serializableWorld.ideffectMap.put(sEffect.id, sEffect);
          effects.add(sEffect.id);
        }
      }
      damageMap = new HashMap<>(enemy.getDamageMap());
    }
    int health;
    String type; //EnemyType type;
    float velocity;
    int waveNumber;
    boolean isDead;

    double x;
    double y;//Vector2<Double> position;

    List<Double> xTrajectory;
    List<Double> yTrajectory; //List<Vector2<Double>> trajectory = new ArrayList<>();

    int moneyReward;
    List<Integer> effects = new ArrayList<>(); // List<Effects>
    Map<String, Integer> damageMap;
  }

  static class SerializableTower extends SerializableEntity {
    private Tower tower = null;
    Tower getTower() {
      if (tower != null) {
        return tower;
      }
      Tower tower = new Tower();
      tower.setType(GameMetaData.getInstance().getTowerType(towerType));
      tower.setCooldown(cooldown);
      tower.setRotation(rotation);
      System.out.println(enemyId);
      if (serializableWorld.idenemyMap.containsKey(enemyId) && !serializableWorld.idenemyMap.get(enemyId).isDead) {
        tower.setTarget(serializableWorld.idenemyMap.get(enemyId).getEnemy());
      }
      tower.setPosition(new Vector2<>(x, y));
      tower.setMode(mode);
      tower.setSellPrice(sellPrice);
      tower.setOwner(owner);
      this.tower = tower;
      return tower;
    }

    SerializableTower(Tower tower, SerializableWorld serializableWorld) {
      super(tower, serializableWorld);
      towerType = tower.getType().getTypeName();
      cooldown = tower.getCooldown();
      rotation = tower.getRotation();
      if (tower.getTarget() != null && !tower.getTarget().isDead()) {
        if (serializableWorld.idMap.containsKey(tower.getTarget())) {
          enemyId = serializableWorld.idMap.get(tower.getTarget());
        } else {
          var sEnemy = new SerializableEnemy(tower.getTarget(), serializableWorld);
          enemyId = sEnemy.id;
          serializableWorld.idenemyMap.put(sEnemy.id, sEnemy);
        }
      }
      x = tower.getCell().getX();
      y = tower.getCell().getY();
      mode = tower.getMode();
      sellPrice = tower.getSellPrice();
      owner = tower.getOwner();
    }

    String towerType; //TowerType type;
    int cooldown;
    double rotation;
    int enemyId; //Enemy target;

    int x;
    int y; //Vector2<Integer> position;

    Mode mode;
    int sellPrice;
    String owner;
  }

  static class SerializableProjectile extends SerializableEntity {
    private Projectile projectile = null;

    Projectile getProjectile() {
      if (projectile != null) {
        return projectile;
      }
      Projectile projectile = new Projectile();
      if (serializableWorld.idenemyMap.containsKey(enemyId)) {
        projectile.setTarget(serializableWorld.idenemyMap.get(enemyId).getEnemy());
      }

      projectile.setRemainingRange(remainingRange);
      projectile.setType(GameMetaData.getInstance().getProjectileType(projectileType));
      projectile.setPosition(new Vector2<>(x, y));
      projectile.setVelocity(new Vector2<>(xVelocity, yVelocity));
      projectile.setRotation(rotation);
      projectile.setScale(scale);
      projectile.setRotationSpeed(rotationSpeed);
      projectile.setFireType(fireType);
      projectile.setParentPosition(new Vector2<>(parentX, parentY));
      projectile.setOwner(owner);
      this.projectile = projectile;
      return projectile;
    }

    SerializableProjectile(Projectile projectile, SerializableWorld serializableWorld) {
      super(projectile, serializableWorld);
      if (projectile.getTarget() != null && !projectile.getTarget().isDead()) {
        if (serializableWorld.idMap.containsKey(projectile.getTarget())) {
          enemyId = serializableWorld.idMap.get(projectile.getTarget());
        } else {
          var sEnemy = new SerializableEnemy(projectile.getTarget(), serializableWorld);
          enemyId = sEnemy.id;
          serializableWorld.idenemyMap.put(sEnemy.id, sEnemy);
        }
      }
      remainingRange = projectile.getRemainingRange();
      projectileType = projectile.getType().getTypeName();
      x = projectile.getPosition().getX();
      y = projectile.getPosition().getY();
      xVelocity = projectile.getVelocity().getX();
      yVelocity = projectile.getVelocity().getY();
      rotation = projectile.getRotation();
      scale = projectile.getScale();
      rotationSpeed = projectile.getRotationSpeed();
      fireType = projectile.getFireType();
      parentX = projectile.getParentPosition().getX();
      parentY = projectile.getParentPosition().getY();
      owner = projectile.getOwner();
    }

    int enemyId; //Enemy target;
    float remainingRange;
    String projectileType; //ProjectileType type;

    double x;
    double y; //Vector2<Double> position;

    double xVelocity;
    double yVelocity; //Vector2<Double> velocity;

    double rotation;
    double scale;
    double rotationSpeed;
    FireType fireType;

    double parentX;
    double parentY;//Vector2<Double> parentPosition;

    String owner;
  }

  static class SerializableTowerPlatform extends SerializableEntity {
    private TowerPlatform towerPlatform = null;

    TowerPlatform getTowerPlatform() {
      if (towerPlatform != null) {
        return towerPlatform;
      }
      TowerPlatform towerPlatform = new TowerPlatform(new Vector2<>(x, y), image);
      this.towerPlatform = towerPlatform;
      return towerPlatform;
    }

    SerializableTowerPlatform(TowerPlatform towerPlatform, SerializableWorld serializableWorld) {
      super(towerPlatform, serializableWorld);
      x = towerPlatform.getCell().getX();
      y = towerPlatform.getCell().getY();
      image = towerPlatform.getImageName();
    }

    int x;
    int y;//Vector2<Integer> position;

    String image;
  }

  static class SerializableRoadTile extends SerializableEntity {
    private RoadTile roadTile = null;
    RoadTile getRoadTile() {
      if (roadTile != null) {
        return roadTile;
      }
      roadTile = new RoadTile(image, new Vector2<>(x, y));
      return roadTile;
    }
    SerializableRoadTile(RoadTile roadTile, SerializableWorld serializableWorld) {
      super(roadTile, serializableWorld);
      x = roadTile.getCell().getX();
      y = roadTile.getCell().getY();
      image = roadTile.getImageName();
    }

    int x;
    int y;//Vector2<Integer> position;

    String image;
  }

  static class SerializablePortal extends SerializableEntity {
    private Portal portal = null;
    Portal getPortal() {
      if (portal != null) {
        return portal;
      }
      portal = new Portal(image, new Vector2<>(x, y));
      return portal;
    }
    SerializablePortal(Portal portal, SerializableWorld serializableWorld) {
      super(portal, serializableWorld);
      x = portal.getCell().getX();
      y = portal.getCell().getY();
      image = portal.getImageName();
    }

    int x;
    int y;//Vector2<Integer> position;

    String image;
  }

  static class SerializableWave extends SerializableEntity {
    private Wave wave = null;

    Wave getWave() {
      if (wave != null) {
        return wave;
      }
      wave = new Wave();
      wave.setNumber(number);
      wave.setRemainingEnemiesCount(remainingEnemiesCount);
      wave.setCurrentEnemyNumber(currentEnemyNumber);
      wave.setDescription(description);
      return wave;
    }

    SerializableWave(Wave wave, SerializableWorld serializableWorld) {
      super(wave, serializableWorld);
      number = wave.getNumber();
      remainingEnemiesCount = wave.getRemainingEnemiesCount();
      currentEnemyNumber = wave.getCurrentEnemyNumber();
      description = wave.getDescription();
    }

    int number;
    int remainingEnemiesCount;
    int currentEnemyNumber;
    WaveDescription description; // this is already serializable
  }

  static class SerializableBase extends SerializableEntity {
    private Base base = null;
    Base getBase() {
      if (base != null) {
        return base;
      }
      base = new Base(health, image, new Vector2<>(x, y));
      return base;
    }
    SerializableBase(Base base, SerializableWorld serializableWorld) {
      super(base, serializableWorld);
      health = base.getHealth();
      image = base.getImageName();
      x = base.getCell().getX();
      y = base.getCell().getY();
    }

    int health;

    String image;

    int x;
    int y; //Vector2<Integer> position;
  }

  static class SerializableEffect extends SerializableEntity {
    private Effect effect;

    Effect getEffect() {
      if (effect != null) {
        return effect;
      }
      Enemy enemy;
      if (serializableWorld.idenemyMap.containsKey(host)) {
        enemy = serializableWorld.idenemyMap.get(host).getEnemy();
      } else {
        enemy = null;
      }
      effect = new Effect(enemy, GameMetaData.getInstance().getEffectType(effectType), owner);
      effect.setRemainingTicks(remainingTicks);
      return effect;
    }

    SerializableEffect(Effect effect, SerializableWorld serializableWorld) {
      super(effect, serializableWorld);
      if (effect.getHost() != null && !effect.getHost().isDead()) {
        if (serializableWorld.idMap.containsKey(effect.getHost())) {
          host = serializableWorld.idMap.get(effect.getHost());
        } else {
          var sEnemy = new SerializableEnemy(effect.getHost(), serializableWorld);
          host = sEnemy.id;
          serializableWorld.idenemyMap.put(sEnemy.id, sEnemy);
        }
      }
      effectType = effect.getType().getName();
      remainingTicks = effect.getRemainingTicks();
      owner = effect.getOwner();

    }

    int host; //Enemy host;
    String effectType; //EffectType type;
    int remainingTicks;
    String owner;
  }
}
