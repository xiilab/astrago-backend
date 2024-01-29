package com.xiilab.servercore.common.utils;

public class ThreadHelper {
	private ThreadHelper() {
		throw new IllegalStateException("Utility class");
	}


	public static void start(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.start();
	}
}
