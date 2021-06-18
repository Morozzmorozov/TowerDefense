package ru.nsu.fit.towerdefense.fx.controllers.game;

import javafx.application.Platform;
import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.multiplayer.ConnectionManager;
import ru.nsu.fit.towerdefense.multiplayer.GameType;
import ru.nsu.fit.towerdefense.simulator.events.Event;

import java.io.File;
import java.util.List;

public class LaggingGameController extends GameController {

    public LaggingGameController(SceneManager sceneManager, ConnectionManager connectionManager,
                                 File snapshotFile, GameMap gameMap, List<String> playerNames, GameType gameType) {
        super(sceneManager, connectionManager, snapshotFile, gameMap, playerNames, null, gameType);
    }

    @Override
    public void onServerMessageReceived(String messageStr) {
        lag(() -> super.onServerMessageReceived(messageStr));
    }

    @Override
    protected void sendEventToServer(Event event) {
        lag(() -> super.sendEventToServer(event));
    }

    private void lag(Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(runnable);
        }).start();
    }
}
