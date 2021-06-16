package ru.nsu.fit.towerdefense.server.session;

import ru.nsu.fit.towerdefense.multiplayer.GameType;
import ru.nsu.fit.towerdefense.multiplayer.entities.SLobby;
import ru.nsu.fit.towerdefense.server.players.PlayerManager;
import ru.nsu.fit.towerdefense.server.sockets.receivers.Messenger;
import ru.nsu.fit.towerdefense.simulator.events.Event;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class SessionController {

	private SessionInfo info;

	private static final int INVITE_LIMIT = 10;
	private HashMap<String, Messenger> connections;
	private ConcurrentHashMap<String, Object> ready;
	private GameController controller;
	private Thread gameThread;


	public SessionController(long id, GameType type, String level)
	{
		info = new SessionInfo(id, type, level);
		connections = new HashMap<>();
		ready = new ConcurrentHashMap<>();
	}

	public String addOwner(String owner)
	{
		String token = generateOwnerToken(owner);
		info.addActiveToken(owner, token);
		PlayerManager.getInstance().addGameToken(token, owner, info.getId());
		info.setOwner(owner);
		return token;
	}

	public long getSessionId()
	{
		return info.getId();
	}


	private String generateToken(String player)
	{
		try
		{
			return Base64.getEncoder().encodeToString((info.getId() + " : " + Arrays.toString(MessageDigest.getInstance("SHA-256").digest(("" + System.nanoTime()).getBytes()))).getBytes());
		}
		catch (Exception ignored) {}
		return null;
	}

	public String generateInviteToken(String player)
	{
		if (info.getInvitedPlayers() == INVITE_LIMIT || info.getFreeSpace() == 0) return null;
		String token = generateToken(player);
		if (token == null) return null;
		info.addInviteToken(player, token);
		return token;
	}

	public String generateOwnerToken(String player)
	{
		String token = generateToken(player);
		if (token == null) return null;
		info.addActiveToken(player, token);
		return token;
	}

	public void leaveSession(String player)
	{
		info.removePlayerToken(player);
	}

	public String getActiveToken(String player)
	{
		return info.getActiveToken(player);
	}

	public void connect(Messenger messenger, String token)
	{
		System.out.println("acceptInvite");
		info.acceptInvite(token, info.getInvitedPlayerByToken(token));
		connections.put(token, messenger);
	}

	public void runGame()
	{
		if (info.getType() == GameType.COOPERATIVE)
		{
			controller = new CooperativeGame(info.getLevel(), info.getPlayers(), this);
		}
		else
		{

		}
		gameThread = new Thread(() -> controller.run());
	}

	public void receiveGameEvent(String eventMessage, String player)
	{
		Event event = Event.deserialize(eventMessage);
		controller.sendEvent(player, event);
	}

	public void switchReady(String player)
	{
		if (ready.contains(player))
		{
			ready.remove(player);
		}
		else
		{
			ready.put(player, player);
		}
	}

	public String getConnectedPlayerByToken(String token)
	{
		return info.getConnectedPlayerByToken(token);
	}
	public String getPlayerByToken(String token)
	{
		return info.getPlayerByToken(token);
	}


	public synchronized boolean disconnectPlayer(String player)
	{
		if (connections.containsKey(player))
		{
			connections.remove(player);
			return true;
		}
		else return false;
	}


	public void sendMessage(String message, String player)
	{
		connections.get(player).sendMessage(message);
	}
	public void sendMessageToAll(String message) {connections.values().forEach(e -> e.sendMessage(message));}
	public SLobby getInfo()
	{
		SLobby lobby = new SLobby();
		lobby.setPlayers(info.getPlayers());
		lobby.setMaxPlayers(info.getPlayersNumber());
		lobby.setLevelName(info.getLevelName());
		lobby.setId("" + info.getId());
		lobby.setGameType(info.getType());
		return lobby;
	}

	public boolean isTokenValid(String token)
	{
		return info.isTokenValid(token);
	}


}
