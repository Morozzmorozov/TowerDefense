package ru.nsu.fit.towerdefense.multiplayer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import ru.nsu.fit.towerdefense.fx.controllers.ServerMessageListener;
import ru.nsu.fit.towerdefense.multiplayer.entities.EloRating;
import ru.nsu.fit.towerdefense.multiplayer.entities.GameSession;
import ru.nsu.fit.towerdefense.multiplayer.entities.LevelScore;
import ru.nsu.fit.towerdefense.multiplayer.entities.Lobby;
import ru.nsu.fit.towerdefense.server.Mappings;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;

public class ConnectionManager {

    private static final String SITE_URI = "http://127.0.0.1:8080";

    private Credentials credentials = new Credentials();

    protected Credentials getCredentials() { // todo del
        return credentials;
    }

    private WebSocketClient webSocketClient;
    private MyWebSocketAdapter socketAdapter;
    private Session session;

    public String getUsername() {
        return credentials.getUsername();
    }

    public Boolean login(String username, String password) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SITE_URI + Mappings.LOGIN_MAPPING +
                    "?username=" + username +
                    "&password=" + password))
                .timeout(Duration.of(15, SECONDS))
                .build();

            HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                credentials.setUsername(username);
                credentials.setUserToken(response.body());
                System.out.println(credentials.getUserToken());
                return true;
            }

            System.out.println("Bad status code: " + response.statusCode());
            return false;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void logout() {
        credentials = new Credentials();
    }

    public List<Lobby> getLobbies() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SITE_URI + Mappings.LOBBIES_MAPPING +
                    "?userToken=" + credentials.getUserToken()))
                .timeout(Duration.of(15, SECONDS))
                .build();

            HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                Lobby[] lobbies = new Gson().fromJson(response.body(), Lobby[].class);
                return Arrays.asList(lobbies);
            }

            System.out.println("Bad status code: " + response.statusCode());
            return null;
        } catch (URISyntaxException | IOException | InterruptedException | JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String createLobby(String gameMapName) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SITE_URI + Mappings.CREATE_LOBBY_MAPPING +
                    "?userToken=" + credentials.getUserToken()/* +
                    "&levelName=" + gameMapName*/))
                .timeout(Duration.of(15, SECONDS))
                .build();

            HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return new Gson().fromJson(response.body(), Lobby.class).getId();
            }

            System.out.println("Bad status code: " + response.statusCode());
            return null;
        } catch (URISyntaxException | IOException | InterruptedException | JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String joinLobby(String lobbyId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SITE_URI + Mappings.JOIN_LOBBY_MAPPING +
                    "?userToken=" + credentials.getUserToken() +
                    "&lobbyId=" + lobbyId))
                .timeout(Duration.of(15, SECONDS))
                .build();

            HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return new Gson().fromJson(response.body(), GameSession.class).getToken();
            }

            System.out.println("Bad status code: " + response.statusCode());
            return null;
        } catch (URISyntaxException | IOException | InterruptedException | JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Lobby getLobby(String sessionToken) {
        return null; // todo
    }

    public List<LevelScore> getLeaderboard(String gameMapName) {
        return null; // todo
    }

    public List<EloRating> getEloLeaderboard() {
        return null; // todo
    }

    public void openSocketConnection(String lobbyId, String token) {
        try {
            webSocketClient = new WebSocketClient();
            webSocketClient.setIdleTimeout(Duration.ofDays(10));
            webSocketClient.start();

            socketAdapter = new MyWebSocketAdapter();
            session = webSocketClient.connect(socketAdapter, URI.create("ws://localhost:8080/game")).get();

            socketAdapter.sendMessage("{\"Token\": \"" + token + "\", \"lobbyId\" : " + lobbyId + "}");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        if (socketAdapter != null) {
            socketAdapter.sendMessage(message);
        }
    }

    public void setServerMessageListener(ServerMessageListener serverMessageListener) {
        if (socketAdapter != null) {
            socketAdapter.setServerMessageListener(serverMessageListener);
        }
    }

    public void closeSocketConnection() {
        try {
            if (session != null) {
                session.close();
            }

            if (webSocketClient != null) {
                webSocketClient.stop();
            }

            socketAdapter = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        closeSocketConnection();
        // todo interrupt()
    }
}
