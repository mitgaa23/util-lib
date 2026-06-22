package me.mitgaa23.util_lib.logging.spinner;

import me.mitgaa23.util_lib.logging.pretty.Colorizer;

import java.util.Objects;
import java.util.function.Supplier;

public class ConsoleSpinner implements Runnable {
	private static final int REFRESH_COUNT = 7;
	private static final long UPDATE_DELAY = 10;

	private static final Supplier<Boolean> DEFAULT_CONDITION = () -> true;
	private static final String FORMAT = " " + Colorizer.colorize("{#feceaa}%s{#}") + " ";
	private static final String FORMAT_MESSAGE = FORMAT + " \t%s\r";
	private static final String FORMAT_NO_MESSAGE = FORMAT + "\r";
	private static final String DONE_STATE = "⣏⣹";
	private static final String[] STATES = new String[]{
			//"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"
			//"⡏⠁", "⠏⠉", "⠋⠙", "⠉⠹", "⠈⢹", "⠀⣹", "⢀⣸", "⣀⣰", "⣄⣠", "⣆⣀", "⣇⡀", "⣏⠀"
			"⡏⠁", "⡏⠉",
			"⠏⠉", "⠏⠙",
			"⠋⠙", "⠋⠹",
			"⠉⠹", "⠉⢹",
			"⠈⢹", "⠈⣹",
			"⠀⣹", "⠀⣸",
			"⢀⣸", "⢀⣰",
			"⣀⣰", "⣀⣠",
			"⣄⣠", "⣄⣀",
			"⣆⣀", "⣆⡀",
			"⣇⡀", "⣇⠀",
			"⣏⠀", "⣏⠁",
	};

	private Supplier<Boolean> condition;
	private String message = "";
	private Thread thread;
	private int index;
	private int counter;

	public void start() {
		start(DEFAULT_CONDITION);
	}

	public void start(Supplier<Boolean> condition) {
		stop();
		reset();

		this.condition = Objects.requireNonNull(condition, "condition cannot be null");

		this.thread = Thread.startVirtualThread(this);
	}

	public void stop() {
		if (!isThreadAlive()) {
			return;
		}

		thread.interrupt();

		try {
			join();
		} catch (InterruptedException _) {
		}
	}

	private void reset() {
		this.index = 0;
		this.thread = null;
		this.condition = DEFAULT_CONDITION;

		this.setMessage("");
	}

	private boolean isThreadAlive() {
		return thread != null && thread.isAlive();
	}

	public void join() throws InterruptedException {
		if (isThreadAlive()) {
			thread.join();
		}
	}

	public void setMessage(String message) {
		Objects.requireNonNull(message, "message cannot be null");

		this.message = message;
	}

	@Override
	public void run() {
		try {
			while (condition.get() == true && !Thread.currentThread().isInterrupted()) {
				step();
			}

		} catch (InterruptedException _) {
		}

		print(DONE_STATE, message);
	}

	private void step() throws InterruptedException {
		print(STATES[index], message);
		advanceIndex();

		Thread.sleep(UPDATE_DELAY);
	}

	private static void print(String state, String message) {
		if (message == null) {
			System.out.printf(FORMAT_NO_MESSAGE, state);
		} else {
			System.out.printf(FORMAT_MESSAGE, state, message);
		}

		System.out.flush();
	}

	private void advanceIndex() {
		counter++;

		if (counter > REFRESH_COUNT) {
			counter = 0;
			index = (index + 1) % STATES.length;
		}
	}
}
