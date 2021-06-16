package ru.nsu.fit.towerdefense.server.sockets;

import ru.nsu.fit.towerdefense.server.session.SessionController;
import ru.nsu.fit.towerdefense.server.session.SessionManager;
import ru.nsu.fit.towerdefense.server.sockets.receivers.EventMessenger;


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

	public boolean upgradeConnection(UserConnection connection, String token, Long sessionId)
	{

		SessionController controller;
		if ((controller = SessionManager.getInstance().getSessionById(sessionId)) != null)
		{
			String player = controller.getPlayerByToken(token);
			if (player == null) return false;
			EventMessenger messenger = new EventMessenger(player);
			messenger.setSession(controller);
			messenger.setOwner(connection);
			connection.setReceiver(messenger);
			controller.connect(messenger, token);
			return true;
		}
		return false;
	}

}
