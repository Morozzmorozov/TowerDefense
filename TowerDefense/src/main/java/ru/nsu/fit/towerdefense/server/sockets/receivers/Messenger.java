package ru.nsu.fit.towerdefense.server.sockets.receivers;

public interface Messenger {
	void receiveMessage(String message);

	void sendMessage(String message);

	void disconnect();
	void close();
}
