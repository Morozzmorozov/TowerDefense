package ru.nsu.fit.towerdefense.server.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.server.database.UserManager;
import ru.nsu.fit.towerdefense.server.lobby.LobbyManager;

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
		String login = req.getParameter("login");
		String password = req.getParameter("password");
		String userToken = req.getParameter("userToken");
		if (login != null && password != null)
		{
			chain.doFilter(req, response);
		}
		else if (userToken != null && UserManager.getInstance().tokenExists(userToken))
		{
			chain.doFilter(req, response);
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
