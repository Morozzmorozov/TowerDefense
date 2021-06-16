package ru.nsu.fit.towerdefense.server.database;

import java.security.MessageDigest;
import java.sql.*;

public class PlayersDatabase {

	private static PlayersDatabase instance = new PlayersDatabase();

	private final String address = "jdbc:postgresql://localhost:5432/towerdefense";
	private final String user = "postgres";
	private final String password = "post";
	Connection connection;

	private final Object credLock = new Object();


	private PlayersDatabase()
	{
		try
		{
			init();
			checkAndRestoreTable();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	public static PlayersDatabase getInstance()
	{
		return instance;
	}


	public void init() throws Exception
	{
		System.out.println("Testing connection to PostgreSQL JDBC");

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
			e.printStackTrace();
			throw new Exception();
		}

		System.out.println("PostgreSQL JDBC Driver successfully connected");
		try {
			connection = DriverManager.getConnection(address, user, password);

		} catch (SQLException e) {
			System.out.println("Connection Failed");
			e.printStackTrace();
			throw new Exception();
		}
	}

	public void checkAndRestoreTable()
	{
		try
		{
			Statement st = connection.createStatement();
			int result = st.executeUpdate("CREATE TABLE credentials (login VARCHAR(10) PRIMARY KEY, password VARCHAR(64) NOT NULL);");
			//st.executeUpdate("INSERT INTO credentials");
			PreparedStatement pst = connection.prepareStatement("INSERT INTO credentials VALUES (?, ?)");
			pst.setString(1, "admin1");
			pst.setString(2, "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918");
			pst.executeUpdate();
			pst.setString(1, "admin2");
			pst.executeUpdate();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

	}

	private String bytesToHex(byte[] hash) {
		StringBuilder hexString = new StringBuilder(2 * hash.length);
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if(hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	/**
	 * Check user's credentials
	 * @param user - username
	 * @param password - password
	 * @return 0 - valid password, 1 - invalid password, 2 - user doesn't exist, 3 - unexpected result
	 */
	public int validate(String user, String password)
	{
		synchronized (credLock)
		{
			try
			{
				byte[] hash = MessageDigest.getInstance("SHA-256").digest(password.getBytes());

				String sHash = bytesToHex(hash);

				System.out.println(sHash);

				PreparedStatement pst = connection.prepareStatement("SELECT password FROM credentials AS cr WHERE cr.login = ?");
				pst.setString(1, user);
				ResultSet res = pst.executeQuery();

				if (!res.next()) return 2;
				String validHash = res.getString(1);

				if (validHash.equals(sHash)) return 0;
				else return 1;

			}
			catch (Exception e)
			{

			}
			return 3;
		}
	}

	public int register(String user, String password)
	{
		synchronized (credLock)
		{
			try
			{
				byte[] hash = MessageDigest.getInstance("SHA-256").digest(password.getBytes());

				String sHash = bytesToHex(hash);

				PreparedStatement pst = connection.prepareStatement("INSERT INTO credentials VALUES(?, ?)");
				pst.setString(1, user);
				pst.setString(2, sHash);
				int t = pst.executeUpdate();

				System.out.println(t);
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
			}
			return 3;
		}
	}
}
