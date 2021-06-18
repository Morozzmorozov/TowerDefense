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
//		System.out.println("Checking person!");
		if (PlayerManager.getInstance().isConnected(player))
		{
//			System.out.println("FAil!");
//			System.out.println("Player is connected!");
			((HttpServletResponse)response).setStatus(403);
		}
		else
		{
//			System.out.println("Ok!");
			chain.doFilter(req, response);
		}
	}

	@Override
	public void destroy()
	{

	}

}