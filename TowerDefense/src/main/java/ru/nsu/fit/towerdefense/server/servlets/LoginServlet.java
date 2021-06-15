package ru.nsu.fit.towerdefense.server.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.server.players.PlayerManager;

import java.io.IOException;

public class LoginServlet extends HttpServlet {
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

			String token = PlayerManager.getInstance().validate(login, password);
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
			System.out.println(e.getMessage());
			resp.setStatus(500);
		}
	}
}