package ru.nsu.fit.towerdefense.server;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;
import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.server.database.PlayersDatabase;
import ru.nsu.fit.towerdefense.server.filters.AuthenticationFilter;
import ru.nsu.fit.towerdefense.server.filters.ParamCleanupFilter;
import ru.nsu.fit.towerdefense.server.filters.SessionCreationFilter;
import ru.nsu.fit.towerdefense.server.players.PlayerManager;
import ru.nsu.fit.towerdefense.server.servlets.*;
import ru.nsu.fit.towerdefense.server.filters.SessionExistenceFilter;
import ru.nsu.fit.towerdefense.server.sockets.GameSocket;

import java.time.Duration;
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
		JettyWebSocketServletContainerInitializer.configure(handler, (servletContext, wsContainer) ->
		{
			// Configure default max size
			wsContainer.setMaxTextMessageSize(65535);
			wsContainer.setIdleTimeout(Duration.ofDays(10));

			// Add websockets
			wsContainer.addMapping("/game", GameSocket.class);
		});
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
		PlayersDatabase.getInstance();
		PlayerManager.getInstance();
		System.out.println("Database connected!");
		GameServer server = new GameServer(8080);
		server.addFilter(Mappings.USER_FILTER_MAPPING, ParamCleanupFilter.class);
		server.addFilter(Mappings.USER_FILTER_MAPPING, AuthenticationFilter.class);
		server.addFilter(Mappings.LOBBY_MAPPING, SessionExistenceFilter.class);
		server.addFilter(Mappings.CREATE_LOBBY_MAPPING, SessionCreationFilter.class);
		server.addServlet(Mappings.JOIN_LOBBY_MAPPING, SessionJoinServlet.class);
		server.addServlet(Mappings.LEAVE_LOBBY_MAPPING, SessionLeaveServlet.class);
		server.addServlet(Mappings.INFO_LOBBY_MAPPING, GetSessionInfoServlet.class);
		server.addServlet(Mappings.LOBBIES_MAPPING, GetSessionsServlet.class);
		server.addServlet(Mappings.CREATE_LOBBY_MAPPING, CreateSessionServlet.class);
		server.addServlet(Mappings.LOGIN_MAPPING, LoginServlet.class);
		server.addServlet(Mappings.REGISTER_MAPPING, RegisterServlet.class);
		server.addServlet(Mappings.ELO_LEADERBOARD_MAPPING, EloRatingServlet.class);

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
