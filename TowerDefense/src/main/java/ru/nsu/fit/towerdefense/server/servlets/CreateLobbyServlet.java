package ru.nsu.fit.towerdefense.server.servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.multiplayer.entities.Lobby;
import ru.nsu.fit.towerdefense.server.lobby.LobbyManager;

import java.io.IOException;

@WebServlet("/createlobby")
public class CreateLobbyServlet  extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String levelName = req.getParameter("levelName");
		if (levelName == null) levelName = "Level 1_4";
		resp.setStatus(200);
		String id = LobbyManager.getInstance().createLobby(levelName);

		Lobby lobby = new Lobby();
		lobby.setId(id);

		resp.getWriter().println(new Gson().toJson(lobby));

	}
}

