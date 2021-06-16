package ru.nsu.fit.towerdefense.multiplayer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import ru.nsu.fit.towerdefense.fx.controllers.ServerMessageListener;
import ru.nsu.fit.towerdefense.multiplayer.entities.SEloRating;
import ru.nsu.fit.towerdefense.multiplayer.entities.SGameSession;
import ru.nsu.fit.towerdefense.multiplayer.entities.SLevelScore;
import ru.nsu.fit.towerdefense.multiplayer.entities.SLobby;
import ru.nsu.fit.towerdefense.server.Mappings;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
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
                .POST(HttpRequest.BodyPublishers.ofString(""))
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

    public List<SLobby> getLobbies() {
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
                SLobby[] lobbies = new Gson().fromJson(response.body(), SLobby[].class);
                return Arrays.asList(lobbies);
            }

            System.out.println("Bad status code: " + response.statusCode());
            return null;
        } catch (URISyntaxException | IOException | InterruptedException | JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public SGameSession createLobby(String gameMapName) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SITE_URI + Mappings.CREATE_LOBBY_MAPPING +
                    "?userToken=" + credentials.getUserToken() +
                    "&levelName=" + encode(gameMapName) +
                    "&gameType=" + GameType.COOPERATIVE)) // todo
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .timeout(Duration.of(15, SECONDS))
                .build();

            HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return new Gson().fromJson(response.body(), SGameSession.class);
            }

            System.out.println("Bad status code: " + response.statusCode());
            return null;
        } catch (URISyntaxException | IOException | InterruptedException | JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String joinLobby(String sessionId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SITE_URI + Mappings.JOIN_LOBBY_MAPPING +
                    "?userToken=" + credentials.getUserToken() +
                    "&sessionId=" + sessionId))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .timeout(Duration.of(15, SECONDS))
                .build();

            HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return new Gson().fromJson(response.body(), SGameSession.class).getSessionToken();
            }

            System.out.println("Bad status code: " + response.statusCode());
            return null;
        } catch (URISyntaxException | IOException | InterruptedException | JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public SLobby getLobby(String lobbyId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SITE_URI + Mappings.INFO_LOBBY_MAPPING +
                    "?userToken=" + credentials.getUserToken() +
                    "&sessionId=" + lobbyId))
                .timeout(Duration.of(15, SECONDS))
                .build();

            HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return new Gson().fromJson(response.body(), SLobby.class);
            }

            System.out.println("Bad status code: " + response.statusCode());
            return null;
        } catch (URISyntaxException | IOException | InterruptedException | JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<SLevelScore> getLeaderboard(String gameMapName) {
        return null; // todo
    }

    public List<SEloRating> getEloLeaderboard() {
        return null; // todo
    }

    public void openSocketConnection(SGameSession gameSession) {
        try {
            webSocketClient = new WebSocketClient();
            webSocketClient.setIdleTimeout(Duration.ofDays(10));
            webSocketClient.start();

            socketAdapter = new MyWebSocketAdapter();
            session = webSocketClient.connect(socketAdapter, URI.create("ws://localhost:8080/game")).get();

            socketAdapter.sendMessage(new Gson().toJson(gameSession));
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

    private String encode(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, StandardCharsets.UTF_8.toString());
    }
}
