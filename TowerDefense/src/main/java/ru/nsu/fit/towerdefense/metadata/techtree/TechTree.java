package ru.nsu.fit.towerdefense.metadata.techtree;

import ru.nsu.fit.towerdefense.metadata.UserMetaData;
import ru.nsu.fit.towerdefense.util.Vector2;

import java.util.*;
import java.util.stream.Collectors;

public class TechTree {
	private ArrayList<Research> research;
	private HashMap<String, Research> nameToResearch;
	private HashSet<String> availableTypes;

	private ArrayList<Research> availableResearches;

	private ArrayList<String> unlocked;

	private List<List<Research>> displayInfo;

	private Vector2<Integer> size;

	public TechTree(HashMap<String, Research> nameToResearch)
	{
		this.nameToResearch = nameToResearch;
		unlocked = new ArrayList<>();
		size = new Vector2<>(0, 0);
	}

	public void unlock(String name)
	{
		Research r = nameToResearch.get(name);
		var t = r.unlock();
		availableTypes.addAll(r.getTowerNames());
		availableResearches.addAll(t);
		availableResearches.remove(r);
		unlocked.add(name);
		UserMetaData.saveResearch(name);
	}


	public ArrayList<Research> getAvailableResearches()
	{
		return availableResearches;
	}

	public ArrayList<Research> getResearch()
	{
		return research;
	}

	public HashSet<String> getAvailableTypes()
	{
		return availableTypes;
	}

	public boolean getIsTypeAvailable(String name)
	{
		return availableResearches.contains(name);
	}

	public ArrayList<String> getUnlocked()
	{
		return unlocked;
	}


	public void setAvailableResearches(ArrayList<Research> availableResearches)
	{
		this.availableResearches = availableResearches;
	}

	public void setAvailableTypes(HashSet<String> availableTypes)
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


	public void process()
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

		displayInfo = new ArrayList<>();


		while (queue.size() > 0)
		{
			Research r = queue.poll();
			int cur = nameToLevel.get(r);
			if (displayInfo.size() <= cur)
			{
				List<Research> list = new LinkedList<>();
				displayInfo.add(list);
			}
			displayInfo.get(cur).add(r);

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

		int xsize = 50 + displayInfo.size() * 250;
		int ysize = 25;

		for (int i = 0; i < displayInfo.size(); i++)
		{
			for (int j = 0; j < displayInfo.get(i).size(); j++)
			{
				var t = displayInfo.get(i).get(j);
				t.setPosition(new Vector2<>(50 + i * 250, 25 + 75 * j));
			}
			ysize = Math.max(ysize, 25 + 75 * displayInfo.get(i).size());
		}

		size = new Vector2<>(xsize, ysize);

		if (rest > 0)
		{
			displayInfo = null;
		}

	}

	public Vector2<Integer> getSize()
	{
		return size;
	}

	public List<List<Research>> getSortedResearches() throws NoSuchElementException
	{
		if (displayInfo == null) throw new NoSuchElementException("Tree has cycle");
		return displayInfo;
	}

	public void loadUnlocked()
	{

		var t = UserMetaData.getUnlockedResearchNames(research.stream().map(Research::getName).collect(Collectors.toList()));
		for (var x : t)
		{
			unlock(x);
		}
	}

}
