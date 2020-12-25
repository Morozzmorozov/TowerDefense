package ru.nsu.fit.towerdefense.model.replay;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Replay {
	private int tickRate = 360;

	private List<EventRecord> eventRecords;
	private List<WorldState> worldStates;

	public Replay(int tickRate, List<EventRecord> eventRecords, List<WorldState> worldStates)
	{
		this.tickRate = tickRate;
		this.eventRecords = eventRecords;
		this.worldStates = worldStates;
	}

	public int getTickRate()
	{
		return tickRate;
	}

	public List<EventRecord> getEventRecords()
	{
		return eventRecords;
	}

	public List<WorldState> getWorldStates()
	{
		return worldStates;
	}

}
