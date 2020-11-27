package ru.nsu.fit.towerdefense.fx;

import javafx.scene.image.Image;
import ru.nsu.fit.towerdefense.model.GameMetaData;

import java.util.HashMap;
import java.util.Map;

public class Images {

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

    public Image getImage(String imageName) {
        Image image = imageNameToImageMap.get(imageName);

        if (image == null) {
            image = new Image(GameMetaData.getInstance().getImagePath(imageName));
            imageNameToImageMap.put(imageName, image);
        }

        return image;
    }
}
