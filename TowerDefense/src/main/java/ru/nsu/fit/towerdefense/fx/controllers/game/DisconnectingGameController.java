package ru.nsu.fit.towerdefense.fx.controllers.game;

import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.multiplayer.ConnectionManager;
import ru.nsu.fit.towerdefense.multiplayer.GameType;
import ru.nsu.fit.towerdefense.simulator.events.Event;

import java.io.File;
import java.util.List;

public class DisconnectingGameController extends GameController {

    private boolean disconnected = false;

    public DisconnectingGameController(SceneManager sceneManager, ConnectionManager connectionManager,
                                       File snapshotFile, GameMap gameMap, List<String> playerNames, GameType gameType) {
        super(sceneManager, connectionManager, snapshotFile, gameMap, playerNames, null, gameType);

        new Thread(() -> {
            try {
                Thread.sleep(10000);
                disconnected = true;
                Thread.sleep(10000);
                disconnected = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onServerMessageReceived(String messageStr) {
        if (disconnected) {
            return;
        }

        super.onServerMessageReceived(messageStr);
    }

    @Override
    protected void sendEventToServer(Event event) {
        if (disconnected) {
            return;
        }

        super.sendEventToServer(event);
    }
}
