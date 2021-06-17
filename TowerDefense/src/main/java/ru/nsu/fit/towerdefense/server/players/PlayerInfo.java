package ru.nsu.fit.towerdefense.server.players;

public class PlayerInfo {
	private String name;
	private String authToken = null;
	private String gameToken = null;
	private Long sessionId = -1L;

	public PlayerInfo(String name)
	{
		this.name = name;
	}

	public void setAuthToken(String authToken)
	{
		this.authToken = authToken;
	}

	public void setGameToken(String gameToken)
	{
		this.gameToken = gameToken;
	}

	public void setSessionId(Long lobbyId)
	{
		this.sessionId = lobbyId;
	}

	public String getName()
	{
		return name;
	}

	public Long getSessionId()
	{
		return sessionId;
	}

	public String getAuthToken()
	{
		return authToken;
	}

	public String getGameToken()
	{
		return gameToken;
	}
}
