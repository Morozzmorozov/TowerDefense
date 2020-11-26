package ru.nsu.fit.towerdefense.fx.controllers;

/**
 * Controller interface is used by JavaFX in javafx.fxml.FXMLLoader for controlling a view and a
 * model.
 *
 * @author Oleg Markelov
 */
public interface Controller {
    /**
     * Returns .fxml file name.
     *
     * @return .fxml file name.
     */
    String getFXMLFileName();

    /**
     * This method is designed to run after the parent node is loaded by javafx.fxml.FXMLLoader.
     */
    default void runAfterSceneSet() {}

    /**
     * Relinquishes any underlying resources of this controller.
     */
    default void dispose() {}
}
