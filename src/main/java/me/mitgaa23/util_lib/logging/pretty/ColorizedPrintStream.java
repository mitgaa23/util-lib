package me.mitgaa23.util_lib.logging.pretty;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * <a href="https://gist.github.com/ConnerWill/d4b6c776b509add763e17f9f113fd25b">Source of ansi escape codes</a>
 */
public class ColorizedPrintStream extends PrintStream {
	public ColorizedPrintStream(OutputStream out) {
		super(out);
	}

	@Override
	public void print(String s) {
		super.print(Colorizer.colorize(s));
	}

	@Override
	public PrintStream printf(String format, Object... args) {
		format = format.formatted(args);

		format = Colorizer.colorize(format);
		super.print(format);

		return this;
	}
}
