package ru.nsu.fit.towerdefense.multiplayer;

import java.util.prefs.Preferences;

public class Credentials {

    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    private final Preferences preferences;

    private String username;
    private String password;

    public Credentials() {
        preferences = Preferences.userRoot().node(getClass().getName());

        initCredentials();
    }

    private void initCredentials() {
        username = preferences.get(USERNAME_KEY, null);
        password = preferences.get(PASSWORD_KEY, null);
    }

    public void updateCredentials(String username, String password) {
        this.username = username;
        this.password = password;

        preferences.put(USERNAME_KEY, username);
        preferences.put(PASSWORD_KEY, password);
    }

    public void clearCredentials() {
        username = null;
        password = null;

        preferences.remove(USERNAME_KEY);
        preferences.remove(PASSWORD_KEY);
    }

    public boolean isEmpty() {
        return username == null;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
