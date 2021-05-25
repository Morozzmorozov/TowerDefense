package ru.nsu.fit.towerdefense.server.sockets.receivers;

public interface MessageReceiver {
	void receiveMessage(String message);

	void disconnect();
}
