package ru.nsu.fit.towerdefense.server.players;

import ru.nsu.fit.towerdefense.multiplayer.entities.SPlayer;
import ru.nsu.fit.towerdefense.multiplayer.entities.SResult;
import ru.nsu.fit.towerdefense.server.database.PlayersDatabase;

import java.util.HashMap;
import java.util.List;

public class RatingEvaluation {

	private static final int K = 20;

	public static void evaluate(List<SPlayer> players)
	{
		System.out.println("evaluate");
		if (players.size() % 2 == 1)
		{
			int p = players.size() / 2 - 1, q = p + 2;
			int cnt = 5;
			while (p > 0)
			{
				PlayersDatabase.getInstance().updateRating(players.get(p).getName(), players.get(p).getEloRating() - cnt);
				PlayersDatabase.getInstance().updateRating(players.get(q).getName(), players.get(q).getEloRating() + cnt);
				cnt += 5;
				p--;
			}
		}
		else
		{
			int p = players.size() / 2 - 1, q = p + 1;
			int cnt = 5;
			while (p > 0)
			{
				PlayersDatabase.getInstance().updateRating(players.get(p).getName(), players.get(p).getEloRating() - cnt);
				PlayersDatabase.getInstance().updateRating(players.get(q).getName(), players.get(q).getEloRating() + cnt);
				cnt += 5;
				p--;
			}
		}
	}
}
