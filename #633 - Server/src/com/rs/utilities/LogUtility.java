package com.rs.utilities;

import java.sql.SQLException;
import java.sql.Statement;

import org.tinylog.Logger;

import com.rs.game.map.World;
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
			sqlLog(message);
			break;
		}
	}
	
	@SneakyThrows(SQLException.class)
	public static void sqlLog(String query) {
		DatabaseConnection connection = World.getConnectionPool().nextFree();
		Statement statement = connection.createStatement();
		if (statement == null) {
			return;
		}
		statement.executeUpdate(query);
		connection.returnConnection();
	}
}