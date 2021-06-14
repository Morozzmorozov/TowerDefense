package ru.nsu.fit.towerdefense.server.players;

public class PlayerInfo {
	private String name;
	private String authToken = null;
	private String gameToken = null;
	private Long lobbyId = -1L;

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

	public void setLobbyId(Long lobbyId)
	{
		this.lobbyId = lobbyId;
	}

	public String getName()
	{
		return name;
	}

	public Long getLobbyId()
	{
		return lobbyId;
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
