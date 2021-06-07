package ru.nsu.fit.towerdefense.fx.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Snapshot {
    public static void make(File snapshotFile, Node node) {
        try {
            File parent = snapshotFile.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IOException("Couldn't create dir: " + parent);
            }

            WritableImage snapshot = node.snapshot(null, null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
            ImageIO.write(bufferedImage, "png", snapshotFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
