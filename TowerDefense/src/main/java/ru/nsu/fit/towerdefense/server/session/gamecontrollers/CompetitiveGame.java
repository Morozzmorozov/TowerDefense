package ru.nsu.fit.towerdefense.server.session.gamecontrollers;

import com.google.gson.Gson;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.multiplayer.Message;
import ru.nsu.fit.towerdefense.multiplayer.entities.SPlayer;
import ru.nsu.fit.towerdefense.multiplayer.entities.SResult;
import ru.nsu.fit.towerdefense.server.players.RatingEvaluation;
import ru.nsu.fit.towerdefense.server.session.GameInstance;
import ru.nsu.fit.towerdefense.server.session.SessionController;
import ru.nsu.fit.towerdefense.simulator.WorldObserver;
import ru.nsu.fit.towerdefense.simulator.world.SerializableWorld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CompetitiveGame implements GameController {

	private HashMap<String, GameInstance> instances; //TODO: make session validation on server side
	private SessionController controller;

	private ConcurrentHashMap<String, SResult> latestResults;
	private List<String> players;


	List<String> winners;
	List<String> losers;

	public CompetitiveGame(GameMap map, List<String> players, SessionController controller)
	{
		this.controller = controller;
		//this.latestResults = new ConcurrentHashMap<>();
		this.players = players;
		instances = new HashMap<>();
		//players.forEach(e -> latestResults.put(e, new SResult()));
		losers = new ArrayList<>();
		winners = new ArrayList<>();
		players.forEach(e->instances.put(e, new GameInstance(map, players, new WorldObserver() {

			@Override
			public void onDefeat()
			{
				losers.add(e);
				instances.remove(e);
			}

			@Override
			public void onVictory()
			{
				winners.add(e);
				instances.remove(e);
			}
		})));
	}


	@Override
	public void sendMessage(String player, Message message)
	{
		if (message.getType() == Message.Type.EVENT)
		{
			if (instances.containsKey(player))
			{
				Event event = Event.deserialize(message.getSerializedEvent());
				instances.get(player).addEvent(event);
			}
//			latestResults.put(player, new Gson().fromJson(message.getSerializedResult(), SResult.class));
		}
		else
		{
			System.out.println("Can't handle message: " + new Gson().toJson(message));
		}
	}

	@Override
	public SerializableWorld getGameState(String owner)
	{

		return null;
	}

	@Override
	public void playerDisconnect(String player)
	{
		losers.add(player);
		instances.remove(player);
	}

	@Override
	public void run()
	{
		Gson gson = new Gson();
		int tick = 0;
		while (instances.size() > 0)
		{
			for (var x : instances.entrySet())
			{
				x.getValue().doStep();
				if (tick == 0)
				{
					Message message = new Message();
					message.setType(Message.Type.STATE);
					message.setSerializedWorld(gson.toJson(x.getValue().serialize()));
					controller.sendMessage(gson.toJson(message), x.getKey());
				}
//				List<SResult> players1 = players.stream().map(e -> latestResults.get(e)).collect(Collectors.toList());
//				Message message = new Message();
//				message.setType(Message.Type.RESULT);
//				message.setSerializedResults(players1.stream().map(gson::toJson).collect(Collectors.toList()));
//				controller.sendMessageToAll(gson.toJson(message));
				try
				{
					Thread.sleep(17);
				}
				catch (Exception e)
				{
				}
			}
			tick++;
		}
		List<SPlayer> players = new ArrayList<>();
		for (var x : winners)
		{
			for (var y : controller.getInfo().getPlayers())
			{
				if (y.getName().equals(x))
				{
					players.add(y);
					break;
				}
			}
		}
		Collections.reverse(losers);
		for (var x : losers)
		{
			for (var y : controller.getInfo().getPlayers())
			{
				if (y.getName().equals(x))
				{
					players.add(y);
					break;
				}
			}
		}
		RatingEvaluation.evaluate(players);
	}

}
