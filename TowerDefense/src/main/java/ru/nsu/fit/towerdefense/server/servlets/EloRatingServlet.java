package ru.nsu.fit.towerdefense.server.servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.server.database.PlayersDatabase;
import ru.nsu.fit.towerdefense.server.session.SessionController;
import ru.nsu.fit.towerdefense.server.session.SessionManager;

import java.io.IOException;

public class EloRatingServlet  extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try
		{
			int page = Integer.parseInt((String)req.getAttribute("param_page"));
			int from = 20 * page;
			int to = 20 * (page + 1);

			var t = PlayersDatabase.getInstance().getRatingLeaderboard(from, to);

			resp.setStatus(200);
			resp.getWriter().println(new Gson().toJson(t));
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			resp.setStatus(500);
		}
	}
}