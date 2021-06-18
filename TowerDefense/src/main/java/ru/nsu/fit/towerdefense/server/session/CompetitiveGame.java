package ru.nsu.fit.towerdefense.server.session;

import com.google.gson.Gson;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.multiplayer.Message;
import ru.nsu.fit.towerdefense.multiplayer.entities.SPlayer;
import ru.nsu.fit.towerdefense.multiplayer.entities.SResult;
import ru.nsu.fit.towerdefense.server.players.PlayerInfo;
import ru.nsu.fit.towerdefense.simulator.events.Event;
import ru.nsu.fit.towerdefense.simulator.world.SerializableWorld;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CompetitiveGame implements GameController {

	private List<GameInstance> instances; //TODO: make session validation on server side
	private SessionController controller;

	private ConcurrentHashMap<String, SResult> latestResults;
	private List<String> players;


	public CompetitiveGame(GameMap map, List<String> players, SessionController controller)
	{
		this.controller = controller;
		this.latestResults = new ConcurrentHashMap<>();
		this.players = players;
		players.stream().map(e -> latestResults.put(e, new SResult()));
	}


	@Override
	public void sendMessage(String player, Message message)
	{
		if (message.getType() == Message.Type.RESULT)
		{
			latestResults.put(player, new Gson().fromJson(message.getSerializedResult(), SResult.class));
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
	public void run()
	{
		Gson gson = new Gson();
		while (true)
		{
			List<SResult> players1 = players.stream().map(e -> latestResults.get(e)).collect(Collectors.toList());
			Message message = new Message();
			message.setType(Message.Type.RESULT);
			message.setSerializedResults(players1.stream().map(gson::toJson).collect(Collectors.toList()));
			controller.sendMessageToAll(gson.toJson(message));
			try
			{
				Thread.sleep(1000);
			}
			catch (Exception e)
			{

			}
		}
	}

}
