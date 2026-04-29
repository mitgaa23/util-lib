package me.mitgaa23.util_lib;

import me.mitgaa23.util_lib.logging.Log;

import java.util.logging.Logger;

public final class Utils {
	private static final Logger LOGGER;

	static {
		long start = System.currentTimeMillis();

		// Loads the class Log (if not loaded)
		LOGGER = Log.get(Utils.class);

		init0();

		long end = System.currentTimeMillis();
		long duration = end - start;

		LOGGER.info("%s initialized in %dms.".formatted(Utils.class.getSimpleName(), duration));
	}

	private Utils() {
	}

	/**
	 * Utility method that just forces the class to be loaded.
	 */
	public static void init() {
	}

	private static void init0() {
	}
}
