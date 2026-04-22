package api.util.logging.core;

public class Logger extends BaseLogger<Logger> {
	public Logger(Class<?> clazz) {
		super(clazz);
	}

	public Logger(String name) {
		super(name);
	}

	public Logger info(String format, Object... args) {
		info(format.formatted(args));
		return this;
	}

	public Logger info(String msg) {
		log(Level.INFO, msg);
		return this;
	}

	@Override
	protected void print(String str) {
		System.out.print(str);
	}

	public Logger debug(String format, Object... args) {
		debug(format.formatted(args));
		return this;
	}

	public Logger debug(String msg) {
		log(Level.DEBUG, msg);
		return this;
	}

	public Logger trace(String format, Object... args) {
		trace(format.formatted(args));
		return this;
	}

	public Logger trace(String msg) {
		log(Level.TRACE, msg);
		return this;
	}

	public Logger warn(String format, Object... args) {
		warn(format.formatted(args));
		return this;
	}

	public Logger warn(String msg) {
		log(Level.WARNING, msg);
		return this;
	}

	public Logger error(String format, Object... args) {
		error(format.formatted(args));
		return this;
	}

	public Logger error(String msg) {
		log(Level.ERROR, msg);
		return this;
	}

	public Logger info(Object obj) {
		info(String.valueOf(obj));
		return this;
	}

	public Logger debug(Object obj) {
		debug(String.valueOf(obj));
		return this;
	}

	public Logger warn(Object obj) {
		warn(String.valueOf(obj));
		return this;
	}

	public Logger error(Object obj) {
		error(String.valueOf(obj));
		return this;
	}

	public Logger trace(Object obj) {
		trace(String.valueOf(obj));
		return this;
	}
}
