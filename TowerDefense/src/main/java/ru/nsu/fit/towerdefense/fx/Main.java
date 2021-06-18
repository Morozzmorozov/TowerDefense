package ru.nsu.fit.towerdefense.fx;

/**
 * Main class is used for launching TowerDefenseApplication inside a .jar file.
 *
 * In case main class extends javafx.application.Application, Java launcher requires the JavaFX
 * runtime available as modules (not as jars).
 *
 * @author Oleg Markelov
 */
public class Main {

    public static boolean LAG = false;

    /**
     * Launches TowerDefenseApplication calling its main() method.
     *
     * @param args command-line options.
     */
    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.equals("lag")) {
                LAG = true;
                break;
            }
        }
        System.out.println(LAG ? "lagging" : "no lagging");
        TowerDefenseApplication.main(args);
    }
}
