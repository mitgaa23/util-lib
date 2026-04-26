package me.mitgaa23.util_lib.math.modules;

import me.mitgaa23.util_lib.logging.Log;

import java.util.logging.Logger;

public final class IntMath {
	private static final Logger LOGGER = Log.get(IntMath.class);

	private IntMath() {
	}

	public static int sqrt(int num) {
		return (int) sqrt((long) num);
	}

	/// [WikiPedia Article](https://en.wikipedia.org/wiki/Square_root_algorithms#Binary_numeral_system_(base_2))
	public static long sqrt(long n) {
		if (n < 0) {
			throw new IllegalArgumentException("Cannot take root of negative number %d.".formatted(n));
		}

		long result = 0;
		long op = n;

		// 4 ^ exp < n
		long exp = log2(n) >> 1;
		long pow4 = 1L << (exp << 1);

		while (pow4 != 0) {
			long sum = result + pow4;

			if (op >= sum) {
				op -= sum;
				result += pow4 << 1;
			}

			result >>= 1;
			pow4 >>= 2; // pow4 / 4
		}

		return result;
	}

	public static int log2(long n) {
		return Long.SIZE - 1 - Long.numberOfLeadingZeros(n);
	}

	public static int log2(int n) {
		return Integer.SIZE - 1 - Integer.numberOfLeadingZeros(n);
	}

	public static int gcdEuler(int a, int b) {
		return (int) gcdEuler((long) a, b);
	}

	public static long gcdEuler(long a, long b) {
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

	public static int gcd(int a, int b) {
		return (int) gcd((long) a, b);
	}

	/// [WikiPedia Article](https://de.wikipedia.org/wiki/Steinscher_Algorithmus)
	public static long gcd(long a, long b) {
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

	public static int sign(long n) {
		return Long.signum(n);
	}

	public static int sign(int n) {
		return Integer.signum(n);
	}
}
