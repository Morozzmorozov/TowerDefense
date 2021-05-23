package ru.nsu.fit.towerdefense.server.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.server.lobby.LobbyManager;

import java.io.IOException;

public class LobbyJoinServlet  extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try
		{
			String token = LobbyManager.getInstance().createToken(req.getParameter("lobbyId"));
			if (token == null){
				resp.setStatus(409);
			}
			else
			{
				resp.setStatus(200);
				resp.getWriter().println("{ \"SessionToken\" : \"" + token + "\"}");
			}
		}
		catch (Exception e){
			resp.setStatus(500);
		}
	}
}