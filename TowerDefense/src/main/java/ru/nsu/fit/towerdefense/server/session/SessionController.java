package ru.nsu.fit.towerdefense.server.session;

import com.google.gson.Gson;
import ru.nsu.fit.towerdefense.multiplayer.GameType;
import ru.nsu.fit.towerdefense.multiplayer.Message;
import ru.nsu.fit.towerdefense.multiplayer.entities.SLobby;
import ru.nsu.fit.towerdefense.multiplayer.entities.SPlayer;
import ru.nsu.fit.towerdefense.server.players.PlayerManager;
import ru.nsu.fit.towerdefense.server.session.gamecontrollers.CompetitiveGame;
import ru.nsu.fit.towerdefense.server.session.gamecontrollers.CooperativeGame;
import ru.nsu.fit.towerdefense.server.session.gamecontrollers.GameController;
import ru.nsu.fit.towerdefense.server.sockets.receivers.Messenger;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
		info.setOwnerInfo(owner);
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
			controller = new CooperativeGame(info.getLevel(), info.getPlayers().stream().map(SPlayer::getName).collect(Collectors.toList()), this);
		}
		else
		{
			controller = new CompetitiveGame(info.getLevel(), info.getPlayers().stream().map(SPlayer::getName).collect(Collectors.toList()), this);
		}
		gameThread = new Thread(() -> controller.run());
		gameThread.start();
	}

	public void receiveGameMessage(Message message, String player)
	{
		controller.sendMessage(player, message);
	}

	public void switchReady(String player)
	{
		info.switchReady(player);

		if (info.canStart())
		{
			Message message = new Message();
			message.setType(Message.Type.START);
			message.setPlayerNames(info.getPlayers().stream().map(SPlayer::getName).collect(Collectors.toList()));
			sendMessageToAll(new Gson().toJson(message));
			runGame();
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
		if (connections.containsKey(getActiveToken(player)))
		{
			connections.get(player).close();
			connections.remove(player);
			info.disconnectPlayer(player);
			if (controller != null)
			{
				controller.playerDisconnect(player);
			}
			PlayerManager.getInstance().disconnect(player);
			if (info.getConnectedPlayers() == 0)
			{
				SessionManager.getInstance().removeSession(this);
			}
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


	public boolean canJoin()
	{
		return info.getFreeSpace() > 0;
	}
}
