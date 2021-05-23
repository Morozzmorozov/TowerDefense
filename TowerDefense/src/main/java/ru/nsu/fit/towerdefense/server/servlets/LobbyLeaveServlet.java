package ru.nsu.fit.towerdefense.server.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.server.lobby.LobbyManager;

import java.io.IOException;

public class LobbyLeaveServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try
		{
			LobbyManager.getInstance().leaveLobby(Long.parseLong(req.getParameter("lobbyId")), req.getParameter("token"));
			resp.setStatus(200);
			resp.getWriter().println("{ \"status\" : \"success\"}");
		}
		catch (Exception e){
			resp.setStatus(500);
		}
	}
}