package ru.nsu.fit.towerdefense.server;

public class Mappings {
    public static final String LOGIN_MAPPING = "/login";
    public static final String REGISTER_MAPPING = "/register";
    public static final String LOBBY_MAPPING = "/session/*";
    public static final String JOIN_LOBBY_MAPPING = "/session/join";
    public static final String LEAVE_LOBBY_MAPPING = "/session/leave";
    public static final String LOBBIES_MAPPING = "/sessions";
    public static final String CREATE_LOBBY_MAPPING = "/createSession";
    public static final String INFO_LOBBY_MAPPING = "/session/info";
    public static final String USER_FILTER_MAPPING = "/*";
    public static final String LEVEL_LEADERBOARD_MAPPING = "/leaderboard";
    public static final String ELO_LEADERBOARD_MAPPING = "/eloLeaderboard";
}
