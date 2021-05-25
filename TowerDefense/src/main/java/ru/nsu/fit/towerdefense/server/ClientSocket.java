package ru.nsu.fit.towerdefense.server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.WriteCallback;
import ru.nsu.fit.towerdefense.server.sockets.ConnectionsManager;
import ru.nsu.fit.towerdefense.server.sockets.UserConnection;

public class ClientSocket  extends WebSocketAdapter {

	@Override
	public void onWebSocketBinary(byte[] payload, int offset, int len)
	{
		/* do nothing */
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason)
	{
		/* do nothing */
	}

	@Override
	public void onWebSocketConnect(Session sess)
	{

		super.onWebSocketConnect(sess);
	}

	@Override
	public void onWebSocketError(Throwable cause)
	{
		/* do nothing */
	}

	@Override
	public void onWebSocketText(String message)
	{
		System.out.println("received: " + message);
	}

	public void sendMessage(String message)
	{

		getRemote().sendString(message, new WriteCallback.Adaptor());
	}

}