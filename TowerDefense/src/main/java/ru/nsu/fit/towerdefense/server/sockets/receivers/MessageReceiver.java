package ru.nsu.fit.towerdefense.server.sockets.receivers;

public interface MessageReceiver {
	void receiveMessage(String message);

	void sendMessage(String message);

	void disconnect();
}
