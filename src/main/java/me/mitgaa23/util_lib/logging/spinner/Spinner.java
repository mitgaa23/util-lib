package me.mitgaa23.util_lib.logging.spinner;

import java.util.function.Supplier;

public final class Spinner {
	private static final ConsoleSpinner spinner = new ConsoleSpinner();

	private Spinner() {
	}

	public static void start() {
		spinner.start();
	}

	public static void start(Supplier<Boolean> condition) {
		spinner.start(condition);
	}

	public static void stop() {
		spinner.stop();
	}

	public static void join() throws InterruptedException {
		spinner.join();
	}

	public static void setMessage(String message) {
		spinner.setMessage(message);
	}
}
