package com.rs.net.mysql.service.impl;

import java.sql.SQLException;
import java.sql.Statement;

import com.rs.game.map.World;
import com.rs.net.mysql.DatabaseConnection;
import com.rs.net.mysql.service.MYSQLService;

import io.vavr.control.Try;
import lombok.SneakyThrows;

public class TestService implements MYSQLService {

	@Override
	@SneakyThrows(SQLException.class)
	public void execute() {
		Try.run(World.getConnectionPool()::nextFree).onSuccess(success -> {
			Try.of(() -> World.getConnectionPool().nextFree().createStatement().executeUpdate("INSERT INTO public.starter(username) values ('vavr');"));
		}).andFinally(() -> World.getConnectionPool().nextFree().returnConnection());
	}
}