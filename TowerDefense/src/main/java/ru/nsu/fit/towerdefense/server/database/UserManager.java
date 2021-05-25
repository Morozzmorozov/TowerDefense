package ru.nsu.fit.towerdefense.server.database;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;

public class UserManager {

	private final HashMap<String, String> tokenToPlayer;
	private final HashMap<String, String> playerToToken;

	private static UserManager instance = new UserManager();

	private UserManager()
	{
		tokenToPlayer = new HashMap<>();
		playerToToken = new HashMap<>();
	}

	public static UserManager getInstance()
	{
		return instance;
	}


	private String generateUserToken(String user)
	{
		try
		{
			String token;
			token = Base64.getEncoder().encodeToString((user + " : " + MessageDigest.getInstance("SHA-256").digest(Long.toString(System.nanoTime()).getBytes())
					+ Math.random() * 1e12).getBytes());
			if (playerToToken.containsKey(user))
			{
				tokenToPlayer.remove(playerToToken.get(user), user);
			}
			playerToToken.put(user, token);
			tokenToPlayer.put(token, user);
			return token;
		}
		catch (Exception e)
		{
			return null;
		}
	}


	public boolean tokenExists(String token)
	{
		return tokenToPlayer.containsKey(token);
	}

	public String validate(String user, String password)
	{
		return generateUserToken(user); //TODO: add credentials check
	}


}
