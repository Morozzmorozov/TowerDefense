package ru.nsu.fit.towerdefense.server.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.server.players.PlayerManager;

import java.io.IOException;

public class SessionCreationFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest req = (HttpServletRequest) request;
		String player = (String)req.getAttribute("playerName");
		if (PlayerManager.getInstance().isConnected(player))
		{
			((HttpServletResponse)response).setStatus(403);
		}
		else
		{
			chain.doFilter(req, response);
		}
	}

	@Override
	public void destroy()
	{

	}

}