package ru.nsu.fit.towerdefense.server.session.gamecontrollers;

import ru.nsu.fit.towerdefense.multiplayer.Message;
import ru.nsu.fit.towerdefense.simulator.world.SerializableWorld;

public interface GameController {

	void sendMessage(String player, Message message);

	SerializableWorld getGameState(String owner);

	void run();
	void playerDisconnect(String player);
}
