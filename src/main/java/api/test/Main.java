package api.test;

import api.command.CommandData;
import api.command.Commander;
import api.test.commands.EchoCommand;
import api.test.commands.ExitCommand;
import api.test.commands.TestCommand;
import util.Utils;
import util.logging.Log;

import java.util.Scanner;
import java.util.logging.Logger;

public final class Main {
	private Main() {
	}

	public static void main(String[] args) {
		Utils.init();

		Logger logger = Log.get(Main.class);
		logger.info("Initializing ...");

		Commander commander = new Commander();
		commander.register(new ExitCommand(CommandData.builder().prefix("/").name("exit").build()));
		commander.register(new EchoCommand(CommandData.builder().prefix("/").name("echo").build()));
		commander.register(new TestCommand(CommandData.builder().prefix("/").name("window").build()));

		logger.info("Initialized.");

		Scanner sc = new Scanner(System.in);
		while (!Thread.currentThread().isInterrupted()) {
			String input = sc.nextLine();

			boolean success = commander.run(input);

			if (success) {
				logger.info("Command '%s' ran successfully.".formatted(input));
			} else {
				logger.info("Command '%s' ran with an error.".formatted(input));
			}
		}
	}
}
