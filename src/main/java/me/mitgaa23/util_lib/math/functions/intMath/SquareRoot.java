package me.mitgaa23.util_lib.math.functions.intMath;

public final class SquareRoot {
	private SquareRoot() {
	}

	public static int binary(int num) {
		return (int) binary((long) num);
	}

	/// [WikiPedia Article](https://en.wikipedia.org/wiki/Square_root_algorithms#Binary_numeral_system_(base_2))
	public static long binary(long n) {
		if (n < 0) {
			throw new IllegalArgumentException("Cannot take root of negative number %d.".formatted(n));
		}

		long result = 0;
		long op = n;

		// 4 ^ exp < n
		long exp = Logarithm.base2(n) >> 1;
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
}
