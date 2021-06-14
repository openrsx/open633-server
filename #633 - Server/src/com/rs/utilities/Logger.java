package com.rs.utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import com.rs.GameConstants;
import com.rs.net.ServerChannelHandler;

import lombok.SneakyThrows;

public final class Logger {

	private static BufferedWriter globallogs;
	static {
		try {
			if (!GameConstants.DEBUG) {
				Calendar c = Calendar.getInstance();
				globallogs = new BufferedWriter(new FileWriter("data/logs/global/log." + ((c.get(Calendar.MONTH)) + 1)
						+ "." + c.get(Calendar.DATE) + "." + c.get(Calendar.YEAR) + ".txt", true));
			}
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	@SneakyThrows(IOException.class)
	public static void globalLog(String ip, String name, Object o) {
		if (!GameConstants.DEBUG) {
			globallogs
					.write("[" + TextUtils.currentTime("hh:mm:ss z") + "]" + "" + name + " - " + ip + " [ " + o + " ]");
			globallogs.newLine();
			globallogs.flush();
		}
	}

	public static void handle(Throwable throwable) {
		System.out.println("ERROR! THREAD NAME: " + Thread.currentThread().getName());
		throwable.printStackTrace();
	}

	public static void debug(long processTime) {
		log(Logger.class, "---DEBUG--- start");
		log(Logger.class, "WorldProcessTime: " + processTime);
		log(Logger.class, "ConnectedChannels: " + ServerChannelHandler.getConnectedChannelsSize());
		log(Logger.class, "---DEBUG--- end");
	}

	public static void log(Object classInstance, Object message) {
		log(classInstance.getClass().getSimpleName(), message);
	}

	public static void log(String className, Object message) {
		String text = "[" + className + "]" + " " + message.toString();
		System.out.println(text);
	}

	private Logger() {

	}

}
