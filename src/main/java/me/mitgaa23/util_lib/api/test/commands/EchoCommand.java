package me.mitgaa23.util_lib.api.test.commands;

import me.mitgaa23.util_lib.api.command.Command;
import me.mitgaa23.util_lib.api.command.CommandData;
import me.mitgaa23.util_lib.util.collection.StringWalker;

public class EchoCommand extends Command {
	public EchoCommand(CommandData build) {
		super(build);
	}

	@Override
	protected void run(StringWalker walker) {
		while (walker.hasRemaining()) {
			boolean match = walker.match("\"", true);

			if (match) {
				String between = walker.matchUntil("\"", true);
				System.out.println(between);
			} else {
				walker.jump(1);
			}
		}
	}

	@Override
	protected boolean validArgs(StringWalker walker) {
		return true;
	}
}
