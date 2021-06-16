package ru.nsu.fit.towerdefense.server.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ParamCleanupFilter  implements Filter {

	private String decode(String value) {
		try
		{
			return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
		}
		catch (Exception e) { return null; }
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest req = (HttpServletRequest) request;
		for (var x : req.getParameterMap().entrySet())
		{
			req.setAttribute("param_" + x.getKey(), decode(x.getValue()[0]));
		}
		chain.doFilter(req, response);
	}

	@Override
	public void destroy()
	{

	}

}