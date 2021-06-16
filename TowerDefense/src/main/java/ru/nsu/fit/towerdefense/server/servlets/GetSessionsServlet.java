package ru.nsu.fit.towerdefense.server.servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.multiplayer.entities.Lobby;
import ru.nsu.fit.towerdefense.server.session.SessionController;
import ru.nsu.fit.towerdefense.server.session.SessionManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public class GetSessionsServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		resp.setStatus(200);

		var sessions = SessionManager.getInstance().getActiveSessions();/*LobbyManager.getInstance().getLobbies();*/
		PrintWriter writer = resp.getWriter();

		List<Lobby> list = sessions.stream().map(SessionController::getInfo).collect(Collectors.toList());

		writer.print(new Gson().toJson(list));

	}
}
