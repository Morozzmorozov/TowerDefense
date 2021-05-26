package ru.nsu.fit.towerdefense.server.servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.multiplayer.entities.Session;
import ru.nsu.fit.towerdefense.server.lobby.LobbyManager;

import java.io.IOException;

public class LobbyJoinServlet  extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try
		{
			Session session = new Session();
			String token = LobbyManager.getInstance().createToken(req.getParameter("lobbyId"), req.getParameter("userToken"));
			if (token == null){
				resp.setStatus(409);
			}
			else
			{
				session.setToken(token);
				resp.setStatus(200);
				resp.getWriter().println(new Gson().toJson(session));
			}
		}
		catch (Exception e){
			resp.setStatus(500);
		}
	}
}