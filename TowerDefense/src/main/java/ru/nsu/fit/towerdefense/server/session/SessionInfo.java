package ru.nsu.fit.towerdefense.server.session;

import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.multiplayer.GameType;
import ru.nsu.fit.towerdefense.multiplayer.entities.SPlayer;
import ru.nsu.fit.towerdefense.server.database.PlayersDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SessionInfo {

	private GameMap level;
	private String levelName;

	private GameType type;
	private String owner;

	private int playersNumber;
	private int readyCnt;

	private HashMap<String, SPlayer> userInfo;
	private HashMap<String, String> tokenToUser;
	private HashMap<String, String> userToToken;

	private HashMap<String, String> inviteTokens;
	private HashMap<String, String> invitePlayers;

	private final long id;

	public SessionInfo(long id, GameType type, String level)
	{
		userInfo = new HashMap<>();
		this.id = id;
		this.levelName = level;
		this.level = GameMetaData.getInstance().getMapDescription(level);
		this.type = type;
		this.playersNumber = this.level.getPlayersNumber();
		tokenToUser = new HashMap<>();
		inviteTokens = new HashMap<>();
		userToToken = new HashMap<>();
		invitePlayers = new HashMap<>();
		readyCnt = 0;
	}

	public void setLevel(String level) {
		this.levelName = level;
		this.level = GameMetaData.getInstance().getMapDescription(level);
		this.playersNumber = this.level.getPlayersNumber();
	}

	public void setType(GameType type)
	{
		this.type = type;
	}

	public long getId()
	{
		return id;
	}

	public GameMap getLevel()
	{
		return level;
	}

	public GameType getType()
	{
		return type;
	}


	public boolean addInviteToken(String player, String token)
	{
		synchronized (this)
		{
			if (tokenToUser.size() < playersNumber)
			{
				inviteTokens.put(token, player);
				invitePlayers.put(player, token);
				return true;
			}
			return false;
		}
	}

	public void addActiveToken(String player, String token)
	{
		synchronized (this)
		{
			tokenToUser.put(token, player);
			userToToken.put(player, token);
		}
	}

	public boolean acceptInvite(String token, String user)
	{
		synchronized (this)
		{
			if (tokenToUser.size() < playersNumber && !tokenToUser.containsKey(token) && inviteTokens.containsKey(token))
			{
				SPlayer info = PlayersDatabase.getInstance().getPlayerInfo(user);

				inviteTokens.remove(token, user);
				invitePlayers.remove(user, token);
				tokenToUser.put(token, user);
				userToToken.put(user, token);
				userInfo.put(user, info);
				if (tokenToUser.size() == playersNumber)
				{
					invitePlayers.clear();
					inviteTokens.clear();
				}
				return true;
			}
			return false;
		}
	}

	public int getInvitedPlayers()
	{
		synchronized (this)
		{
			return inviteTokens.size();
		}
	}

	public String getPlayerToken(String player)
	{
		if (userToToken.containsKey(player)) return userToToken.get(player);
		if (invitePlayers.containsKey(player)) return inviteTokens.get(player);
		return null;
	}

	public void removePlayerToken(String player)
	{
		String token = getPlayerToken(player);
		if (token == null) return;
		userToToken.remove(player, token);
		tokenToUser.remove(token, player);
		invitePlayers.remove(player, token);
		inviteTokens.remove(token, player);
		userInfo.remove(player);
	}

	public String getActiveToken(String player)
	{
		return userToToken.get(player);
	}
	public String getInviteToken(String player) { return invitePlayers.get(player);}

	public List<SPlayer> getPlayers()
	{
		synchronized (this)
		{
			return new ArrayList<>(userInfo.values());
		}
	}

	public String getConnectedPlayerByToken(String token)
	{
		return tokenToUser.get(token);
	}


	public String getInvitedPlayerByToken(String token)
	{
		return inviteTokens.get(token);
	}

	public String getPlayerByToken(String token)
	{
		if (inviteTokens.containsKey(token)) return inviteTokens.get(token);
		if (tokenToUser .containsKey(token)) return  tokenToUser.get(token);
		return null;
	}

	public int getFreeSpace()
	{
		return playersNumber - tokenToUser.size();
	}

	public void setOwner(String owner)
	{
		this.owner = owner;
	}

	public String getOwner()
	{
		return owner;
	}

	public int getPlayersNumber()
	{
		return playersNumber;
	}

	public String getLevelName()
	{
		return levelName;
	}

	public synchronized boolean isTokenValid(String token)
	{
		return tokenToUser.containsKey(token) || inviteTokens.containsKey(token);
	}

	public void switchReady(String player)
	{
		SPlayer info = userInfo.get(player);
		if (info != null)
		{
			if (info.getReady()) readyCnt--;
			else readyCnt++;

			info.setReady(!info.getReady());
		}
	}

	public boolean canStart()
	{
		return readyCnt == playersNumber;
	}
}
