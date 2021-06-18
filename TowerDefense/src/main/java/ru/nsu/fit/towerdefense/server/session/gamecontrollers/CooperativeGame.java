package ru.nsu.fit.towerdefense.server.session.gamecontrollers;

import com.google.gson.Gson;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.multiplayer.Message;
import ru.nsu.fit.towerdefense.server.session.GameInstance;
import ru.nsu.fit.towerdefense.server.session.SessionController;
import ru.nsu.fit.towerdefense.simulator.WorldObserver;
import ru.nsu.fit.towerdefense.simulator.events.Event;
import ru.nsu.fit.towerdefense.simulator.world.SerializableWorld;

import java.util.List;

public class CooperativeGame implements GameController {

	private GameInstance instance;
	private SessionController controller;
	private boolean isRunning = true;

	public CooperativeGame(GameMap map, List<String> players, SessionController controller)
	{
		instance = new GameInstance(map, players, new WorldObserver() {

			@Override
			public void onDefeat()
			{
				isRunning = false;
			}

			@Override
			public void onVictory()
			{
				isRunning = false;
			}
		});
		this.controller = controller;
	}


	@Override
	public void sendMessage(String player, Message message)
	{
		if (message.getType() == Message.Type.EVENT)
		{
			Event event = Event.deserialize(message.getSerializedEvent());
			instance.addEvent(event);
			controller.sendMessageToAll(new Gson().toJson(message));
		}
		else
		{
			System.out.println("Can't handle message: " + new Gson().toJson(message));
		}
	}

	@Override
	public SerializableWorld getGameState(String owner)
	{
		return instance.serialize();
	}


	@Override
	public void playerDisconnect(String player)
	{

	}

	@Override
	public void run()
	{
		int tick = 0;

		while (isRunning)
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
