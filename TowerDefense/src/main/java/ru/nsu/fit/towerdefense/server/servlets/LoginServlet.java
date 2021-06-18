package ru.nsu.fit.towerdefense.server.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.server.players.PlayerManager;

import java.io.IOException;

public class LoginServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try
		{
//			System.out.println("Trying to login");
			String username = (String)req.getAttribute("param_username");
			String password = (String)req.getAttribute("param_password");

			if (username == null || password == null)
			{
				resp.setStatus(401);
				return;
			}
//			System.out.println("Going to validate");
			String token = PlayerManager.getInstance().validate(username, password);
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