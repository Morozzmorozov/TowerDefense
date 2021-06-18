package ru.nsu.fit.towerdefense.server.session;

import ru.nsu.fit.towerdefense.multiplayer.GameType;
import ru.nsu.fit.towerdefense.multiplayer.entities.SGameSession;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SessionManager {

	private ConcurrentHashMap<Long, SessionController> activeSessions;
	private long sessionId = 1L;

	
	private static SessionManager instance = new SessionManager();

	private SessionManager()
	{
		activeSessions = new ConcurrentHashMap<>();
	}

	public static SessionManager getInstance()
	{
		return instance;
	}

	public synchronized SGameSession createSession(GameType type, String level, String owner)
	{
		SessionController controller = new SessionController(sessionId, type, level);
		String token = controller.addOwner(owner);
		long id = sessionId;
		activeSessions.put(sessionId, controller);
		sessionId++;
		if (sessionId <= 0) sessionId = 1L;
		SGameSession session = new SGameSession();
		session.setSessionId("" + id);
		session.setSessionToken(token);
		return session;
	}

	public List<SessionController> getActiveSessions()
	{
		return activeSessions.values().stream().filter(SessionController::canJoin).collect(Collectors.toList());
	}

	public void removeSession(SessionController session)
	{
		activeSessions.remove(session.getSessionId(), session);
	}

	public String createToken(Long id, String player) throws Exception
	{
		SessionController controller = activeSessions.get(id);
		return controller.generateInviteToken(player);
	}


	public SessionController getSessionById(long id)
	{
		return activeSessions.get(id);
	}

	public boolean isSessionExists(Long sessionId)
	{
		return activeSessions.containsKey(sessionId);
	}

}
