package ru.nsu.fit.towerdefense.metadata.techtree;

import java.util.ArrayList;
import java.util.HashMap;

public class TechTree {
	private ArrayList<Research> research;
	private HashMap<String, Research> nameToResearch;
	private ArrayList<String> availableTypes;

	private ArrayList<Research> availableResearches;

	private ArrayList<String> unlocked;

	public TechTree(HashMap<String, Research> nameToResearch)
	{
		this.nameToResearch = nameToResearch;
		unlocked = new ArrayList<>();
	}



	public void unlock(String name)
	{
		Research r = nameToResearch.get(name);
		var t = r.unlock();
		availableTypes.addAll(r.getTowerNames());
		availableResearches.addAll(t);
		availableResearches.remove(r);
		unlocked.add(name);
	}


	public ArrayList<Research> getAvailableResearches()
	{
		return availableResearches;
	}

	public ArrayList<Research> getResearch()
	{
		return research;
	}

	public ArrayList<String> getAvailableTypes()
	{
		return availableTypes;
	}


	public ArrayList<String> getUnlocked()
	{
		return unlocked;
	}


	public void setAvailableResearches(ArrayList<Research> availableResearches)
	{
		this.availableResearches = availableResearches;
	}

	public void setAvailableTypes(ArrayList<String> availableTypes)
	{
		this.availableTypes = availableTypes;
	}

	public void setResearch(ArrayList<Research> research)
	{
		this.research = research;
	}

	public void setUnlocked(ArrayList<String> unlocked)
	{
		this.unlocked = unlocked;
	}

}
