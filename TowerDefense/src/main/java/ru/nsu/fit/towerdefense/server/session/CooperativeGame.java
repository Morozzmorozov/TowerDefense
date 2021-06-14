package ru.nsu.fit.towerdefense.server.session;

import com.google.gson.Gson;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.multiplayer.Message;
import ru.nsu.fit.towerdefense.simulator.events.Event;
import ru.nsu.fit.towerdefense.simulator.world.SerializableWorld;

import java.util.List;

public class CooperativeGame implements GameController {

	private GameInstance instance;
	private SessionController controller;

	public CooperativeGame(GameMap map, List<String> players, SessionController controller)
	{
		instance = new GameInstance(map, players);
		this.controller = controller;
	}


	@Override
	public void sendEvent(String player, Event event)
	{
		instance.addEvent(event);
	}

	@Override
	public SerializableWorld getGameState(String owner)
	{
		return instance.serialize();
	}

	@Override
	public void run()
	{
		int tick = 0;

		while (true)
		{
			tick ++;
			instance.doStep();

			try
			{
				Thread.sleep(17);
			}
			catch (Exception e){}

			if (tick == 60)
			{
				tick = 0;
				Message message = new Message();
				message.setType(Message.Type.STATE);
				message.setSerializedWorld(new Gson().toJson(instance.serialize()));
				controller.sendMessageToAll(new Gson().toJson(message));
			}
		}
	}

}
