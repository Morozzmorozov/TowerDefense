package ru.nsu.fit.towerdefense.multiplayer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import ru.nsu.fit.towerdefense.multiplayer.entities.Lobby;
import ru.nsu.fit.towerdefense.multiplayer.entities.Session;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

public class UserManager {

    private static final String SITE_URI = "http://127.0.0.1:8080";

    private static final String LOGIN_MAPPING = "/login";
    private static final String CREATE_LOBBY_MAPPING = "/createlobby";
    private static final String JOIN_LOBBY_MAPPING = "/lobby/join";

    private Credentials credentials = new Credentials();

    public String getUsername() {
        return credentials.getUsername();
    }

    public Boolean login(String username, String password) {
        if (true)
            return true; // todo delete

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SITE_URI + LOGIN_MAPPING + "?username=" + username + "&password=" + password))
                .timeout(Duration.of(15, SECONDS))
                .build();

            HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                credentials.setUsername(username);
                credentials.setPassword(password);
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

    public String createLobby(String gameMapName) {
        if (true)
            return "id_123"; // todo delete

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SITE_URI + CREATE_LOBBY_MAPPING +
                    "?username=" + credentials.getUsername() +
                    "&password=" + credentials.getPassword() +
                    "&map=" + gameMapName))
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
        if (true)
            return "id_123"; // todo delete

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SITE_URI + JOIN_LOBBY_MAPPING +
                    "?username=" + credentials.getUsername() +
                    "&password=" + credentials.getPassword() +
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
}
