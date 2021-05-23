package ru.nsu.fit.towerdefense.simulator.events;

import ru.nsu.fit.towerdefense.simulator.exceptions.GameplayException;
import ru.nsu.fit.towerdefense.simulator.world.World;

public interface Event {

  void fire(World world) throws GameplayException;

  long getFrameNumber();

  EventType getType();

  String getPlayer();
}
