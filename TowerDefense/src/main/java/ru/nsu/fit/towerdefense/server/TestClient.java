package ru.nsu.fit.towerdefense.server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import ru.nsu.fit.towerdefense.server.sockets.GameSocket;

import java.net.URI;
import java.util.concurrent.Future;

public class TestClient {
	public static void main(String args[]) throws Exception{
		run();
	}


	public static void run() throws Exception{
		URI uri = URI.create("ws://localhost:8080/game");

		WebSocketClient client = new WebSocketClient();
		client.start();
		// The socket that receives events
		ClientSocket socket = new ClientSocket();
		// Attempt Connect
		Future<Session> fut = client.connect(socket, uri);
		// Wait for Connect
		Session session = fut.get();
		//Thread.sleep(1000);

		String token = "{\"Token\": \"/vODtK7VehQZyXjT9JqheciYvfUGw4vjTooT5+wuAFM=\", \"LobbyId\" : 0}";

		socket.sendMessage(token);

		try {
			Thread.sleep(500);
		}
		catch (Exception e){}

		socket.sendMessage("{\"ChangeStatus\" : \"Ready\"}");

		try {
			Thread.sleep(100000);
		}
		catch (Exception e){}

		// Close session
		session.close();

		client.stop();
	}
}
