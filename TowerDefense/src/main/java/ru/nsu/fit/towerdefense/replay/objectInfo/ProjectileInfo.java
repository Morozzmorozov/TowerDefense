package ru.nsu.fit.towerdefense.replay.objectInfo;

import ru.nsu.fit.towerdefense.util.Vector2;

public class ProjectileInfo {
	private String id;
	private String target;
	private Vector2<Double> position;
	private String type;
	private Double range;
	public ProjectileInfo(){}

	public Double getRange()
	{
		return range;
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

	public Vector2<Double> getPosition()
	{
		return position;
	}

	public void setRange(Double range)
	{
		this.range = range;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public void setPosition(Vector2<Double> position)
	{
		this.position = position;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void setTarget(String target)
	{
		this.target = target;
	}

}
