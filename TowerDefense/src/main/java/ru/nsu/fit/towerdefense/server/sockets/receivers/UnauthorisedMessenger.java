package ru.nsu.fit.towerdefense.server.sockets.receivers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.nsu.fit.towerdefense.server.lobbyOld.LobbyManager;
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
			JsonNode node = mapper.readValue(message, JsonNode.class);
			Long lobbyId = node.get("lobbyId").asLong();
			String token = node.get("Token").asText();

			if (LobbyManager.getInstance().isTokenValid(lobbyId, token))
			{
				if (ConnectionsManager.getInstance().upgradeConnection(owner, token, lobbyId))
				{
					owner.sendMessage("{\"status\" : \"Connected\"}");
				}
				else
				{
					owner.sendMessage("{\"status\" : \"Can't connect\"}");
				}
			}
			else
			{
				owner.sendMessage("{\"status\" : \"Can't connect\"}");
			}
		}
		catch (Exception e){
			owner.sendMessage("{\"status\" : \"Invalid message\"}");
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


}
