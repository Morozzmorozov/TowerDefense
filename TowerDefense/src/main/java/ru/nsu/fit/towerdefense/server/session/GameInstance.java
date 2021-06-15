package ru.nsu.fit.towerdefense.server.session;

import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.simulator.WorldControl;
import ru.nsu.fit.towerdefense.simulator.events.Event;
import ru.nsu.fit.towerdefense.simulator.world.SerializableWorld;

import java.util.List;

public class GameInstance {

	private WorldControl control;

	public GameInstance(GameMap map, List<String> players)
	{
		control = new WorldControl(map, 1000/60, null, players);
	}

	public void doStep()
	{
		control.simulateTick();
	}

	public void addEvent(Event event)
	{
		control.submitEvent(event);
	}

	public SerializableWorld serialize()
	{
		return control.getState();
	}


}
