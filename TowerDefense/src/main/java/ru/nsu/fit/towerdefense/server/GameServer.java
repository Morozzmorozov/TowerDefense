package ru.nsu.fit.towerdefense.server;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import ru.nsu.fit.towerdefense.server.servlets.CreateLobbyServlet;
import ru.nsu.fit.towerdefense.server.servlets.GetLobbiesServlet;
import ru.nsu.fit.towerdefense.server.filters.LobbyExistenceFilter;
import ru.nsu.fit.towerdefense.server.servlets.LobbyJoinServlet;
import ru.nsu.fit.towerdefense.server.servlets.LobbyLeaveServlet;

import java.util.EnumSet;

public class GameServer {
	private int port;
	private Server server;
	private ServletContextHandler handler;
	public GameServer(int port)
	{
		this.port = port;
		server = new Server(port);
		handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
	}

	public void addFilter(String addr, Class<? extends Filter> clazz)
	{
		handler.addFilter(clazz, addr, EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));
	}


	public void addServlet(String addr, Class<? extends Servlet> clazz)
	{
		handler.addServlet(clazz, addr);
	}

	public void start() throws Exception
	{
		server.setHandler(handler);
		server.start();
		server.join();
	}


	public void shutdown() throws Exception
	{
		server.stop();
	}


	public static void main(String[] args)
	{
		GameServer server = new GameServer(8080);
		server.addFilter("/lobby/*", LobbyExistenceFilter.class);
		server.addServlet("/lobby/join", LobbyJoinServlet.class);
		server.addServlet("/lobby/leave", LobbyLeaveServlet.class);
		server.addServlet("/lobbies", GetLobbiesServlet.class);
		server.addServlet("/createlobby", CreateLobbyServlet.class);
		try
		{
			server.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
