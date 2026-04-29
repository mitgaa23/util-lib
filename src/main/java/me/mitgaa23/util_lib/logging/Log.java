package me.mitgaa23.util_lib.logging;

import me.mitgaa23.util_lib.Utils;
import me.mitgaa23.util_lib.collection.ClassLocal;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class Log {
	private static final ClassLocal<Logger> LOGGERS;

	static {
		LogManager manager = LogManager.getLogManager();
		manager.reset();

		init0();

		LOGGERS = new ClassLocal<>(Log::getLogger);

		// Load Utils class (if not loaded already)
		Utils.init();
	}

	private Log() {
	}

	private static void init0() {
		ColorizedLogHandler.init();
	}

	public static void remove(Class<?> key) {
		LOGGERS.remove(key);
	}

	public static Logger get(Class<?> key) {
		return LOGGERS.get(key);
	}

	public static Logger set(Class<?> key, Logger value) {
		return LOGGERS.set(key, value);
	}

	public static void reset(Class<?> key) {
		LOGGERS.reset(key);
	}

	private static Logger getLogger(Class<?> c) {
		Objects.requireNonNull(c, "Cannot get Logger for class null.");

		Logger logger = Logger.getLogger(c.getSimpleName());
		logger.setLevel(Level.ALL);
		return logger;
	}
}
