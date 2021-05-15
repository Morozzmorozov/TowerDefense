package ru.nsu.fit.towerdefense.server;

import jakarta.servlet.Servlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class GameServer {
	private int port;
	private Server server;
	private ServletContextHandler handler;
	public GameServer(int port){
		this.port = port;
		server = new Server(port);
		handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
	}


	public void addServlet(String addr, Class<? extends Servlet> clazz)
	{
		handler.addServlet(clazz, addr);
	}

	public void start() throws Exception {
		server.setHandler(handler);
		server.join();
	}


	public void shutdown() throws Exception {
		server.stop();
	}
}
