package ru.nsu.fit.towerdefense.server.sockets;

import ru.nsu.fit.towerdefense.server.sockets.receivers.Messenger;
import ru.nsu.fit.towerdefense.server.sockets.receivers.UnauthorisedMessenger;

public class UserConnection
{

	private GameSocket socket;
	private Messenger receiver;

	public UserConnection(GameSocket socket)
	{
		this.socket = socket;
		this.socket.setOwner(this);
		this.receiver = new UnauthorisedMessenger(this);
	}

	public void setReceiver(Messenger receiver){
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
