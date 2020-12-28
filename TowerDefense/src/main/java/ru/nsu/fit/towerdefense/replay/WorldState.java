package ru.nsu.fit.towerdefense.replay;

import ru.nsu.fit.towerdefense.replay.objectInfo.EnemyInfo;
import ru.nsu.fit.towerdefense.replay.objectInfo.ProjectileInfo;
import ru.nsu.fit.towerdefense.replay.objectInfo.TowerInfo;

import java.util.List;

public class WorldState {
	private List<EnemyInfo> enemies;
	private List<TowerInfo> towers;
	private List<ProjectileInfo> projectiles;
	private int money;
	private int id;
	private int baseHealth;
	private int waveNumber;
	private int currentEnemyNumber;
	private int countdown;


	public WorldState(List<EnemyInfo> enemies, List<TowerInfo> towers, List<ProjectileInfo> projectiles,
			int money, int id, int baseHealth, int waveNumber, int currentEnemyNumber, int countdown)
	{
		this.baseHealth = baseHealth;
		this.enemies = enemies;
		this.money = money;
		this.towers = towers;
		this.projectiles = projectiles;
		this.id = id;
		this.waveNumber = waveNumber;
		this.currentEnemyNumber = currentEnemyNumber;
		this.countdown = countdown;
	}

	public int getBaseHealth()
	{
		return baseHealth;
	}

	public int getMoney()
	{
		return money;
	}

	public List<EnemyInfo> getEnemies()
	{
		return enemies;
	}

	public List<ProjectileInfo> getProjectiles()
	{
		return projectiles;
	}

	public List<TowerInfo> getTowers()
	{
		return towers;
	}

	public int getId()
	{
		return id;
	}

	public int getWaveNumber() {
		return waveNumber;
	}

	public int getCurrentEnemyNumber() {
		return currentEnemyNumber;
	}

	public int getCountdown() {
		return countdown;
	}
}
