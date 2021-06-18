package ru.nsu.fit.towerdefense.replay;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.nsu.fit.towerdefense.simulator.events.Event;
import ru.nsu.fit.towerdefense.simulator.world.SerializableWorld;
import ru.nsu.fit.towerdefense.simulator.world.World;

public class Replay {

  public String serialize() {
    return new Gson().toJson(this);
  }

  public Replay(Reader json) {
    JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);

    stateRate = jsonObject.get("stateRate").getAsInt();
    length = jsonObject.get("length").getAsInt();

    for (var entry : jsonObject.get("states").getAsJsonObject().entrySet()) {
      states.put(Integer.parseInt(entry.getKey()), World.deserialize(new Gson().toJson(entry.getValue().getAsJsonObject())));
    }

    for (var entry : jsonObject.get("events").getAsJsonObject().entrySet()) {
      List<Event> list = new ArrayList<>();
      for (var event : entry.getValue().getAsJsonArray()) {

        list.add(Event.deserialize(new Gson().toJson(event.getAsJsonObject())));
      }
      events.put(Integer.parseInt(entry.getKey()), list);
    }
  }

  private int stateRate;
  private int length;
  private Map<Integer, SerializableWorld> states = new HashMap<>();

  private Map<Integer, List<Event>> events = new HashMap<>();

  public void submitEvent(Event event) {
    if (!events.containsKey((int)event.getFrameNumber())) {
      events.put((int)event.getFrameNumber(), new ArrayList<>());
    }
    events.get((int)event.getFrameNumber()).add(event);
  }

  public void submitState(SerializableWorld world) {
    states.put((int)world.getTick(), world);
  }

  public Replay(int stateRate) {
    this.stateRate = stateRate;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public int getStateRate() {
    return stateRate;
  }

  public Map<Integer, SerializableWorld> getStates() {
    return states;
  }

  public Map<Integer, List<Event>> getEvents() {
    return events;
  }


}
