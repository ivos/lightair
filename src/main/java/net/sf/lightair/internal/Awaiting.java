package net.sf.lightair.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * Helper functions for awaiting the target database state.
 */
public class Awaiting {

	private static final Logger log = LoggerFactory.getLogger(Awaiting.class);

	private static final long DELAY_BETWEEN_CALLS = 20;

	private static void sleep() {
		try {
			Thread.sleep(DELAY_BETWEEN_CALLS);
		} catch (InterruptedException e) {
			log.error("Await interrupted.", e);
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
