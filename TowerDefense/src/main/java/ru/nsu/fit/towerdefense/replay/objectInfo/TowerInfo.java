package ru.nsu.fit.towerdefense.replay.objectInfo;

import ru.nsu.fit.towerdefense.util.Vector2;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;

public class TowerInfo {
	private String id;
	private String type;
	private Vector2<Double> position;
	private Tower.Mode mode;
	private double rotation;
	private String target;
	private int cooldown;
	private int sellPrice;

	public TowerInfo(){}

	public double getRotation()
	{
		return rotation;
	}

	public int getCooldown()
	{
		return cooldown;
	}

	public Tower.Mode getMode()
	{
		return mode;
	}

	public String getId()
	{
		return id;
	}

	public String getTarget()
	{
		return target;
	}

	public String getType()
	{
		return type;
	}

	public int getSellPrice()
	{
		return sellPrice;
	}

	public Vector2<Double> getPosition()
	{
		return position;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public void setPosition(Vector2<Double> position)
	{
		this.position = position;
	}

	public void setCooldown(int cooldown)
	{
		this.cooldown = cooldown;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void setMode(Tower.Mode mode)
	{
		this.mode = mode;
	}

	public void setSellPrice(int sellPrice)
	{
		this.sellPrice = sellPrice;
	}

	public void setRotation(double rotation)
	{
		this.rotation = rotation;
	}

	public void setTarget(String target)
	{
		this.target = target;
	}

}
