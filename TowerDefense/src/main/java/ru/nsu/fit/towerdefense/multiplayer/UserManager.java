package ru.nsu.fit.towerdefense.multiplayer;

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
    private static final String LOGOUT_MAPPING = "/logout";
    private static final String PING_MAPPING = "/ping";
    private static final String RAW_MOVIE = "/raw-movie";
    private static final String RAW_MOVIES = "/raw-movies";
    private static final String RAW_PHRASE = "/raw-phrase";

    private final Credentials credentials = new Credentials();

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
                credentials.updateCredentials(username, password);
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
        credentials.clearCredentials();
    }
}
