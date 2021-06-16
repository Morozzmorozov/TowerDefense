package ru.nsu.fit.towerdefense.server.servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.server.session.SessionController;
import ru.nsu.fit.towerdefense.server.session.SessionManager;


import java.io.IOException;

public class GetSessionInfoServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try
		{
			long id = Long.parseLong(req.getParameter("sessionId"));

			SessionController session = SessionManager.getInstance().getSessionById(id);

			if (session == null){
				resp.setStatus(409);
			}
			else
			{
				resp.setStatus(200);
				resp.getWriter().print(new Gson().toJson(session.getInfo()));
			}
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			resp.setStatus(500);
		}
	}
}