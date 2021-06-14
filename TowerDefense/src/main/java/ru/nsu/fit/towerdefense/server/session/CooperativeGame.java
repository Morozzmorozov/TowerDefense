package ru.nsu.fit.towerdefense.server.session;

import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.simulator.events.Event;
import ru.nsu.fit.towerdefense.simulator.world.SerializableWorld;

import java.util.List;

public class CooperativeGame implements GameController {

	private GameInstance instance;

	public CooperativeGame(GameMap map, List<String> players)
	{
		instance = new GameInstance(map, players);
	}


	@Override
	public void sendEvent(String player, Event event)
	{
		instance.addEvent(event);
	}

	@Override
	public SerializableWorld getGameState(String owner)
	{
		return instance.serialize();
	}

	@Override
	public void run()
	{

	}

}
