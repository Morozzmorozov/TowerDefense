package ru.nsu.fit.towerdefense.server.players;

import ru.nsu.fit.towerdefense.server.database.PlayersDatabase;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;

public class PlayerManager {
	private static PlayerManager instance = new PlayerManager();

	private HashMap<String, PlayerInfo> nameToPlayers;
	private HashMap<String, PlayerInfo> authTokenToPlayer;


	private PlayerManager()
	{
		nameToPlayers = new HashMap<>();
		authTokenToPlayer = new HashMap<>();
	}

	public static PlayerManager getInstance()
	{
		return instance;
	}

	public void createPlayer(String name)
	{
		synchronized (this)
		{
			PlayerInfo info = new PlayerInfo(name);
			nameToPlayers.put(name, info);
		}
	}

	public String generateToken(String player)
	{
		try
		{
			PlayerInfo info = nameToPlayers.get(player);
			String token = Base64.getEncoder().encodeToString((player + " : " + MessageDigest.getInstance("SHA-256").digest(Long.toString(System.nanoTime()).getBytes())
					+ Math.random() * 1e12).getBytes());
			if (info.getAuthToken() != null)
			{
				authTokenToPlayer.remove(info.getAuthToken());
			}
			authTokenToPlayer.put(token, info);
			info.setAuthToken(token);
			return token;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public String getPlayerByToken(String token)
	{
		if (authTokenToPlayer.containsKey(token))
		{
			return authTokenToPlayer.get(token).getName();
		}
		else
		{
			return null;
		}
	}

	public void addGameToken(String token, String player, Long lobbyId)
	{
		PlayerInfo info = nameToPlayers.get(player);
		info.setGameToken(token);
		info.setSessionId(lobbyId);
	}

	public boolean isConnected(String player)
	{
		return nameToPlayers.get(player).getSessionId() != -1L;
	}


	public String validate(String name, String password)
	{
		synchronized (this)
		{
			if (PlayersDatabase.getInstance().validate(name, password) == 0)
			{
				if (!nameToPlayers.containsKey(name))
				{
					createPlayer(name);
				}
				return generateToken(name);
			}
			return null;
		}
	}

	public void disconnect(String player)
	{
		synchronized(this)
		{
			nameToPlayers.get(player).setGameToken(null);
			nameToPlayers.get(player).setSessionId(-1L);
		}
	}

}
