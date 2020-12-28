package ru.nsu.fit.towerdefense.replay.objectInfo;

import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType;
import ru.nsu.fit.towerdefense.util.Vector2;

public class ProjectileInfo {
	private String id;
	private String target;
	private Vector2<Double> position;
	private Vector2<Double> velocity;
	private String type;
	private Double range;
	private TowerType.FireType fireType;
	private Double scale;
	private Vector2<Double> parentPosition;

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

	public Vector2<Double> getVelocity()
	{
		return velocity;
	}

	public Double getScale()
	{
		return scale;
	}

	public TowerType.FireType getFireType()
	{
		return fireType;
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

	public void setVelocity(Vector2<Double> velocity)
	{
		this.velocity = velocity;
	}

	public void setFireType(TowerType.FireType fireType)
	{
		this.fireType = fireType;
	}

	public void setScale(Double scale)
	{
		this.scale = scale;
	}

	public Vector2<Double> getParentPosition() {
		return parentPosition;
	}

	public void setParentPosition(Vector2<Double> parentPosition) {
		this.parentPosition = parentPosition;
	}
}
