package com.rs.net.mysql.service.impl;

import java.sql.SQLException;
import java.sql.Statement;

import com.rs.game.map.World;
import com.rs.net.mysql.DatabaseConnection;
import com.rs.net.mysql.service.MYSQLService;

import lombok.SneakyThrows;

public class TestService implements MYSQLService {

	@Override
	@SneakyThrows(SQLException.class)
	public void execute() {
		DatabaseConnection connection = World.getConnectionPool().nextFree();
		Statement statement = connection.createStatement();
		if (statement == null) {
			return;
		}
		statement.executeUpdate("INSERT INTO public.starter(username) values ('test');");
		connection.returnConnection();
	}
}