package ru.nsu.fit.towerdefense.fx;

import javafx.scene.image.Image;
import ru.nsu.fit.towerdefense.fx.exceptions.RenderException;

import java.util.HashMap;
import java.util.Map;

public class Images {

    private static final String IMAGES_DIRECTORY = "ru/nsu/fit/towerdefense/config/images/";

    private static Images instance;

    private final Map<String, Image> imageNameToImageMap;

    private Images() {
        imageNameToImageMap = new HashMap<>();
    }

    public static Images getInstance() {
        if (instance == null) {
            instance = new Images();
        }

        return instance;
    }

    public Image getImage(String imageName) throws RenderException {
        Image image = imageNameToImageMap.get(imageName);

        if (image == null) {
            try {
                image = new Image(IMAGES_DIRECTORY + imageName);
                imageNameToImageMap.put(imageName, image);
            } catch (IllegalArgumentException e) {
                throw new RenderException("Cannot open image \"" + imageName + "\".");
            }
        }

        return image;
    }
}
