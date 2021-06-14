package ru.nsu.fit.towerdefense.server.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.server.lobbyOld.LobbyManager;
import ru.nsu.fit.towerdefense.server.session.SessionManager;

import java.io.IOException;

public class SessionExistenceFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest req = (HttpServletRequest)request;
		String id = req.getParameter("sessionId");
		Long lId = Long.parseLong(id);
 		if (!SessionManager.getInstance().isSessionExists(lId)){
			((HttpServletResponse)response).setStatus(400);
		}
		else
		{
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy()
	{

	}

}
