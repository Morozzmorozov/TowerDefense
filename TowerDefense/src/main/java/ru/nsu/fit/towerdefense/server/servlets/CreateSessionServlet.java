package ru.nsu.fit.towerdefense.server.servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.multiplayer.GameType;
import ru.nsu.fit.towerdefense.multiplayer.entities.SGameSession;
import ru.nsu.fit.towerdefense.server.session.SessionInfo;
import ru.nsu.fit.towerdefense.server.session.SessionManager;

import java.io.IOException;

public class CreateSessionServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String levelName = (String)req.getAttribute("param_levelName");
		String gameType = (String)req.getAttribute("param_gameType");
		String player = (String)req.getAttribute("playerName");
		if (levelName == null) levelName = "Level 1_4";
		if (gameType == null) gameType = "COOPERATIVE";
		resp.setStatus(201);
		SGameSession session = SessionManager.getInstance().createSession(GameType.valueOf(gameType), levelName, player);
		resp.getWriter().println(new Gson().toJson(session));
	}
}

