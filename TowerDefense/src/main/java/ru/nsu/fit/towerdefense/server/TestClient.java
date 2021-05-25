package ru.nsu.fit.towerdefense.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.Future;

public class TestClient {
	public static void main(String args[]) throws Exception{
		run();
	}


	public static void run() throws Exception{

		URL create = new URL("http://localhost:8080/createlobby");

		HttpURLConnection creation = (HttpURLConnection)create.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(creation.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while((inputLine = in.readLine()) != null)
		{
			response.append(inputLine);
		}

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode root = objectMapper.readValue(response.toString(), ObjectNode.class);

		long lobbyId = root.get("LobbyId").asLong();

		URL join = new URL("http://localhost:8080/lobby/join?LobbyId=" + lobbyId);

		HttpURLConnection joining = (HttpURLConnection)join.openConnection();
		in = new BufferedReader(new InputStreamReader(joining.getInputStream()));
		inputLine = "";
		response = new StringBuffer();
		while((inputLine = in.readLine()) != null)
		{
			response.append(inputLine);
		}

		root = objectMapper.readValue(response.toString(), ObjectNode.class);

		String token = root.get("SessionToken").asText();

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

		String auth = "{\"Token\": \"" + token + "\", \"LobbyId\" : " + lobbyId + "}";

		socket.sendMessage(auth);

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
