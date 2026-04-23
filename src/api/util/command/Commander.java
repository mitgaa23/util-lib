package api.util.command;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Commander {
	private final Set<Command> commands = new HashSet<>();

	public void register(Command cmd) {
		commands.add(cmd);
	}

	public Optional<Command> get(String str) {
		for (Command cmd : commands) {
			boolean matched = cmd.matches(str);

			if (matched) {
				return Optional.of(cmd);
			}
		}

		return Optional.empty();
	}

	public boolean run(String str) {
		for (Command cmd : commands) {
			if (cmd.runOnMatch(str)) {
				return true;
			}
		}

		return false;
	}
}
