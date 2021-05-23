package ru.nsu.fit.towerdefense.server.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.server.lobby.LobbyManager;

import java.io.IOException;

@WebServlet("/createlobby")
public class CreateLobbyServlet  extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		resp.setStatus(200);
		String id = LobbyManager.getInstance().createLobby();

		resp.getWriter().println("{ \"lobbyId\" : \"" + id + "\"}");

	}
}

