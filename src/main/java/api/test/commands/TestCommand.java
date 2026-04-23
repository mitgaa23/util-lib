package api.test.commands;

import api.command.Command;
import api.command.CommandData;
import util.collection.StringWalker;

import javax.swing.*;

public class TestCommand extends Command {
	private JFrame frame;

	public TestCommand(CommandData data) {
		super(data);
	}

	@Override
	protected void run(StringWalker walker) {
		if (walker.match("open", true) && !walker.hasRemaining()) {
			createWindow();
			return;
		}

		if (frame == null) {
			return;
		}

		if (walker.match("close", true) && !walker.hasRemaining()) {
			frame.dispose();
			frame = null;
		}
	}

	private void createWindow() {
		if (frame != null) {
			frame.dispose();
		}

		frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		int size = 25;
		frame.setSize(16 * size, 9 * size);

		frame.setVisible(true);
	}

	@Override
	protected boolean validArgs(StringWalker walker) {
		boolean matches = walker.match("open", true) || walker.match("close", true);
		return matches && !walker.hasRemaining();
	}
}
