package ru.nsu.fit.towerdefense.server.sockets.receivers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import ru.nsu.fit.towerdefense.multiplayer.entities.SGameSession;
import ru.nsu.fit.towerdefense.server.session.SessionController;
import ru.nsu.fit.towerdefense.server.session.SessionManager;
import ru.nsu.fit.towerdefense.server.sockets.ConnectionsManager;
import ru.nsu.fit.towerdefense.server.sockets.UserConnection;


public class UnauthorisedMessenger implements Messenger {

	private UserConnection owner;

	public UnauthorisedMessenger(UserConnection owner){
		this.owner = owner;
		owner.setReceiver(this);
	}

	@Override
	public void receiveMessage(String message)
	{
		ObjectMapper mapper = new ObjectMapper();
		try
		{
			SGameSession session = new Gson().fromJson(message, SGameSession.class);

			Long sessionId = Long.valueOf(session.getSessionId());


			if (!SessionManager.getInstance().isSessionExists(sessionId))
			{
				owner.sendMessage("{\"status\" : \"can't connect\"}");
				return;
			}

			SessionController controller = SessionManager.getInstance().getSessionById(sessionId);

			if (controller.isTokenValid(session.getSessionToken()))
			{
				if (ConnectionsManager.getInstance().upgradeConnection(owner, session.getSessionToken(), sessionId))
				{
					owner.sendMessage("{\"status\" : \"connected\"}");
					return;
				}
			}
			owner.sendMessage("{\"status\" : \"can't connect\"}");
			return;
		}
		catch (Exception e){
			owner.sendMessage("{\"status\" : \"invalid message\"}");
		}
	}

	@Override
	public void sendMessage(String message)
	{
		owner.sendMessage(message);
	}

	@Override
	public void disconnect()
	{
	}

	@Override
	public void close()
	{
		owner.close();
	}
}
