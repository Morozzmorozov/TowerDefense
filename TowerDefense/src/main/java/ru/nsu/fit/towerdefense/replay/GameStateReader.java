package ru.nsu.fit.towerdefense.replay;

import javafx.util.Pair;
import ru.nsu.fit.towerdefense.replay.objectInfo.EnemyInfo;
import ru.nsu.fit.towerdefense.replay.objectInfo.ProjectileInfo;
import ru.nsu.fit.towerdefense.replay.objectInfo.TowerInfo;
import ru.nsu.fit.towerdefense.simulator.util.Vector2;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameStateReader {

	private static GameStateReader instance = null;

	private XMLInputFactory factory = null;

	private XMLStreamReader reader = null;

	private String replayDir = "./Replays/";

	private GameStateReader()
	{
		factory = XMLInputFactory.newFactory();
	}

	public static GameStateReader getInstance()
	{
		if (instance == null) instance = new GameStateReader();
		return instance;
	}

	public String[] getReplays(String level)
	{
		String dir = replayDir + level + "/";

		File parent = new File(dir);
		if (!parent.exists() && !parent.mkdirs())
		{
			return new String[0];
		}
		return parent.list();
	}



	public Replay readReplay(String level, String name)
	{
		try
		{
			String dir = replayDir + level + "/" + name;
			File file = new File(dir);
			ArrayList<WorldState> states = new ArrayList<>();
			ArrayList<EventRecord> records = new ArrayList<>();

			XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(file));
			int tickRate = 0;

			reader.next();
			tickRate = Integer.parseInt(reader.getAttributeValue(0));
			reader.next();
			while (reader.hasNext())
			{
				if (reader.getEventType() == XMLStreamConstants.START_ELEMENT)
				{
					int id = Integer.parseInt(reader.getAttributeValue(0));
					String type = reader.getAttributeValue(1);
					reader.next();
					if (type.equals("true"))
					{
						states.add(parseState(id, reader));
					}
					else
					{
						records.add(parseRecord(id, reader));
					}
				}
				else
				{
					reader.next();
				}
			}
			Replay replay = new Replay(tickRate, records, states);
			return replay;
		}
		catch (Exception e)
		{
			return null;
		}
	}


	private WorldState parseState(int fid, XMLStreamReader reader1)
	{
		try
		{
			ArrayList<EnemyInfo> enemies = new ArrayList<>();
			ArrayList<TowerInfo> towers = new ArrayList<>();
			ArrayList<ProjectileInfo> projectiles = new ArrayList<>();
			int money = 0;
			int health = 0;
			while (true)
			{
				int event = reader1.getEventType();
				if (event == XMLStreamConstants.START_ELEMENT)
				{
					switch (reader1.getLocalName())
					{
						case "Base" -> health = Integer.parseInt(reader1.getAttributeValue(0));
						case "Enemy" -> enemies.add(parseEnemy(reader1));
						case "Tower" -> towers.add(parseTower(reader1));
						case "Projectile" -> projectiles.add(parseProjectile(reader1));
						default -> money = Integer.parseInt(reader1.getAttributeValue(0));
					}
				}
				else if (event == XMLStreamConstants.END_ELEMENT)
				{
					if (reader1.getLocalName().equals("Frame")) break;
				}
				reader1.next();
			}
			return new WorldState(enemies, towers, projectiles, money, fid, health);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private EnemyInfo parseEnemy(XMLStreamReader reader1)
	{
		try
		{
			String type = "";
			Vector2<Double> position = new Vector2<>(0.0, 0.0);
			int wave = 0;
			String id = "";
			int health = 0;
			ArrayList<Vector2<Double>> trajectory = new ArrayList<>();
			EnemyInfo info = new EnemyInfo();
			while (true)
			{
				int event = reader1.getEventType();
				if (event == XMLStreamConstants.START_ELEMENT)
				{
					if (reader1.getLocalName().equals("Enemy"))
					{
						id = reader1.getAttributeValue(0);
						health = Integer.parseInt(reader1.getAttributeValue(1));
						info.setHealth(health);
						position.setX(Double.parseDouble(reader1.getAttributeValue(2)));
						position.setY(Double.parseDouble(reader1.getAttributeValue(3)));

						type = reader1.getAttributeValue(4);
						wave = Integer.parseInt(reader1.getAttributeValue(5));
					}
					else if (reader1.getLocalName().equals("Point")) // trajectory
					{
						trajectory.add(new Vector2<>(Double.parseDouble(reader1.getAttributeValue(0)), Double.parseDouble(reader1.getAttributeValue(1))));
					}
				}
				else if (event == XMLStreamConstants.END_ELEMENT)
				{
					if (reader1.getLocalName().equals("Enemy"))
						break;
				}
				reader1.next();
			}
			info.setId(id);
			info.setHealth(health);
			info.setPosition(position);
			info.setType(type);
			info.setWave(wave);
			info.setTrajectory(trajectory);
			return info;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private TowerInfo parseTower(XMLStreamReader reader1)
	{
		try
		{
			TowerInfo info = new TowerInfo();
			info.setId(reader1.getAttributeValue(0));
			info.setType(reader1.getAttributeValue(1));
			info.setPosition(new Vector2<>(Double.parseDouble(reader1.getAttributeValue(2)), Double.parseDouble(reader1.getAttributeValue(3))));
			info.setMode(Tower.Mode.valueOf(reader1.getAttributeLocalName(4)));
			info.setRotation(Double.parseDouble(reader1.getAttributeValue(5)));
			info.setCooldown(Integer.parseInt(reader1.getAttributeValue(6)));
			info.setTarget(reader1.getAttributeValue(7));
			reader1.next();
			return info;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private ProjectileInfo parseProjectile(XMLStreamReader reader1)
	{
		try
		{
			ProjectileInfo info = new ProjectileInfo();

			info.setId(reader1.getAttributeValue(0));
			info.setTarget(reader1.getAttributeValue(1));
			info.setPosition(new Vector2<>(Double.parseDouble(reader1.getAttributeValue(2)), Double.parseDouble(reader1.getAttributeValue(3))));
			info.setType(reader1.getAttributeValue(4));
			info.setRange(Double.parseDouble(reader1.getAttributeValue(5)));
			return info;
		}
		catch (Exception e)
		{
			return null;
		}
	}



	private EventRecord parseRecord(int fid, XMLStreamReader reader1)
	{
		try
		{
			List<Pair<Integer, String>> buildTower = new LinkedList<>();
			List<Pair<Integer, String>> upgradeTower = new LinkedList<>();
			boolean callWave = false;
			List<String> removeEnemy = new LinkedList<>();
			List<String> removeProjectile = new LinkedList<>();
			List<Pair<Integer, String>> enemyDamage = new LinkedList<>();
			List<Integer> damageToBase = new LinkedList<>();
			List<Pair<String, Tower.Mode>> tuneTower = new LinkedList<>();
			while (true)
			{
				int eventType = reader1.getEventType();
				if (eventType == XMLStreamConstants.START_ELEMENT)
				{
					switch (reader1.getLocalName())
					{
						case "BuildTower" -> {
							int position = Integer.parseInt(reader1.getAttributeValue(0));
							String type = reader1.getAttributeValue(1);
							buildTower.add(new Pair<>(position, type));
							break;
						}
						case "UpgradeTower" -> {
							int position = Integer.parseInt(reader1.getAttributeValue(0));
							String type = reader1.getAttributeValue(1);
							upgradeTower.add(new Pair<>(position, type));
							break;
						}
						case "CallWave" -> callWave = true;
						case "RemoveEnemy" -> {
							String id = reader1.getAttributeValue(0);
							removeEnemy.add(id);
							break;
						}
						case "RemoveProjectile" -> {
							String id = reader1.getAttributeValue(0);
							removeProjectile.add(id);
							break;
						}
						case "DealDamage" -> {
							String id = reader1.getAttributeValue(0);
							int amount = Integer.parseInt(reader1.getAttributeValue(1));
							enemyDamage.add(new Pair<>(amount, id));
							break;
						}
						case "DealDamageBase" -> {
							int amount = Integer.parseInt(reader1.getAttributeValue(0));
							damageToBase.add(amount);
							break;
						}
						case "SwitchMode" -> tuneTower.add(new Pair<>(reader1.getAttributeValue(0), Tower.Mode.valueOf(reader1.getAttributeValue(1))));
					}
				}
				else if (eventType == XMLStreamConstants.END_ELEMENT)
				{
					if (reader1.getLocalName().equals("Frame"))
						break;
				}
				reader1.next();
			}
			return new EventRecord(fid, buildTower, upgradeTower, callWave, removeEnemy, removeProjectile, enemyDamage, damageToBase, tuneTower);
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			return null;
		}
	}
}
