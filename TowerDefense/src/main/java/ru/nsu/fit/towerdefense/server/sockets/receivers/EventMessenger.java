package ru.nsu.fit.towerdefense.server.sockets.receivers;

import com.google.gson.Gson;
import ru.nsu.fit.towerdefense.multiplayer.Message;
import ru.nsu.fit.towerdefense.server.session.SessionController;
import ru.nsu.fit.towerdefense.server.sockets.UserConnection;

public class EventMessenger implements Messenger {

	private SessionController session;
	private UserConnection owner;
	private String player;

	public EventMessenger(String player) {
		this.player = player;
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
		session.disconnectPlayer(player);
	}

	private void parseEvent(String message)
	{
		Message message1 = new Gson().fromJson(message, Message.class);
		switch (message1.getType())
		{
			case READY -> {System.out.println("Got switch ready message!"); session.switchReady(player);}
			case EVENT -> {System.out.println("Got event message!"); session.receiveGameMessage(message1, player); session.sendMessageToAll(message);}
			case RESULT-> {System.out.println("Got result message!"); session.receiveGameMessage(message1, player);}
			default -> {}
		}
	}

	public void setSession(SessionController session) {
		this.session = session;
	}

	public void setOwner(UserConnection connection)
	{
		this.owner = connection;
	}

}
