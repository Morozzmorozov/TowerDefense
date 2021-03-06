package ru.nsu.fit.towerdefense.server.servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.multiplayer.entities.SGameSession;
import ru.nsu.fit.towerdefense.server.session.SessionManager;

import java.io.IOException;

public class SessionJoinServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try
		{
			Long id = Long.parseLong((String)req.getAttribute("param_sessionId"));
			String player = (String)req.getAttribute("playerName");

			String token = SessionManager.getInstance().getSessionById(id).generateInviteToken(player);
			if (token == null){
				resp.setStatus(409);
			}
			else
			{
				resp.setStatus(200);
				resp.getWriter().print(token);
			}
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			resp.setStatus(500);
		}
	}
}