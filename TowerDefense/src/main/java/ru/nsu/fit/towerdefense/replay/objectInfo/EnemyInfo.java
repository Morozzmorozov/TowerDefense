package ru.nsu.fit.towerdefense.replay.objectInfo;

import javafx.util.Pair;
import ru.nsu.fit.towerdefense.util.Vector2;
import java.util.List;

public class EnemyInfo {
	private String id;
	private String type;
	private int health;
	private int wave;
	private Vector2<Double> position;
	private List<Vector2<Double>> trajectory;
	private int reward;

	private List<Pair<String, Integer>> effects;

	public EnemyInfo() {}

	public void setType(String type)
	{
		this.type = type;
	}

	public void setHealth(int health)
	{
		this.health = health;
	}

	public void setPosition(Vector2<Double> position)
	{
		this.position = position;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void setTrajectory(List<Vector2<Double>> trajectory)
	{
		this.trajectory = trajectory;
	}

	public void setWave(int wave)
	{
		this.wave = wave;
	}

	public void setEffects(List<Pair<String, Integer>> effects)
	{
		this.effects = effects;
	}

	public int getHealth()
	{
		return health;
	}

	public int getWave()
	{
		return wave;
	}

	public List<Pair<String, Integer>> getEffects()
	{
		return effects;
	}

	public List<Vector2<Double>> getTrajectory()
	{
		return trajectory;
	}

	public String getId()
	{
		return id;
	}

	public String getType()
	{
		return type;
	}

	public Vector2<Double> getPosition()
	{
		return position;
	}

	public int getReward() {
		return reward;
	}

	public void setReward(int reward) {
		this.reward = reward;
	}
}
