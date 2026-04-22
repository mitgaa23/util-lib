package api.core;

import java.util.Objects;

public record CommandData(String prefix, String name, String delimiter) {
	public CommandData(String prefix, String name, String delimiter) {
		this.prefix = Objects.requireNonNull(prefix);
		this.name = Objects.requireNonNull(name);
		this.delimiter = Objects.requireNonNull(delimiter);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String prefix;
		private String name;
		private String delimiter = " ";

		public Builder prefix(String prefix) {
			this.prefix = prefix;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder delimiter(String delimiter) {
			this.delimiter = delimiter;
			return this;
		}

		public CommandData build() {
			return new CommandData(prefix, name, delimiter);
		}
	}
}
