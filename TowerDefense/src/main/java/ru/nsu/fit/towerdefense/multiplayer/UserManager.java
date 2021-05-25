package ru.nsu.fit.towerdefense.multiplayer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import ru.nsu.fit.towerdefense.fx.controllers.ServerMessageListener;
import ru.nsu.fit.towerdefense.multiplayer.entities.Lobby;
import ru.nsu.fit.towerdefense.multiplayer.entities.Session;
import ru.nsu.fit.towerdefense.server.Mappings;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;

public class UserManager {

    private static final String SITE_URI = "http://127.0.0.1:8080";

    private Credentials credentials = new Credentials();

    private WebSocketClient webSocketClient;
    private MyWebSocketAdapter socketAdapter;
    private org.eclipse.jetty.websocket.api.Session session;

    public String getUsername() {
        return credentials.getUsername();
    }

    public Boolean login(String username, String password) {
        /*if (true)
            return true; // todo delete*/

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
        /*if (true)
            return new ArrayList<>() {{ // todo delete
                add(new Lobby() {{
                    setId("111");
                    setLevelName("Level 1");
                    setMaxPlayers(2);
                    setPlayers(List.of("John"));
                }});
                add(new Lobby() {{
                    setId("222");
                    setLevelName("Level 2");
                    setMaxPlayers(3);
                    setPlayers(List.of("Jane"));
                }});
            }};*/

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
        /*if (true)
            return "id_123"; // todo delete*/

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
        /*if (true)
            return "id_123"; // todo delete*/

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
                return new Gson().fromJson(response.body(), Session.class).getToken();
            }

            System.out.println("Bad status code: " + response.statusCode());
            return null;
        } catch (URISyntaxException | IOException | InterruptedException | JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void openSocketConnection(String lobbyId, String token) {
        try {
            webSocketClient = new WebSocketClient();
            webSocketClient.start();

            socketAdapter = new MyWebSocketAdapter();
            session = webSocketClient.connect(socketAdapter, URI.create("ws://localhost:8080/game")).get();
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
            session.close();
            socketAdapter = null;
            webSocketClient.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
