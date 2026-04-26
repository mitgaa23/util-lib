package me.mitgaa23.util_lib.logging.pretty;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Colorizer {
	public static final String ESCAPE_CODE = "\033";
	public static final String FOREGROUND_ESCAPE = escape("[38;2;%s;%s;%sm");
	public static final String BACKGROUND_ESCAPE = escape("[48;2;%s;%s;%sm");
	public static final String FOREGROUND_RESET = escape("[38;0m");
	public static final String BACKGROUND_RESET = escape("[48;0m");
	public static final String COLOR_REGEX = "#([0-9a-fA-F]{0,6})";
	public static final Pattern FOREGROUND_PATTERN = Pattern.compile("\\{" + COLOR_REGEX + "}");
	public static final Pattern BACKGROUND_PATTERN = Pattern.compile("\\[" + COLOR_REGEX + "]");

	private Colorizer() {
	}

	public static String escape(String str) {
		return (ESCAPE_CODE + "%s").formatted(str);
	}

	public static String foreground(int rgb) {
		return getColorString(rgb, FOREGROUND_ESCAPE, FOREGROUND_RESET);
	}

	private static String getColorString(int rgb, String escape, String reset) {
		if (rgb <= 0) {
			return reset;
		}

		int r = getRed(rgb);
		int g = getGreen(rgb);
		int b = getBlue(rgb);

		return escape.formatted(r, g, b);
	}

	private static int getRed(int rgb) {
		return (rgb >> 16) & 0xFF;
	}

	private static int getGreen(int rgb) {
		return (rgb >> 8) & 0xFF;
	}

	private static int getBlue(int rgb) {
		return rgb & 0xFF;
	}

	public static String background(int rgb) {
		return getColorString(rgb, BACKGROUND_ESCAPE, BACKGROUND_RESET);
	}

	public static String colorize(String str) {
		Matcher foregroundMatcher = FOREGROUND_PATTERN.matcher(str);

		str = insertAnsiEscapes(str, foregroundMatcher, Colorizer::foreground);
		Matcher backgroundMatcher = BACKGROUND_PATTERN.matcher(str);

		return insertAnsiEscapes(str, backgroundMatcher, Colorizer::background);
	}

	private static String insertAnsiEscapes(String str, Matcher matcher, Function<Integer, String> rgbFunc) {
		StringBuilder sb = new StringBuilder();
		int currIndex = 0;

		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();

			sb.append(str, currIndex, start);
			currIndex = end;

			String group = matcher.group(1);

			int rgb = -1;
			if (group != null && !group.isEmpty()) {
				rgb = Integer.parseInt(group, 16);
			}

			sb.append(rgbFunc.apply(rgb));
		}

		if (currIndex < str.length()) {
			sb.append(str, currIndex, str.length());
		}

		// reset
		sb.append(rgbFunc.apply(-1));

		return sb.toString();
	}
}
