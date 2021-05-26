package ru.nsu.fit.towerdefense.server.sockets.receivers;

import com.google.gson.Gson;
import ru.nsu.fit.towerdefense.multiplayer.Message;
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
	public void sendMessage(String message)
	{
		owner.sendMessage(message);
	}

	@Override
	public void disconnect()
	{
		lobby.connectedUserLeaves(this);
	}

	private void parseEvent(String message)
	{
		Message message1 = new Gson().fromJson(message, Message.class);
		switch (message1.getType())
		{
			case READY -> {System.out.println("Got switch ready message!"); lobby.switchReady(this);}
			case EVENT -> {System.out.println("Got event message!"); lobby.sendEvent(message1.getSerializedEvent());}
			default -> {}
		}
	}

	public void setLobby(LobbyControl lobby) {
		this.lobby = lobby;
	}

	public void setOwner(UserConnection connection)
	{
		this.owner = connection;
	}

}
