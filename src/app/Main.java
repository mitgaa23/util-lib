package app;

import api.core.CommandData;
import api.core.Commander;
import api.util.logging.Log;
import api.util.logging.core.Logger;
import app.commands.ExitCommand;

import java.util.Scanner;

public final class Main {
	private Main() {
	}

	public static void main(String[] args) {
		Logger logger = Log.get(Main.class);
		logger.info("Initializing ...");

		Commander commander = new Commander();
		commander.register(new ExitCommand(CommandData.builder().prefix("/").name("exit").build()));
		commander.register(new EchoCommand(CommandData.builder().prefix("/").name("echo").build()));

		logger.info("Initialized.");

		Scanner sc = new Scanner(System.in);
		while (!Thread.currentThread().isInterrupted()) {
			String input = sc.nextLine();

			boolean success = commander.run(input);

			if (success) {
				logger.info("Command '%s' ran successfully.", input);
			} else {
				logger.warn("Command '%s' ran with an error.", input);
			}
		}
	}
}
