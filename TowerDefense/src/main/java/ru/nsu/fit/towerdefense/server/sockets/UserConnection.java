package ru.nsu.fit.towerdefense.server.sockets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.nsu.fit.towerdefense.server.sockets.receivers.MessageReceiver;
import ru.nsu.fit.towerdefense.server.sockets.receivers.UnauthorisedReceiver;

public class UserConnection
{
	public enum State
	{
		UNAUTHORIZED,
		NOT_READY,
		READY,
		IN_GAME
	}
	private GameSocket socket;
	private MessageReceiver receiver;

	public UserConnection(GameSocket socket)
	{
		this.socket = socket;
		this.socket.setOwner(this);
		this.receiver = new UnauthorisedReceiver(this);
	}

	public void setReceiver(MessageReceiver receiver){
		this.receiver = receiver;
	}

	public void receiveMessage(String message){
		receiver.receiveMessage(message);
	}

	public void sendMessage(String message)
	{
		socket.sendMessage(message);
	}

	public void disconnect() {receiver.disconnect();}

}