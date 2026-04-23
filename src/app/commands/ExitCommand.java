package app.commands;

import api.util.StringWalker;
import api.util.command.Command;
import api.util.command.CommandData;

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
