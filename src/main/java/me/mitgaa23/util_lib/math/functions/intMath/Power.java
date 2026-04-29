package me.mitgaa23.util_lib.math.functions.intMath;

public final class Power {
	private Power() {
	}

	public static int binary(int n, int exp) {
		return (int) binary((long) n, exp);
	}

	/// Branchless version of [WikiPedia Article](https://en.wikipedia.org/wiki/Exponentiation_by_squaring)
	public static long binary(long n, long exp) {
		if (n == 0 || exp < 0) {
			return 0;
		}

		if (exp == 0) {
			return 1;
		}

		long multiple = 1;

		while (exp > 1L) {
			multiple *= (exp & 1) * n + (~exp & 1);
			exp >>= 1;
			n *= n;
		}

		return n * multiple;
	}

	public static int binaryMod(int n, int exp, int mod) {
		return (int) binaryMod((long) n, exp, mod);
	}

	/// Branchless modulo version of [WikiPedia Article](https://en.wikipedia.org/wiki/Exponentiation_by_squaring)
	public static long binaryMod(long n, long exp, long mod) {
		if (mod <= 0) {
			throw new IllegalArgumentException("mod %d <= 0".formatted(mod));
		}

		if (n == 0 || exp < 0) {
			return 0;
		}

		if (exp == 0) {
			return 1;
		}

		long multiple = 1;

		while (exp > 1) {
			multiple *= (~exp & 1) + (exp & 1) * n;
			multiple %= mod;

			n *= n;
			n %= mod;

			exp >>= 1;
		}

		return (n * multiple) % mod;
	}
}
