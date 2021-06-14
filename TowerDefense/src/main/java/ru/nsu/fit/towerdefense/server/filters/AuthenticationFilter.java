package ru.nsu.fit.towerdefense.server.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.server.database.UserManager;
import ru.nsu.fit.towerdefense.server.players.PlayerManager;

import java.io.IOException;

public class AuthenticationFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{

		HttpServletRequest req = (HttpServletRequest) request;
		String login = req.getParameter("username");
		String password = req.getParameter("password");
		String userToken = req.getParameter("userToken");
		if (login != null && password != null)
		{
			chain.doFilter(req, response);
		}
		else if (userToken != null)
		{
			String player = PlayerManager.getInstance().getPlayerByToken(userToken);
			if (player != null)
			{
				req.setAttribute("playerName", player);
				chain.doFilter(req, response);
			}
			else
			{
				((HttpServletResponse) response).setStatus(401);
			}
		}
		else
		{
			((HttpServletResponse) response).setStatus(401);
		}
	}

	@Override
	public void destroy()
	{

	}

}
