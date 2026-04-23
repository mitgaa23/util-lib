package api.command;

import util.collection.StringWalker;
import util.logging.Log;

import java.util.logging.Logger;

public abstract class Command {
	private final CommandData data;

	public Command(CommandData data) {
		this.data = data;

		logger();
	}

	public Logger logger() {
		return Log.get(getClass());
	}

	public boolean runOnMatch(String str) {
		return runOnMatch(new StringWalker(str));
	}

	public boolean runOnMatch(StringWalker walker) {
		if (!matches(walker)) {
			return false;
		}

		run(walker);
		return true;
	}

	protected boolean matches(StringWalker walker) {
		if (!matchCommand(walker)) {
			return false;
		}

		walker.pos.push();
		if (!validArgs(walker)) {
			logger().warning("'%s' has invalid args for command '%s'.".formatted(walker.get(), name()));
			return false;
		}
		walker.pos.pop();

		logger().fine("'%s' has valid args for command '%s'.".formatted(walker.get(), name()));
		logger().fine("'%s' fully matches command '%s'".formatted(walker.get(), name()));

		return true;
	}

	protected abstract void run(StringWalker walker);

	private boolean matchCommand(StringWalker walker) {
		if (!walker.match(prefix(), true)) {
			logger().fine("'%s' does not match prefix '%s' for command '%s'.".formatted(walker.get(),
			                                                                            prefix(),
			                                                                            name()
			));
			return false;
		}

		logger().fine("'%s' matches prefix '%s' for command '%s'.".formatted(walker.get(), prefix(), name()));

		String command = walker.matchUntil(delimiter(), true);
		if (!name().equals(command)) {
			logger().fine("'%s' does not match command '%s'.".formatted(command, name()));
			return false;
		}

		logger().fine("'%s' matches command '%s'.".formatted(command, name()));
		return true;
	}

	protected abstract boolean validArgs(StringWalker walker);

	public String name() {
		return data.name();
	}

	public String prefix() {
		return data.prefix();
	}

	public String delimiter() {
		return data.delimiter();
	}

	public boolean matches(String str) {
		return matches(new StringWalker(str));
	}

	@Override
	public String toString() {
		return prefix() + name();
	}
}
