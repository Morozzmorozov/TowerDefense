package ru.nsu.fit.towerdefense.server.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.nsu.fit.towerdefense.server.lobby.LobbyManager;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/lobbies")
public class GetLobbiesServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		resp.setStatus(200);

		var lobbies = LobbyManager.getInstance().getLobbies();
		PrintWriter writer = resp.getWriter();
		writer.print("{ \"lobbies\" : [");
		for (int id = lobbies.size() - 1; id >= 0; id--){
			writer.print("{\"lobbyId\" : " + lobbies.get(id).getId() + ", \"PlayersInLobby\" : " + lobbies.get(id).getJoined()
					             + ", \"MaxPlayers\" : " + lobbies.get(id).getPlayersNumber() + ", \"Level name\" : \"" + lobbies.get(id).getLevelName() + "\"}\n");
			if (id > 0){
				writer.print(",");
			}
		}
		writer.print("]}");

	}
}
