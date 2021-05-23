package ru.nsu.fit.towerdefense.server.lobby;


import java.security.MessageDigest;
import java.util.*;

public class LobbyManager {

	private HashMap<Long, LobbyControl> lobbies;

	private long ids = 0;

	private static LobbyManager instance = new LobbyManager();

	private LobbyManager()
	{
		lobbies = new HashMap<>();
	}

	public static LobbyManager getInstance()
	{
		return instance;
	}

	public synchronized String createLobby()
	{
		LobbyControl lobby = new CooperativeLobbyControl(ids);
		lobbies.put(ids, lobby);
		ids++;
		return Long.toString(lobby.getId());
	}

	public List<LobbyControl> getLobbies()
	{
		ArrayList<LobbyControl> list = new ArrayList<>();
		for (var x : lobbies.entrySet())
		{
			if (x.getValue().canJoin())
				list.add(x.getValue());
		}
		list.sort((a, b) -> Integer.signum(a.getPlayersNumber() - b.getPlayersNumber() - a.getJoined() + b.getJoined()));
		return list;
	}

	public synchronized void removeLobby(Lobby lobby)
	{
		lobbies.remove(lobby);
	}


	public String createToken(String id) throws Exception{
		LobbyControl control = lobbies.get(Long.parseLong(id));
		String token = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest((control.getId() + " : " + System.nanoTime()).getBytes()));
		if (control.addAwaitingToken(token)) return token;
		return null;
	}

	public boolean isLobbyExists(String id){
		return lobbies.get(Long.parseLong(id)) != null;
	}

	public synchronized void leaveLobby(Long lobbyId, String token){
		if (lobbies.get(lobbyId).userLeaves(token)){
			lobbies.remove(lobbyId);
		}
	}

}
