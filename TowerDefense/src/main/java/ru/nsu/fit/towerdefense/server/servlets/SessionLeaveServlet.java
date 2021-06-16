package ru.nsu.fit.towerdefense.server.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.server.session.SessionManager;

import java.io.IOException;

public class SessionLeaveServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try
		{
			Long id = Long.parseLong(req.getParameter("sessionId"));
			String player = (String)req.getAttribute("playerName");

			SessionManager.getInstance().getSessionById(id).disconnectPlayer(player);

//			LobbyManager.getInstance().leaveLobby(Long.parseLong(req.getParameter("lobbyId")), req.getParameter("token"), req.getParameter("userToken"));
			resp.setStatus(200);
			resp.getWriter().println("{ \"status\" : \"success\"}");
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			resp.setStatus(500);
		}
	}
}