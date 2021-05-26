package ru.nsu.fit.towerdefense.server.lobby;


import ru.nsu.fit.towerdefense.server.sockets.UserConnection;
import ru.nsu.fit.towerdefense.server.sockets.receivers.MessageReceiver;

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
		synchronized (lobbies)
		{
			for (var x : lobbies.entrySet())
			{
				if (x.getValue().canJoin())
					list.add(x.getValue());
			}
		}
		list.sort((a, b) -> Integer.signum(a.getPlayersNumber() - b.getPlayersNumber() - a.getJoined() + b.getJoined()));
		return list;
	}

	public synchronized void removeLobby(LobbyControl lobby)
	{
		lobbies.remove(lobby.getId(), lobby);
	}


	public String createToken(String id, String userToken) throws Exception{
		LobbyControl control = lobbies.get(Long.parseLong(id));
		String token = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest((control.getId() + " : " + System.nanoTime()).getBytes()));
		if (control.addAwaitingToken(token, userToken)) return token;
		return null;
	}

	public boolean isLobbyExists(Long id){
		return lobbies.get(id) != null;
	}

	public synchronized void leaveLobby(Long lobbyId, String token, String userToken){
		if (lobbies.get(lobbyId).userLeaves(token, userToken)){
			lobbies.remove(lobbyId);
		}
	}


	public boolean isTokenValid(Long lobbyId, String token)
	{
		synchronized (lobbies)
		{
			if (!lobbies.containsKey(lobbyId)) return false;
			return lobbies.get(lobbyId).isTokenValid(token);
		}
	}

	public LobbyControl tryConnect(MessageReceiver receiver, String token, Long lobbyId)
	{
		synchronized (lobbies.get(lobbyId))
		{
			if (lobbies.get(lobbyId).addMessageReceiver(token, receiver)) return lobbies.get(lobbyId);
			return null;
		}
	}

	public LobbyControl getLobbyByID(long id)
	{
		return lobbies.get(id);
	}

}
