package ru.nsu.fit.towerdefense.server.lobby;

import java.util.Map;

public class CompetitionLobbyControl extends LobbyControl {

    private Map<Long, GamePerformance> userIdToGamePerformanceMap;

    public CompetitionLobbyControl(long id, String levelName) {
        super(id, levelName);
    }

    public void updateGamePerformance(Long userId, GamePerformance gamePerformance) {
    }

    public Map<Long, GamePerformance> getUserIdToGamePerformanceMap() {
        return userIdToGamePerformanceMap;
    }
}
