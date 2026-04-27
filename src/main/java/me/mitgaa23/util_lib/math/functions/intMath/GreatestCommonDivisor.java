package me.mitgaa23.util_lib.math.functions.intMath;

public final class GreatestCommonDivisor {
	private GreatestCommonDivisor() {
	}

	public static int euler(int a, int b) {
		return (int) euler((long) a, b);
	}

	public static long euler(long a, long b) {
		long min = Math.min(a, b);
		b = Math.max(a, b);
		a = min;

		while (b != 0) {
			long oldA = a;
			a = b;
			b = oldA % b;
		}

		return a;
	}

	public static int binary(int a, int b) {
		return (int) binary((long) a, b);
	}

	/// [WikiPedia Article](https://de.wikipedia.org/wiki/Steinscher_Algorithmus)
	public static long binary(long a, long b) {
		if (a == 0) {
			return b;
		}

		long min = Math.min(a, b);
		b = Math.max(a, b);
		a = min;

		int k = Long.numberOfTrailingZeros(a | b);
		a >>= k;

		do {
			b >>= Long.numberOfTrailingZeros(b);

			min = Math.min(a, b);
			b = Math.max(a, b);
			a = min;

			b -= a;

		} while (b != 0);

		return a << k;
	}
}
