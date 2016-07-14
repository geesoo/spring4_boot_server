package com.example.spring.server;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gimbyeongsu
 * 
 */
public final class ReadThreadPool {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadThreadPool.class);

	private BootConfigFactory config;
	private final ReadThread[] readRead;
	private ExecutorService poolReadExecutor;
	private int readReadPoolSize = 0;

	public ReadThreadPool(BootConfigFactory bootConfigFactory, ReadThread[] readRead) {
		this.config = bootConfigFactory;
		this.readReadPoolSize = this.config.getReadThreadSize();
		this.readRead = readRead;
	}

	public void startPool() {
		String name = config.getReadThreadName();
		int priority = config.getReadThreadPriority();
		poolReadExecutor = Executors.newFixedThreadPool(readReadPoolSize + 1, new ThreadFactoryImpl(name, false,
				priority));
		for (int i = 0; i < readReadPoolSize; ++i) {
			poolReadExecutor.execute(readRead[i]);
		}
	}

	public void accept(int readNum, SocketChannel sc) {
		ReadThread r = readRead[readNum];
		r.setAccept(sc);
	}

	public void shutdown() {
		LOGGER.debug("");
		poolReadExecutor.shutdown();
		for (ReadThread each : readRead) {
			each.shutdown();
		}
	}
}