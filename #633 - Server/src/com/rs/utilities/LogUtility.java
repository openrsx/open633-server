package com.rs.utilities;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.tinylog.Logger;

import com.rs.GameConstants;
import com.rs.game.map.World;
import com.rs.game.player.Player;
import com.rs.net.mysql.DatabaseConnection;

import lombok.SneakyThrows;

public final class LogUtility {

	public enum LogType {
		INFO, TRACE, DEBUG, WARN, ERROR, SQL
	}
	
	public static void log(LogType logType, String message) {
		switch(logType) {
		case DEBUG:
			Logger.debug(message);
			break;
		case ERROR:
			Logger.error(message);
			break;
		case INFO:
			Logger.info(message);
			break;
		case TRACE:
			Logger.trace(message);
			break;
		case WARN:
			Logger.warn(message);
			break;
		case SQL:
			submitFullQuery(message);
			break;
		}
	}
	
	@SneakyThrows(SQLException.class)
	public static void submitFullQuery(String query) {
		if (!checkSQLState())
			return;
		DatabaseConnection connection = World.getConnectionPool().nextFree();
		Statement statement = connection.createStatement();
		if (statement == null) {
			return;
		}
		statement.executeUpdate(query);
		connection.returnConnection();
	}
	
	@SneakyThrows(SQLException.class)
	public static void submitSQLLog(Player player, String query) {
		if (!checkSQLState())
			return;
		DatabaseConnection connection = World.getConnectionPool().nextFree();
		Statement statement = connection.createStatement();
		if (statement == null) {
			return;
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss aa");
		Date date = new Date();
		statement.executeUpdate("INSERT INTO log(username, logtext, date) VALUES ('"+ player.getDisplayName()+"','" + query + "','" + dateFormat.format(date) + "');");
		connection.returnConnection();
	}
	
	private static boolean checkSQLState() {
		if (!GameConstants.SQL_ENABLED)
			System.out.println("Unable to process request, MYSQL services are not present.");
		return GameConstants.SQL_ENABLED;
	}
}