import org.junit.Test;
import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.replay.GameStateWriter;
import ru.nsu.fit.towerdefense.util.Vector2;
import ru.nsu.fit.towerdefense.simulator.world.Wave;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Base;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.EnemyType;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType;

import java.util.ArrayList;

public class GameStateWriterTest {

	@Test
	public void test()
	{

		GameStateWriter writer = GameStateWriter.getInstance();
		writer.startNewReplay(360, "map1");
		ArrayList<Enemy> enemies = new ArrayList<>();
		ArrayList<Tower> tower = new ArrayList<>();
		ArrayList<Projectile> projectiles = new ArrayList<>();
		GameMetaData metaData = GameMetaData.getInstance();
		Base base = new Base(100, "",new Vector2<Integer>(1, 1));
		EnemyType goblin = metaData.getEnemyType("Goblin");
		TowerType archer = metaData.getTowerType("Archer");
		Wave wave = new Wave();
		wave.setNumber(0);
//		enemies.add(new Enemy(goblin, wave, new Vector2<Double>(10.0, 10.0), 5));
		tower.add(new Tower());

		tower.get(0).setType(archer);
		tower.get(0).setCooldown(20);
		tower.get(0).setPosition(new Vector2<>(3, 3));

		writer.newFrame();
		writer.fullCopy(enemies, tower, projectiles, base, 100);
		writer.endFrame();
		writer.newFrame();
		writer.endFrame();

		writer.newFrame();
		writer.dealDamageBase(3);
		writer.endFrame();
		writer.close();
	}

	@Test
	public void saveTest()
	{
		GameStateWriter writer = GameStateWriter.getInstance();

		ArrayList<Enemy> enemies = new ArrayList<>();
		ArrayList<Tower> tower = new ArrayList<>();
		ArrayList<Projectile> projectiles = new ArrayList<>();
		GameMetaData metaData = GameMetaData.getInstance();
		Base base = new Base(100, "",new Vector2<Integer>(1, 1));
		EnemyType goblin = metaData.getEnemyType("Goblin");
		TowerType archer = metaData.getTowerType("Archer");
		Wave wave = new Wave();
		wave.setNumber(0);
//		enemies.add(new Enemy(goblin, wave, new Vector2<Double>(10.0, 10.0), 5));
		tower.add(new Tower());

		tower.get(0).setType(archer);
		tower.get(0).setCooldown(20);
		tower.get(0).setPosition(new Vector2<>(3, 3));

		writer.saveGame("map1", enemies, tower, projectiles, base, 1000);
		writer.saveGame("Level 1", enemies, tower, projectiles, base, 1000);
	}

}
