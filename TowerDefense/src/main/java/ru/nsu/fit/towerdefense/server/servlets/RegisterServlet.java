package ru.nsu.fit.towerdefense.server.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.server.database.PlayersDatabase;
import ru.nsu.fit.towerdefense.server.players.PlayerManager;

import java.io.IOException;

public class RegisterServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try
		{
			String login = req.getParameter("username");
			String password = req.getParameter("password");

			if (login == null || password == null)
			{
				resp.setStatus(401);
				return;
			}

			int t = PlayersDatabase.getInstance().register(login, password);
			resp.getWriter().println(t);
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			resp.setStatus(500);
		}
	}
}