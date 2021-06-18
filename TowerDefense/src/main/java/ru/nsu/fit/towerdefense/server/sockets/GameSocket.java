package ru.nsu.fit.towerdefense.server.sockets;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.WriteCallback;


public class GameSocket extends WebSocketAdapter {
	private UserConnection owner;

	@Override
	public void onWebSocketBinary(byte[] payload, int offset, int len) {
		/* do nothing */
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		/* do nothing */
		owner.disconnect();
	}

	@Override
	public void onWebSocketConnect(Session sess) {
		super.onWebSocketConnect(sess);
		ConnectionsManager.getInstance().registerConnection(this);
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		/* do nothing */
	}

	@Override
	public void onWebSocketText(String message)
	{
		owner.receiveMessage(message);
	}

	public void sendMessage(String message) {
		getRemote().sendString(message, new WriteCallback.Adaptor());
	}

	public UserConnection getOwner()
	{
		return owner;
	}

	public void setOwner(UserConnection owner)
	{
		this.owner = owner;
	}

	public void close()
	{
		getSession().close();
	}
}
