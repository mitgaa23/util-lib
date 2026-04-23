package me.mitgaa23.util_lib.api.test.commands;

import me.mitgaa23.util_lib.util.collection.StringWalker;
import me.mitgaa23.util_lib.api.command.Command;
import me.mitgaa23.util_lib.api.command.CommandData;

public class ExitCommand extends Command {
	public ExitCommand(CommandData data) {
		super(data);
	}

	@Override
	protected void run(StringWalker walker) {
		logger().info("exiting ...");

		if (!walker.hasRemaining()) {
			System.exit(0);
			return;
		}

		String remaining = walker.remaining();
		int status = Integer.parseInt(remaining);
		System.exit(status);
	}

	@Override
	protected boolean validArgs(StringWalker walker) {
		if (!walker.hasRemaining()) {
			return true;
		}

		try {
			Integer.parseInt(walker.remaining());
			return true;

		} catch (NumberFormatException _) {
			return false;
		}
	}
}
