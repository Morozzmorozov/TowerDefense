package ru.nsu.fit.towerdefense.server.servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.multiplayer.entities.Lobby;
import ru.nsu.fit.towerdefense.server.database.UserManager;
import ru.nsu.fit.towerdefense.server.lobby.LobbyManager;

import java.io.IOException;

public class LoginServlet  extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try
		{
			String login = req.getParameter("login");
			String password = req.getParameter("password");

			if (login == null || password == null)
			{
				resp.setStatus(401);
				return;
			}

			String token = UserManager.getInstance().validate(login, password);
			if (token == null)
			{
				resp.setStatus(401);
			}
			else
			{
				resp.setStatus(200);
				resp.getWriter().print(token);
			}
		}
		catch (Exception e){
			resp.setStatus(500);
		}
	}
}