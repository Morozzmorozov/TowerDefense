package ru.nsu.fit.towerdefense.multiplayer;

import ru.nsu.fit.towerdefense.fx.controllers.ServerMessageListener;
import ru.nsu.fit.towerdefense.multiplayer.entities.Lobby;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ConnectionManagerStub extends ConnectionManager {

    private final List<Lobby> lobbies = List.of(
        new Lobby() {{
            setId("111");
            setLevelName("Level 1");
            setMaxPlayers(2);
            setPlayers(List.of("John", "Jane"));
        }},
        new Lobby() {{
            setId("222");
            setLevelName("Level 2");
            setMaxPlayers(3);
            setPlayers(List.of("admin"));
        }},
        new Lobby() {{
            setId("333");
            setLevelName("Level 3");
            setMaxPlayers(3);
            setPlayers(List.of("Super Player", "Bad Player"));
        }}
    );

    private final List<String> players = List.of("John", "Jane", "Super Player");

    @Override
    public Boolean login(String username, String password) {
        super.getCredentials().setUsername(username);
        super.getCredentials().setUserToken("TOKEN");
        return true;
    }

    @Override
    public List<Lobby> getLobbies() {
        return getRandomSubList(lobbies);
    }

    @Override
    public String createLobby(String gameMapName) {
        return "id_123";
    }

    @Override
    public String joinLobby(String lobbyId) {
        return "token_123";
    }

    @Override
    public Lobby getLobby(String token) {
        return new Lobby() {{
            setId("444");
            setLevelName("Level 1_4");
            setMaxPlayers(3);
            setPlayers(getRandomSubList(players));
        }};
    }

    @Override
    public void openSocketConnection(String lobbyId, String token) {}

    @Override
    public void sendMessage(String message) {}

    @Override
    public void setServerMessageListener(ServerMessageListener serverMessageListener) {}

    @Override
    public void closeSocketConnection() {}

    @Override
    public void dispose() {}

    private static <T> List<T> getRandomSubList(List<T> list) {
        list = new ArrayList<>(list);
        Collections.shuffle(list);
        return list.subList(0, ThreadLocalRandom.current().nextInt(0, list.size() + 1));
    }
}
