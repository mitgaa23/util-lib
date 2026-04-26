package me.mitgaa23.util_lib.logging;

import me.mitgaa23.util_lib.logging.pretty.Colorizer;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public class ColorizedLogHandler extends Handler {
	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	public static final String LOG_FORMAT = "{#cdd6f4}[{#75ffbb} {time} {#cdd6f4}|{#bb75ff} {thread} {#cdd6f4}->{#bb75ff} {name} {#cdd6f4}|{#%x} {level} {#cdd6f4}]:{#a6adc8} {msg}";

	private static final PrintStream OUT = System.out;
	private static final ThreadMXBean THREAD_MX_BEAN = ManagementFactory.getThreadMXBean();

	public static void init() {
		LogManager manager = LogManager.getLogManager();
		manager.reset();

		Logger rootLogger = manager.getLogger("");

		if (rootLogger != null) {
			rootLogger.addHandler(new ColorizedLogHandler());
		}
	}

	@Override
	public void publish(LogRecord record) {
		ThreadInfo threadInfo = THREAD_MX_BEAN.getThreadInfo(record.getLongThreadID());

		Level level = record.getLevel();
		String msg = record.getMessage();
		String name = record.getLoggerName();
		Instant instant = record.getInstant();
		String threadName = threadInfo.getThreadName();
		LocalDateTime time = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

		String format = LOG_FORMAT.formatted(getColor(level));
		format = format.replaceAll("\\{time}", FORMATTER.format(time));
		format = format.replaceAll("\\{name}", name);
		format = format.replaceAll("\\{level}", level.getName());
		format = format.replaceAll("\\{thread}", threadName);
		format = format.replaceAll("\\{msg}", msg);

		String colorized = Colorizer.colorize(format);

		OUT.println(colorized);
	}

	private static int getColor(Level level) {
		if (level == Level.FINEST) {
			return 0xCCFFFF;
		}

		if (level == Level.FINER) {
			return 0xBBEEFF;
		}

		if (level == Level.FINE) {
			return 0xAADDFF;
		}

		if (level == Level.INFO) {
			return 0x75BBFF;
		}

		if (level == Level.WARNING) {
			return 0xFFBB75;
		}

		if (level == Level.SEVERE) {
			return 0xFF7575;
		}

		return 0xEEEEEE;
	}

	@Override
	public void flush() {
		// We do not need to flush the output stream, but we could.
		// OUT.flush();
	}

	@Override
	public void close() {
		// We do not want to close the output stream, as it will become useless, but we could.
		// OUT.close();
	}
}
