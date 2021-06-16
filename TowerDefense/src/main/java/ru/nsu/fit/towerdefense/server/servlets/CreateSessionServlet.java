package ru.nsu.fit.towerdefense.server.servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.multiplayer.entities.SLobby;
import ru.nsu.fit.towerdefense.server.session.SessionInfo;
import ru.nsu.fit.towerdefense.server.session.SessionManager;

import java.io.IOException;

public class CreateSessionServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String levelName = req.getParameter("levelName");
		String gameType = req.getParameter("gameType");
		String player = (String)req.getAttribute("playerName");
		if (levelName == null) levelName = "Level 1_4";
		if (gameType == null) gameType = "COOPERATIVE";
		resp.setStatus(201);
		String id = "" + SessionManager.getInstance().createSession(SessionInfo.GameType.valueOf(gameType), levelName, player);

//		String id = LobbyManager.getInstance().createLobby(levelName);

		SLobby lobby = new SLobby();
		lobby.setId(id);

		resp.getWriter().println(new Gson().toJson(lobby));

	}
}

