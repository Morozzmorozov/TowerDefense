package ru.nsu.fit.towerdefense.server.sockets;

import ru.nsu.fit.towerdefense.server.lobby.LobbyControl;
import ru.nsu.fit.towerdefense.server.lobby.LobbyManager;
import ru.nsu.fit.towerdefense.server.sockets.receivers.EventReceiver;


public class ConnectionsManager {
	private static final ConnectionsManager instance = new ConnectionsManager();

	private ConnectionsManager()
	{
	}

	public static ConnectionsManager getInstance()
	{
		return instance;
	}

	public synchronized void registerConnection(GameSocket socket)
	{
		UserConnection connection = new UserConnection(socket);
		socket.setOwner(connection);
	}

	public boolean upgradeConnection(UserConnection connection, String token, Long lobbyId)
	{
		EventReceiver receiver = new EventReceiver();
		LobbyControl control;

		if ((control = LobbyManager.getInstance().tryConnect(receiver, token, lobbyId)) != null)
		{
			receiver.setLobby(control);
			receiver.setOwner(connection);
			connection.setReceiver(receiver);
			return true;
		}
		return false;
	}

}
