package com.rs.game.player.content;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.task.Task;
import com.rs.utilities.Utils;

import lombok.SneakyThrows;

public final class FadingScreen {
	
	public static void fade(final Player player, final Runnable event) {
		player.getMovement().lock();
		unfade(player, fade(player), event);
	}

	public static void unfade(final Player player, long startTime, final Runnable event) {
		unfade(player, 2500, startTime, event);
	}

	@SneakyThrows(Throwable.class)
	public static void unfade(final Player player, long endTime, long startTime, final Runnable event) {
		long leftTime = endTime - (Utils.currentTimeMillis() - startTime);
		if (leftTime > 0) {
			CoresManager.slowExecutor.schedule(new TimerTask() {
				@Override
				public void run() {
					unfade(player, event);
				}

			}, leftTime, TimeUnit.MILLISECONDS);
		} else
			unfade(player, event);
	}

	@SneakyThrows(Throwable.class)
	public static void unfade(final Player player, Runnable event) {
		event.run();
		World.get().submit(new Task(0) {
			@Override
			protected void execute() {
				player.getInterfaceManager().sendInterface(false, 170);
				CoresManager.slowExecutor.schedule(new TimerTask() {
					@Override
					public void run() {
						player.getInterfaceManager().closeFadingInterface();
						player.getMovement().unlock();
					}
				}, 2, TimeUnit.SECONDS);
				this.cancel();
			}
		});
	}

	public static long fade(Player player, long fadeTime) {
		player.getInterfaceManager().sendInterface(false, 115);
		return Utils.currentTimeMillis() + fadeTime;
	}

	public static long fade(Player player) {
		return fade(player, 0);
	}
}