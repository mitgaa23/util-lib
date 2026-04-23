package util;

import util.logging.core.ColorizedLogHandler;

import java.util.logging.Logger;

public final class Utils {
	static {
		long start = System.currentTimeMillis();

		init0();

		long end = System.currentTimeMillis();
		long duration = end - start;

		Logger logger = Logger.getLogger(Utils.class.getName());
		logger.info("Utils initialized in %dms.".formatted(duration));
	}

	private Utils() {
	}

	/**
	 * Utility method that just forces the class to be loaded.
	 */
	public static void init() {
	}

	private synchronized static void init0() {
		ColorizedLogHandler.init();
	}
}
