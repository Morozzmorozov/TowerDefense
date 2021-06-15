package ru.nsu.fit.towerdefense.server.session;

import ru.nsu.fit.towerdefense.simulator.events.Event;
import ru.nsu.fit.towerdefense.simulator.world.SerializableWorld;

public interface GameController {

	void sendEvent(String player, Event event);

	SerializableWorld getGameState(String owner);

	void run();
}
