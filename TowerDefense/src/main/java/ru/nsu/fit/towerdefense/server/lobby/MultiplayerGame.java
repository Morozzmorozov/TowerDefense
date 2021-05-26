package ru.nsu.fit.towerdefense.server.lobby;

import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.simulator.ServerSimulator;
import ru.nsu.fit.towerdefense.simulator.WorldControl;
import ru.nsu.fit.towerdefense.simulator.WorldObserver;

import java.util.List;

public class MultiplayerGame {

	private ServerSimulator simulator;

	public MultiplayerGame(String mapName, WorldObserver observer, List<String> players)
	{
		simulator = new WorldControl(GameMetaData.getInstance().getMapDescription(mapName), 60, observer, players);
	}

}
