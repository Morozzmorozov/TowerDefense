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
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try
		{
			String username = (String)req.getAttribute("param_username");
			String password = (String)req.getAttribute("param_password");

			if (username == null || password == null)
			{
				resp.setStatus(401);
				return;
			}

			int t = PlayersDatabase.getInstance().register(username, password);
			if (t == 1) resp.setStatus(400);
			else resp.setStatus(201);
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			resp.setStatus(500);
		}
	}
}