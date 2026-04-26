package commands;

import me.mitgaa23.util_lib.command.Command;
import me.mitgaa23.util_lib.command.CommandData;
import me.mitgaa23.util_lib.collection.StringWalker;

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
