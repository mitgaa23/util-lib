import me.mitgaa23.util_lib.command.CommandData;
import me.mitgaa23.util_lib.command.Commander;
import me.mitgaa23.util_lib.api.test.commands.EchoCommand;
import me.mitgaa23.util_lib.api.test.commands.ExitCommand;
import me.mitgaa23.util_lib.api.test.commands.TestCommand;
import me.mitgaa23.util_lib.Utils;
import me.mitgaa23.util_lib.logging.Log;

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
