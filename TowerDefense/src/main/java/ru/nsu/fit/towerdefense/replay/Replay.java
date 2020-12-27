package ru.nsu.fit.towerdefense.replay;

import java.util.List;

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

	public int getReplayLength()
	{
		return eventRecords.size() + worldStates.size();
	}

}
