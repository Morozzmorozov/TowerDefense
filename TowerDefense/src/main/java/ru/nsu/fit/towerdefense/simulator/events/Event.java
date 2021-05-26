package ru.nsu.fit.towerdefense.simulator.events;

import com.google.gson.Gson;
import ru.nsu.fit.towerdefense.simulator.exceptions.GameplayException;
import ru.nsu.fit.towerdefense.simulator.world.World;

public interface Event {

  String serialize();

  static Event deserialize(String str) {
    Gson gson = new Gson();
    String type = gson.toJsonTree(str).getAsJsonObject().get("type").getAsString();
    return switch (type) {
      case "BUILD_TOWER" -> gson.fromJson(str, BuildTowerEvent.class);
      case "SELL_TOWER" -> gson.fromJson(str, SellTowerEvent.class);
      case "TUNE_TOWER" -> gson.fromJson(str, TuneTowerEvent.class);
      case "UPGRADE_TOWER" -> gson.fromJson(str, UpgradeTowerEvent.class);
      default -> throw new RuntimeException("Unknown event type: " + type);
    };
  }

  void fire(World world) throws GameplayException;

  long getFrameNumber();

  EventType getType();

  String getPlayer();
}
