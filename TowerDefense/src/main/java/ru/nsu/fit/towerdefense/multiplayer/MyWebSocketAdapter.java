package ru.nsu.fit.towerdefense.multiplayer;

import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.WriteCallback;
import ru.nsu.fit.towerdefense.fx.controllers.ServerMessageListener;

public class MyWebSocketAdapter extends WebSocketAdapter {

    private ServerMessageListener serverMessageListener;

    public void setServerMessageListener(ServerMessageListener serverMessageListener) {
        this.serverMessageListener = serverMessageListener;
    }

    @Override
    public void onWebSocketText(String message) {
        System.out.println("received: " + message); // todo del
        if (serverMessageListener != null) {
            serverMessageListener.onServerMessageReceived(message);
        }
    }

    public void sendMessage(String message) {
        getRemote().sendString(message, new WriteCallback.Adaptor());
    }
}
