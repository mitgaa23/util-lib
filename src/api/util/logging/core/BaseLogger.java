package api.util.logging.core;

import api.util.logging.pretty.Colorizer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class BaseLogger<THIS extends BaseLogger<THIS>> implements System.Logger {
	protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	protected final Set<Level> ignored = new HashSet<>(Collections.singleton(Level.TRACE));
	protected final String name;
	protected volatile boolean enabled = true;

	public BaseLogger(Class<?> clazz) {
		this(clazz.getSimpleName());
	}

	public BaseLogger(String name) {
		this.name = name;

		log(Level.TRACE, "Initialized logger with name '%s'.", name);
	}

	/**
	 * Returns a {@code String} that has the logging format.
	 *
	 * @param level The {@code Level} that the message has.
	 * @param msg   The message to log.
	 * @return A string in the logging format.
	 */
	protected String format(Level level, String msg) {
		String format = "{#cdd6f4}[{#75ffbb} %s {#cdd6f4}|{#bb75ff} %s {#cdd6f4}->{#bb75ff} %s {#cdd6f4}|{#%s} %s {#cdd6f4}]: {#a6adc8}%s{#}";

		String formatted = format.formatted(FORMATTER.format(LocalDateTime.now()),
		                                    getName(),
		                                    Thread.currentThread().getName(),
		                                    Integer.toString(getColor(level), 16),
		                                    level,
		                                    msg
		);

		return Colorizer.colorize(formatted);
	}

	/**
	 * Prints the given string.
	 *
	 * @param str The given string.
	 */
	protected abstract void print(String str);

	/**
	 * Prints a line (like {@code \n} does).
	 *
	 * @return This.
	 * @see #print(String)
	 */
	public THIS line() {
		print("\n");
		return getThis();
	}

	/**
	 * @return True, if this {@code Logger} is enabled.
	 * @see #setEnabled(boolean)
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @return The name of this {@code Logger}.
	 */
	public String getName() {
		return name;
	}

	private int getColor(Level level) {
		return switch (level) {
			case TRACE -> 0xEECCFF;
			case DEBUG -> 0xAADDFF;
			case INFO -> 0x75BBFF;
			case WARNING -> 0xFFBB75;
			case ERROR -> 0xFF7575;
			default -> 0xEEEEEE;
		};
	}

	/**
	 * Gets this Logger, but casted to the generic type THIS.
	 *
	 * @return The casted value.
	 * @apiNote Only done to reduce the use of {@code @SuppressWarnings("unchecked")}
	 */
	@SuppressWarnings("unchecked")
	private THIS getThis() {
		return (THIS) this;
	}

	@Override
	public boolean isLoggable(Level level) {
		return isEnabled() && !ignored.contains(level);
	}

	/**
	 * Logs the given message, if this {@code Logger} is enabled and the given {@code Level} is noticed / not ignored.
	 *
	 * @param level The given level.
	 * @param msg   The message to log.
	 * @see #isEnabled()
	 * @see #notice(Level[])
	 * @see #noticeAll()
	 * @see #ignore(Level[])
	 */
	public void log(Level level, String msg) {
		if (!isLoggable(level)) {
			return;
		}

		String formattedMessage = format(level, msg);

		print(formattedMessage);
		line();
	}

	/**
	 * Logs the given message, if this {@code Logger} is enabled and the given {@code Level} is noticed / not ignored.
	 * Formats the given args into the format and then into the logging format.
	 *
	 * @param level  The given logging level.
	 * @param format The given format.
	 * @param args   The args to format (into {@code format}).
	 * @see #log(Level, String)
	 */
	public void log(Level level, String format, Object... args) {
		log(level, format.formatted(args));
	}

	@Override
	public void log(Level level, ResourceBundle bundle, String msg, Throwable thrown) {
		log(level, msg);
	}

	@Override
	public void log(Level level, ResourceBundle bundle, String format, Object... params) {
		log(level, format, params);
	}

	/**
	 * Completely enables or disables any output done by this {@code Logger}.
	 *
	 * @param enabled The new state.
	 * @return This.
	 * @see #isEnabled()
	 */
	public THIS setEnabled(boolean enabled) {
		this.enabled = enabled;
		return getThis();
	}

	/**
	 * Clears a line (like {@code \r} does).
	 *
	 * @return This.
	 * @see #print(String)
	 */
	public THIS clearLine() {
		print("\r");
		return getThis();
	}

	/**
	 * Ignores the given {@code Level}s from being logged.
	 *
	 * @param levels The given levels.
	 * @return This.
	 * @see #setEnabled(boolean)
	 * @see #notice(Level[])
	 * @see #noticeAll()
	 */
	public THIS ignore(Level... levels) {
		Arrays.sort(levels, Comparator.comparingInt(Level::getSeverity));

		for (Level level : levels) {
			log(Level.TRACE, "Ignoring log calls with level %s.", level);
			ignored.add(level);
		}

		return getThis();
	}

	/**
	 * Notices (logs) the given {@code Level}s again. (If they have been ignored)
	 *
	 * @param levels The given {@code Level}s
	 * @return This.
	 * @see #noticeAll()
	 * @see #ignore(Level[])
	 */
	public THIS notice(Level... levels) {
		Arrays.sort(levels, Comparator.comparingInt(Level::getSeverity).reversed());

		for (Level level : levels) {
			if (ignored.remove(level)) {
				log(Level.TRACE, "No longer ignoring log calls from level %s.", level);
			}
		}

		return getThis();
	}

	/**
	 * Notices (logs) all log {@code Level}s.
	 *
	 * @return This.
	 * @see #notice(Level[])
	 * @see #ignore(Level[])
	 */
	public THIS noticeAll() {
		ignored.clear();

		log(Level.TRACE, "No longer ignoring any log calls.");

		return getThis();
	}

	/**
	 * Ignored all log calls.
	 *
	 * @return This.
	 * @see #notice(Level[])
	 * @see #noticeAll()
	 * @see #ignore(Level[])
	 */
	public THIS ignoreAll() {
		ignored.addAll(Arrays.asList(Level.values()));
		return getThis();
	}
}
