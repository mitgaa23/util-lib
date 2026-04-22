package app;

import api.core.Command;
import api.core.CommandData;
import api.util.StringWalker;

public class EchoCommand extends Command {
	public EchoCommand(CommandData build) {
		super(build);
	}

	@Override
	protected void run(StringWalker walker) {
		System.out.println(walker.remaining());
	}

	@Override
	protected boolean validArgs(StringWalker walker) {
		return true;
	}
}
