package api.core;

import api.util.StringWalker;
import api.util.logging.Log;
import api.util.logging.core.Logger;

import java.lang.System.Logger.Level;

public abstract class Command {
	private final CommandData data;

	public Command(CommandData data) {
		this.data = data;

		logger();
	}

	public Logger logger() {
		return Log.get(getClass()).ignore(Level.DEBUG);
	}

	public boolean runOnMatch(String str) {
		StringWalker walker = new StringWalker(str);

		if (!matches(walker)) {
			return false;
		}

		logger().debug("'%s' fully matches, running command '%s' ...", str, name());

		run(walker);
		return true;
	}

	protected boolean matches(StringWalker walker) {
		if (!matchCommand(walker)) {
			return false;
		}

		if (!validArgs(walker)) {
			logger().debug("'%s' has invalid args for command '%s'.", walker.get(), name());
			return false;
		}

		logger().debug("'%s' has valid args for command '%s'.", walker.get(), name());
		logger().debug("'%s' fully matches command '%s'", walker.get(), name());

		return true;
	}

	public String name() {
		return data.name();
	}

	protected abstract void run(StringWalker walker);

	private boolean matchCommand(StringWalker walker) {
		if (!walker.match(prefix(), true)) {
			logger().debug("'%s' does not match prefix '%s' for command '%s'.", walker.get(), prefix(), name());
			return false;
		}

		logger().debug("'%s' matches prefix '%s' for command '%s'.", walker.get(), prefix(), name());

		String command = walker.matchUntil(delimiter(), true);
		if (!name().equals(command)) {
			logger().debug("'%s' does not match command '%s'.", walker.get(), name());
			return false;
		}

		logger().debug("'%s' matches command '%s'.", walker.get(), name());
		return true;
	}

	protected abstract boolean validArgs(StringWalker walker);

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
