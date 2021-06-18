package ru.nsu.fit.towerdefense.fx;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.nsu.fit.towerdefense.multiplayer.ConnectionManager;

/**
 * TowerDefenseApplication is a game made with JavaFX.
 *
 * @author Oleg Markelov
 */
public class TowerDefenseApplication extends Application {

    public static String USER = "1";
    public static boolean LAG = false;

    private SceneManager sceneManager;
    private ConnectionManager connectionManager;

    /**
     * Launches JavaFX application.
     *
     * @param args command-line options.
     */
    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            try {
                if (args[i].equals("-l") || args[i].equals("--lag")) {
                    LAG = true;
                } else if (args[i].equals("-u") || args[i].equals("-user")) {
                    USER = args[i+1];
                }
            } catch (IndexOutOfBoundsException e) {
                System.err.println("Bad arguments syntax");
            }
        }
        System.out.println("User: " + USER);
        System.out.println("lagging is " + (LAG ? "ON" : "OFF"));

        launch(args);
    }

    /**
     * Creates a new SceneManager and shows a menu window.
     *
     * Called when JavaFX starts the application.
     *
     * @param primaryStage stage created by JavaFX.
     */
    @Override
    public void start(Stage primaryStage) {
        connectionManager = new ConnectionManager();
//        connectionManager = new ConnectionManagerStub(); // todo del
        sceneManager = new SceneManager(primaryStage, connectionManager);
        sceneManager.switchToMenu();
        primaryStage.show();
    }

    /**
     * Disposes scene manager.
     *
     * Called when JavaFX stops the application.
     */
    @Override
    public void stop() {
        if (connectionManager != null) {
            connectionManager.dispose();
        }

        if (sceneManager != null) {
            sceneManager.dispose();
        }
    }
}
