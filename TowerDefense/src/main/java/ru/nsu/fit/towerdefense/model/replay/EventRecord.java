package ru.nsu.fit.towerdefense.model.replay;

import javafx.util.Pair;
import ru.nsu.fit.towerdefense.model.world.gameobject.Tower;

import java.util.List;

public class EventRecord {
	private List<Pair<Integer, String>> buildTower;
	private List<Pair<Integer, String>> upgradeTower;
	private boolean callWave;
	private List<String> removeEnemy;
	private List<String> removeProjectile;
	private List<Pair<Integer, String>> enemyDamage;
	private List<Integer> damageToBase;
	private List<Pair<String, Tower.Mode>> tuneTower;
	private int id;

	public EventRecord(int id, List<Pair<Integer, String>> buildTower, List<Pair<Integer, String>> upgradeTower,
	                   boolean callWave, List<String> removeEnemy, List<String> removeProjectile, List<Pair<Integer, String>> enemyDamage,
	                   List<Integer> damageToBase, List<Pair<String, Tower.Mode>> tuneTower)
	{
		this.id = id;
		this.buildTower = buildTower;
		this.upgradeTower = upgradeTower;
		this.callWave = callWave;
		this.removeEnemy = removeEnemy;
		this.removeProjectile = removeProjectile;
		this.enemyDamage = enemyDamage;
		this.damageToBase = damageToBase;
		this.tuneTower = tuneTower;
	}

	public int getId()
	{
		return id;
	}

	public List<Integer> getDamageToBase()
	{

		return damageToBase;
	}

	public List<Pair<String, Tower.Mode>> getTuneTower()
	{
		return tuneTower;
	}

	public List<String> getRemoveEnemy()
	{
		return removeEnemy;
	}

	public List<String> getRemoveProjectile()
	{
		return removeProjectile;
	}

	public List<Pair<Integer, String>> getBuildTower()
	{
		return buildTower;
	}

	public List<Pair<Integer, String>> getEnemyDamage()
	{
		return enemyDamage;
	}

	public List<Pair<Integer, String>> getUpgradeTower()
	{
		return upgradeTower;
	}

	public boolean isCallWave()
	{
		return callWave;
	}

}
