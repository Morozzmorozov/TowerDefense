package ru.nsu.fit.towerdefense.server.servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.multiplayer.entities.Session;
import ru.nsu.fit.towerdefense.server.session.SessionManager;

import java.io.IOException;

public class SessionJoinServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try
		{
			Long id = Long.parseLong(req.getParameter("sessionId"));
			String player = (String)req.getAttribute("playerName");

			String token = SessionManager.getInstance().getSessionById(id).generateInviteToken(player);



			Session session = new Session();
//			String token = LobbyManager.getInstance().createToken(req.getParameter("lobbyId"), req.getParameter("userToken"));
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