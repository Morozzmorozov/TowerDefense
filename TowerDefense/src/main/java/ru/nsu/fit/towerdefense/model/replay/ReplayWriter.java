package ru.nsu.fit.towerdefense.model.replay;

import ru.nsu.fit.towerdefense.model.world.gameobject.Base;
import ru.nsu.fit.towerdefense.model.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.model.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.model.world.gameobject.Tower;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ReplayWriter {


	private static ReplayWriter instance = null;

	private XMLOutputFactory factory = null;

	private XMLStreamWriter writer = null;

	private int currentTick = 0;
	private int fullCopy = 360;
	String tab = "\t\t\t\t\t\t\t";
	private ReplayWriter()
	{
		factory = XMLOutputFactory.newFactory();
	}

	public static ReplayWriter getInstance()
	{
		if (instance == null) instance = new ReplayWriter();
		return instance;
	}

	public void startNewReplay(int tickRate, String levelName)
	{
		try
		{
			int id = (int) (Math.random() * 1000);
			String name = "./Replays/" + levelName + "/Replay" + id + ".xml";
			File file = new File(name);
			File parent = file.getParentFile();
			if (!parent.exists() && !parent.mkdirs())
			{
				System.out.println("Ploho");
			}
			file.createNewFile();
			writer = factory.createXMLStreamWriter(new FileWriter(file));
			writer.writeStartDocument();
			writer.writeCharacters(System.getProperty("line.separator"));
			writer.writeStartElement("Replay");
			writer.writeAttribute("tickRate", Integer.toString(tickRate));
			writer.writeCharacters(System.getProperty("line.separator"));
			currentTick = 0;
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

  	}

	public void newFrame()
	{
		try
		{
			writer.writeCharacters(tab.substring(0, 1));
			writer.writeStartElement("Frame");
			writer.writeAttribute("frameId", Integer.toString(currentTick));
			writer.writeAttribute("isFullCopy", Boolean.toString((currentTick % fullCopy) == 0));
			writer.writeCharacters(System.getProperty("line.separator"));
		}
		catch (Exception e)
		{

		}
	}

	private List<Enemy> enemies = new ArrayList<>();
	private List<Tower> towers = new ArrayList<>();
	private List<Projectile> projectiles = new ArrayList<>();

	public void fullCopy(List<Enemy> enemies, List<Tower> towers, List<Projectile> projectiles, Base base)
	{
		if (currentTick % fullCopy != 0)
			return;
		try
		{
			writer.writeCharacters(tab.substring(0, 2));
			writer.writeStartElement("Base");
			writer.writeAttribute("Health", Integer.toString(base.getHealth()));
			writer.writeEndElement();
			writer.writeCharacters(System.getProperty("line.separator"));
			for (var x : enemies)
			{
				if (x.isDead()) continue;
				writer.writeCharacters(tab.substring(0, 2));
				writer.writeStartElement("Enemy");
				writer.writeAttribute("EnemyId", x.getId().toString());
				writer.writeAttribute("Health", Integer.toString(x.getHealth()));
				writer.writeAttribute("PosX", Double.toString(x.getPosition().getX()));
				writer.writeAttribute("PosY", Double.toString(x.getPosition().getY()));
				writer.writeAttribute("Type", x.getType().getTypeName());
				writer.writeAttribute("Wave", Integer.toString(x.getWave().getNumber()));
				writer.writeCharacters(System.getProperty("line.separator"));
				writer.writeCharacters(tab.substring(0, 3));
				writer.writeStartElement("Trajectory");
				writer.writeCharacters(System.getProperty("line.separator"));
				for (var y : x.getTrajectory())
				{
					writer.writeCharacters(tab.substring(0, 4));
					writer.writeStartElement("Point");
					writer.writeAttribute("PosX", Double.toString(y.getX()));
					writer.writeAttribute("PosY", Double.toString(y.getY()));
					writer.writeEndElement();
					writer.writeCharacters(System.getProperty("line.separator"));
				}
				writer.writeCharacters(tab.substring(0, 3));
				writer.writeEndElement();
				writer.writeCharacters(System.getProperty("line.separator"));
				writer.writeCharacters(tab.substring(0, 2));
				writer.writeEndElement();
				writer.writeCharacters(System.getProperty("line.separator"));
			}
			for (var x : towers)
			{
				writer.writeCharacters(tab.substring(0, 2));
				writer.writeStartElement("Tower");
				writer.writeAttribute("TowerId", x.getId().toString());
				writer.writeAttribute("Type", x.getType().getTypeName());
				writer.writeAttribute("PosX", Double.toString(x.getPosition().getX()));
				writer.writeAttribute("PosY", Double.toString(x.getPosition().getY()));
				writer.writeAttribute("Rotation", Double.toString(x.getRotation()));
				writer.writeAttribute("Cooldown", Integer.toString(x.getCooldown()));
				String target = "";
				if (x.getTarget() != null && !x.getTarget().isDead())
					target = x.getTarget().getId().toString();
				else
					target = "None";
				writer.writeAttribute("TargetId", target);
				writer.writeEndElement();
				writer.writeCharacters(System.getProperty("line.separator"));
			}

			for (var x : projectiles)
			{
				if (x.getRemainingRange() <= 0.0) continue;
				writer.writeCharacters(tab.substring(0, 2));
				writer.writeStartElement("Projectile");
				writer.writeAttribute("ProjectileId", x.getId().toString());
				String target = "";
				if (x.getTarget() != null && !x.getTarget().isDead())
					target = x.getTarget().getId().toString();
				else
					target = "None";
				writer.writeAttribute("TargetId", target);
				writer.writeAttribute("PosX", Double.toString(x.getPosition().getX()));
				writer.writeAttribute("PosX", Double.toString(x.getPosition().getY()));
				writer.writeAttribute("Type", x.getType().getTypeName());
				writer.writeAttribute("RemainingRange", Double.toString(x.getRemainingRange()));
				writer.writeEndElement();
				writer.writeCharacters(System.getProperty("line.separator"));

			}
		}
		catch (Exception e)
		{

		}
	}

	public void buildTower(int position, String typeName)
	{
		if (currentTick % fullCopy == 0)
			return;
		try
		{
			writer.writeCharacters(tab.substring(0, 2));
			writer.writeStartElement("BuildTower");
			writer.writeAttribute("Position", Integer.toString(position));
			writer.writeAttribute("Type", typeName);
			writer.writeEndElement();
			writer.writeCharacters(System.getProperty("line.separator"));
		}
		catch (Exception e)
		{

		}
	}

	public void upgradeTower(int position, String newType)
	{
		if (currentTick % fullCopy == 0)
			return;
		try
		{
			writer.writeCharacters(tab.substring(0, 2));
			writer.writeStartElement("UpgradeTower");
			writer.writeAttribute("Position", Integer.toString(position));
			writer.writeAttribute("NewType", newType);
			writer.writeEndElement();
			writer.writeCharacters(System.getProperty("line.separator"));
		}
		catch (Exception e)
		{

		}
	}

	public void callWave()
	{
		if (currentTick % fullCopy == 0)
			return;
		try
		{
			writer.writeCharacters(tab.substring(0, 2));
			writer.writeStartElement("CallWave");
			writer.writeEndElement();
			writer.writeCharacters(System.getProperty("line.separator"));
		}
		catch (Exception e)
		{

		}
	}

	public void removeEnemy(Enemy enemy)
	{
		if (currentTick % fullCopy == 0)
			return;
		try
		{
			writer.writeCharacters(tab.substring(0, 2));
			writer.writeStartElement("RemoveEnemy");
			writer.writeAttribute("EnemyId", enemy.getId().toString());
			writer.writeEndElement();
			writer.writeCharacters(System.getProperty("line.separator"));
		}
		catch (Exception e)
		{

		}
	}

	public void removeProjectile(Projectile projectile)
	{
		if (currentTick % fullCopy == 0)
			return;
		try
		{
			writer.writeCharacters(tab.substring(0, 2));
			writer.writeStartElement("RemoveProjectile");
			writer.writeAttribute("ProjectileId", projectile.getId().toString());
			writer.writeEndElement();
			writer.writeCharacters(System.getProperty("line.separator"));
		}
		catch (Exception e)
		{

		}
	}

	public void dealDamageEnemy(Enemy enemy, int damage)
	{
		if (currentTick % fullCopy == 0)
			return;
		try
		{
			writer.writeCharacters(tab.substring(0, 2));
			writer.writeStartElement("DealDamage");
			writer.writeAttribute("EnemyId", enemy.getId().toString());
			writer.writeAttribute("Damage", Integer.toString(damage));
			writer.writeEndElement();
			writer.writeCharacters(System.getProperty("line.separator"));
		}
		catch (Exception e)
		{

		}
	}

	public void dealDamageBase(int damage)
	{
		if (currentTick % fullCopy == 0)
			return;
		try
		{
			writer.writeCharacters(tab.substring(0, 2));
			writer.writeStartElement("DealDamageBase");
			writer.writeAttribute("Damage", Integer.toString(damage));
			writer.writeEndElement();
			writer.writeCharacters(System.getProperty("line.separator"));
		}
		catch (Exception e)
		{

		}
	}

	public void endFrame()
	{
		try
		{
			writer.writeCharacters(tab.substring(0, 1));
			writer.writeEndElement();
			writer.writeCharacters(System.getProperty("line.separator"));
			currentTick++;
		}
		catch (Exception e)
		{
		}
	}


	public void close()
	{
		try
		{
			writer.writeEndElement();
			writer.writeCharacters(System.getProperty("line.separator"));
			writer.writeEndDocument();
			writer.close();
		}
		catch (Exception e)
		{

		}
	}


}
