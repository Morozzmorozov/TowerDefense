package ru.nsu.fit.towerdefense.metadata.techtree;

import javax.print.attribute.standard.RequestingUserName;
import java.util.*;

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

	public List<List<Research>> getSortedResearches() throws NoSuchElementException
	{
		Queue<Research> queue = new LinkedList<>();
		HashMap<Research, Integer> nameToLevel = new HashMap<>();
		HashMap<Research, Integer> nameToLeft = new HashMap<>();
		int rest = 0;
 		for (var x : research)
	    {
	    	rest++;
	    	nameToLeft.put(x, x.getLeft());
	    	nameToLevel.put(x, 0);
	    }
		for (var x : availableResearches)
		{
			queue.add(x);
			nameToLevel.put(x, 0);
		}

		List<List<Research>> result = new ArrayList<>();


 		while (queue.size() > 0)
	    {
	    	Research r = queue.poll();
	    	int cur = nameToLevel.get(r);
	    	if (result.size() <= cur)
		    {
		    	List<Research> list = new LinkedList<>();
		    	result.add(list);
		    }
	    	result.get(cur).add(r);

	    	rest--;

	    	for (var to : r.getInfluence())
		    {
		    	int t = nameToLeft.get(to);
		    	int level = nameToLevel.get(to);
		    	level = Math.max(level, cur + 1);
		    	nameToLeft.put(to, t - 1);
			    nameToLevel.put(to, level);
		    	if (t == 1)
			    {
			    	queue.add(to);
			    }
		    }
	    }
 		if (rest > 0)
	    {
	    	throw new NoSuchElementException("Tree has cycle");
	    }
 		return result;
	}


}
