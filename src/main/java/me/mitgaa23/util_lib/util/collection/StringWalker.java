package me.mitgaa23.util_lib.util.collection;

public class StringWalker {
	public final VersionStack<Integer> pos = new VersionStack<>(0);
	private final char[] chars;

	public StringWalker(String string) {
		this.chars = string.toCharArray();
	}

	public boolean hasRemaining() {
		return pos.get() < chars.length;
	}

	public void jump(int offset) {
		pos.update(old -> old + offset);
	}

	public void reset() {
		pos.clear();
		pos.set(0);
	}

	public boolean match(String str, boolean consume) {
		return match(str.toCharArray(), consume);
	}

	public boolean match(char[] checkedChars, boolean consume) {
		if (pos.get() + checkedChars.length > length()) {
			return false;
		}

		for (int i = 0; i < checkedChars.length; i++) {
			if (checkedChars[i] != chars[pos.get() + i]) {
				return false;
			}
		}

		if (consume) {
			pos.update(old -> old + checkedChars.length);
		}

		return true;
	}

	public int length() {
		return chars.length;
	}

	public String matchUntil(String delimiter, boolean consume) {
		return matchUntil(delimiter.toCharArray(), consume);
	}

	public String matchUntil(char[] delimiter, boolean consume) {
		StringBuilder sb = new StringBuilder();
		int matchCount = 0;

		for (int i = pos.get(); i < length(); i++) {
			boolean delimiterMatched = true;

			for (int j = 0; j < delimiter.length; j++) {
				if (delimiter[j] != chars[i + j]) {
					delimiterMatched = false;
					break;
				}
			}

			if (delimiterMatched) {
				matchCount++;
				continue;
			}

			if (matchCount > 0) {
				break;
			}

			sb.append(chars[i]);
		}

		if (consume) {
			final int finalMatchCount = matchCount;
			pos.update(old -> old + sb.length() + delimiter.length * finalMatchCount);
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		return "StringWalker{" +
				"pos=" + pos +
				" remaining=" + remaining() +
				'}';
	}

	public String remaining() {
		return get().substring(pos.get());
	}

	public String get() {
		return new String(chars);
	}
}
