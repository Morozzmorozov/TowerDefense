package ru.nsu.fit.towerdefense.replay;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReplayManager {

  private static ReplayManager instance;
  private static final String replayDir = "./Replays/";
  private static final String saveDir = "./Saves/";

  public static ReplayManager getInstance() {
    if (instance == null) {
      instance = new ReplayManager();
    }
    return instance;
  }

  private ReplayManager() {

  }

  public static List<String> getReplays(String level) {
    String dir = replayDir + level + "/";

    File parent = new File(dir);
    if (!parent.exists() && !parent.mkdirs()) {
      return new ArrayList<>();
    }
    var list = parent.list();
    if (list == null) {
      return null;
    } else {
      return Arrays.asList(list);
    }
  }

  public static Replay readReplay(String level, String name) {
    String dir = replayDir + level + "/" + name;
    File file = new File(dir);

    try (FileReader fileReader = new FileReader(file)) {
      return //new Gson().fromJson(fileReader, NewReplay.class);
      new Replay(fileReader);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void saveReplay(Replay replay, String levelName) {
    String dir = replayDir + levelName + "/";
    File parent = new File(dir);
    if (!parent.exists() && !parent.mkdirs()) {
      System.out.println("Unable to create file");
      return;
    }
    int id = parent.list().length;
    File file = new File(dir + "/Replay_" + id + ".json");

    try (var fileWriter = new FileWriter(file)) {
      fileWriter.write(replay.serialize());
      //new Gson().toJson(replay, fileWriter);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
