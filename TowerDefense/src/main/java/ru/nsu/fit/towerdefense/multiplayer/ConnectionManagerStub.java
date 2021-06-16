package ru.nsu.fit.towerdefense.multiplayer;

import ru.nsu.fit.towerdefense.fx.controllers.ServerMessageListener;
import ru.nsu.fit.towerdefense.multiplayer.entities.SEloRating;
import ru.nsu.fit.towerdefense.multiplayer.entities.SLevelScore;
import ru.nsu.fit.towerdefense.multiplayer.entities.SLobby;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ConnectionManagerStub extends ConnectionManager {

    private final List<SLobby> lobbies = List.of(
        new SLobby() {{
            setId("111");
            setLevelName("Level 1");
            setMaxPlayers(2);
            setPlayers(List.of("John", "Jane"));
            setType(Type.Cooperative);
        }},
        new SLobby() {{
            setId("222");
            setLevelName("Level 2");
            setMaxPlayers(3);
            setPlayers(List.of("admin"));
            setType(Type.Competition);
        }},
        new SLobby() {{
            setId("333");
            setLevelName("Level 3");
            setMaxPlayers(3);
            setPlayers(List.of("Super Player", "Bad Player"));
            setType(Type.Cooperative);
        }}
    );

    private final List<String> players = List.of("John", "Jane", "Super Player");

    private final List<SLevelScore> levelScores = List.of(
        new SLevelScore() {{
            setPlayerName("Jonh");
            setScore(654321);
            setTimestamp(1234567890L * 3);
        }},
        new SLevelScore() {{
            setPlayerName("Jane");
            setScore(123456);
            setTimestamp(1234567890L * 2);
        }},
        new SLevelScore() {{
            setPlayerName("admin");
            setScore(222);
            setTimestamp(1234567890L);
        }}
    );

    private final List<SEloRating> eloRatings = List.of(
        new SEloRating() {{
            setPlayerName("John");
            setRating(1899);
        }},
        new SEloRating() {{
            setPlayerName("Jane");
            setRating(1378);
        }},
        new SEloRating() {{
            setPlayerName("admin");
            setRating(1002);
        }}
    );

    @Override
    public Boolean login(String username, String password) {
        super.getCredentials().setUsername(username);
        super.getCredentials().setUserToken("TOKEN");
        return true;
    }

    @Override
    public List<SLobby> getLobbies() {
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
    public SLobby getLobby(String sessionToken) {
        return new SLobby() {{
            setId("444");
            setLevelName("Level 1_4");
            setMaxPlayers(3);
            setPlayers(getRandomSubList(players));
            setType(Type.Cooperative);
        }};
    }

    public List<SLevelScore> getLeaderboard(String gameMapName) {
        return levelScores;
    }

    public List<SEloRating> getEloLeaderboard() {
        return eloRatings;
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
