package util;

import util.logging.Log;
import util.logging.ColorizedLogHandler;

import java.util.logging.Logger;

public final class Utils {
	static {
		long start = System.currentTimeMillis();

		init0();

		long duration = System.currentTimeMillis() - start;

		Logger logger = Log.get(Utils.class);
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
