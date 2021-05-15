package ru.nsu.fit.towerdefense.simulator;

import java.util.List;
import ru.nsu.fit.towerdefense.simulator.events.Event;
import ru.nsu.fit.towerdefense.simulator.world.World;

public interface ServerSimulator {

  /**
   * Schedule an Event to this simulator for future simulation or, in case the tick of this event
   * has already been calculated, simulate the past ticks as if this event had happened during them
   *
   * @param event the Event object to be put into simulation
   */
  void submitEvent(Event event);

  /**
   * Gets all events which were passed to this Simulator in the order of their submission. This may
   * include events which have been passed to the Simulator but have not fired yet
   *
   * @return list of all events passed to the Simulator
   */
  List<Event> getEvents();

  /**
   * Gets the world state of this Simulator
   *
   * @return the current state of the world processed by this Simulator
   */
  World getState();
}
