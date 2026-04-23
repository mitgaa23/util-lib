package util.logging;

import util.collection.ClassLocal;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Log {
	private static final ClassLocal<Logger> LOGGERS = new ClassLocal<>(c -> {
		Logger logger = Logger.getLogger(c.getSimpleName());
		logger.setLevel(Level.ALL);
		return logger;
	});

	private Log() {
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
}
