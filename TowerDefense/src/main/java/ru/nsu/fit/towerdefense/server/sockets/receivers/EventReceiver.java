package ru.nsu.fit.towerdefense.server.sockets.receivers;

import ru.nsu.fit.towerdefense.server.lobby.LobbyControl;
import ru.nsu.fit.towerdefense.server.sockets.UserConnection;

public class EventReceiver implements MessageReceiver {

	private LobbyControl lobby;
	private UserConnection owner;

	public EventReceiver() {
	}

	@Override
	public void receiveMessage(String message)
	{
		parseEvent(message);
	}

	@Override
	public void disconnect()
	{
		lobby.connectedUserLeaves(this);
	}

	private void parseEvent(String message)
	{

	}

	public void setLobby(LobbyControl lobby) {
		this.lobby = lobby;
	}

	public void setOwner(UserConnection connection)
	{
		this.owner = connection;
	}

}
