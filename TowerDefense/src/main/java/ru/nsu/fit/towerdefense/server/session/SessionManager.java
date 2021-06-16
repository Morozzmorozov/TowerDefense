package ru.nsu.fit.towerdefense.server.session;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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

	public synchronized long createSession(SessionInfo.GameType type, String level, String owner)
	{
		SessionController controller = new SessionController(sessionId, type, level);
		controller.addOwner(owner);
		long id = sessionId;
		activeSessions.put(sessionId, controller);
		sessionId++;
		if (sessionId <= 0) sessionId = 1L;
		return id;
	}

	public List<SessionController> getActiveSessions()
	{
		return new ArrayList<>(activeSessions.values());
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
		return activeSessions.contains(sessionId);
	}

}