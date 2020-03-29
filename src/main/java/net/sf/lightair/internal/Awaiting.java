package net.sf.lightair.internal;

import java.util.concurrent.Callable;

/**
 * Helper functions for awaiting target database state.
 */
public class Awaiting {

	private static final long DELAY_BETWEEN_CALLS = 20;

	private static void sleep() {
		try {
			Thread.sleep(DELAY_BETWEEN_CALLS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static <T> T resolveCallable(Callable<T> callable) {
		try {
			return callable.call();
		} catch (Exception e) {
			throw new RuntimeException("Error calling function.", e);
		}
	}

	public static String awaitEmpty(long limit, Callable<String> function) {
		long start = System.currentTimeMillis();
		String result = resolveCallable(function);
		while (!result.isEmpty() && (System.currentTimeMillis() - start <= limit)) {
			sleep();
			result = resolveCallable(function);
		}
		return result;
	}
}
