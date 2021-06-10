package com.rs.cores;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class CoresManager {

	protected static volatile boolean shutdown;
	public static WorldThread worldThread;
	public static ExecutorService serverWorkerChannelExecutor;
	public static ExecutorService serverBossChannelExecutor;
	public static ScheduledExecutorService slowExecutor;
	public static int serverWorkersCount;

	public static void init() {
		worldThread = new WorldThread();
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		serverWorkersCount = availableProcessors >= 6 ? availableProcessors - (availableProcessors >= 12 ? 6 : 4) : 2;
		serverWorkerChannelExecutor = serverWorkersCount > 1
				? Executors.newFixedThreadPool(serverWorkersCount, new DecoderThreadFactory())
				: Executors.newSingleThreadExecutor(new DecoderThreadFactory());
		serverBossChannelExecutor = Executors.newSingleThreadExecutor(new DecoderThreadFactory());
		slowExecutor = availableProcessors >= 6
				? Executors.newScheduledThreadPool(availableProcessors >= 12 ? 4 : 2, new SlowThreadFactory())
				: Executors.newSingleThreadScheduledExecutor(new SlowThreadFactory());
		worldThread.start();
	}

	public static void shutdown() {
		serverWorkerChannelExecutor.shutdown();
		serverBossChannelExecutor.shutdown();
		slowExecutor.shutdown();
		shutdown = true;
	}
}