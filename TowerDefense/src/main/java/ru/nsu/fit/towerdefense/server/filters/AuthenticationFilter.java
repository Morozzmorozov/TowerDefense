package ru.nsu.fit.towerdefense.server.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
//		System.out.println("AuthFilter");
		HttpServletRequest req = (HttpServletRequest) request;
		String login = (String)req.getAttribute("param_username");
		String password = (String)req.getAttribute("param_password");
		String userToken = (String)req.getAttribute("param_userToken");
		if (login != null && password != null)
		{
			chain.doFilter(req, response);
//			System.out.println("User filtered!");
		}
		else if (userToken != null)
		{
			String player = PlayerManager.getInstance().getPlayerByToken(userToken);
			if (player != null)
			{
				req.setAttribute("playerName", player);
				System.out.println("User filtered!");
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
