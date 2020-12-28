package ru.nsu.fit.towerdefense.metadata.techtree;

import ru.nsu.fit.towerdefense.util.Vector2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Research {

	private String name = "";
	private String info = "";
	private String image = "";

	//List of towertype names, which this research
	private List<String> towerNames;

	private int cost;

	//number of researcher to unlock this
	private int left;

	private List<Research> influence;

	private Vector2<Integer> size;
	private Vector2<Integer> position;

	public Research(String name, String info, List<String> towerNames, int cost, String image)
	{
		this.name = name;
		this.info = info;
		this.towerNames = towerNames;
		this.cost = cost;
		left = 0;
		influence = new ArrayList<>();
		this.image = image;
		size = new Vector2<>(50, 50);
		position = new Vector2<>(0, 0);
	}

	/**
	 * Update dependency and return list of researches, which can be unlocked
	 * @return new available nodes;
	 */
	public List<Research> unlock()
	{
		List<Research> research = new LinkedList<>();
		for (var x : influence)
		{
			x.left--;
			if (x.left == 0)
			{
				research.add(x);
			}
		}
		return research;
	}

	public void addDependency(Research to)
	{
		to.left++;
		influence.add(to);
	}


	public String getImage()
	{
		return image;
	}

	public int getCost()
	{
		return cost;
	}

	public String getName()
	{
		return name;
	}

	public int getLeft()
	{
		return left;
	}

	public String getInfo()
	{
		return info;
	}

	public Vector2<Integer> getSize()
	{
		return size;
	}

	public Vector2<Integer> getPosition()
	{
		return position;
	}

	public List<Research> getInfluence()
	{
		return influence;
	}

	public List<String> getTowerNames()
	{
		return towerNames;
	}

	public void setCost(int cost)
	{
		this.cost = cost;
	}

	public void setInfluence(List<Research> influence)
	{
		this.influence = influence;
	}

	public void setTowerNames(List<String> towerNames)
	{
		this.towerNames = towerNames;
	}

	public void setInfo(String info)
	{
		this.info = info;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setImage(String image)
	{
		this.image = image;
	}

	public void setSize(Vector2<Integer> size)
	{
		this.size = size;
	}

	public void setPosition(Vector2<Integer> position)
	{
		this.position = position;
	}

}
