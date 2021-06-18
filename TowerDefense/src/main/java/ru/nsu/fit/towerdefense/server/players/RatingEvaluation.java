package ru.nsu.fit.towerdefense.server.players;

import ru.nsu.fit.towerdefense.multiplayer.entities.SPlayer;
import ru.nsu.fit.towerdefense.multiplayer.entities.SResult;

import java.util.HashMap;
import java.util.List;

public class RatingEvaluation {

	private static final int K = 20;

	public static void evaluate(List<SPlayer> players, List<SResult> results)
	{
		HashMap<String, Double> deltas = new HashMap<>();
		for (int i = 0; i < players.size(); i++)
		{
			deltas.put(players.get(i).getName(), 0.0);
		}
		for (int i = 0; i < players.size(); i++)
		{
			var x = players.get(i);
			for (int j = 0; j < players.size(); j++)
			{
				var y = players.get(j);
				if (x == y) continue;
				double Ea = 1.0/(1 + Math.pow(10, (y.getEloRating() - x.getEloRating()) / 400.0));
				//double delta = K * (results.get(i) - Ea);
			}
		}
		//for
	}


}
