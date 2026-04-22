package api.util.logging;

import api.util.ClassLocal;
import api.util.logging.core.Logger;

public final class Log {
	private static final ClassLocal<Logger> LOGGERS = new ClassLocal<>(Logger::new);

	public static Logger get(Class<?> key) {
		return LOGGERS.get(key);
	}

	public static void remove(Class<?> key) {
		LOGGERS.remove(key);
	}

	public static Logger set(Class<?> key, Logger value) {
		return LOGGERS.set(key, value);
	}

	public static void reset(Class<?> key) {
		LOGGERS.reset(key);
	}
}
