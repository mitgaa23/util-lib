package me.mitgaa23.util_lib.math.modules;

import me.mitgaa23.util_lib.logging.Log;
import me.mitgaa23.util_lib.math.functions.intMath.GreatestCommonDivisor;
import me.mitgaa23.util_lib.math.functions.intMath.Logarithm;
import me.mitgaa23.util_lib.math.functions.intMath.Power;
import me.mitgaa23.util_lib.math.functions.intMath.SquareRoot;

import java.util.logging.Logger;

public final class IntMath {
	private static final Logger LOGGER = Log.get(IntMath.class);

	private IntMath() {
	}

	public static int gcdEuler(int a, int b) {
		return GreatestCommonDivisor.euler(a, b);
	}

	public static long gcdEuler(long a, long b) {
		return GreatestCommonDivisor.euler(a, b);
	}

	public static int gcdBinary(int a, int b) {
		return GreatestCommonDivisor.binary(a, b);
	}

	public static long gcdBinary(long a, long b) {
		return GreatestCommonDivisor.binary(a, b);
	}

	public static int log2(long n) {
		return Logarithm.base2(n);
	}

	public static int log2(int n) {
		return Logarithm.base2(n);
	}

	public static int sqrt(int n) {
		return SquareRoot.binary(n);
	}

	public static long sqrt(long n) {
		return SquareRoot.binary(n);
	}

	public static int sign(long n) {
		return Long.signum(n);
	}

	public static int sign(int n) {
		return Integer.signum(n);
	}

	public static long powerMod(long n, long exp, long mod) {
		return Power.binaryMod(n, exp, mod);
	}

	public static int powerMod(int n, int exp, int mod) {
		return Power.binaryMod(n, exp, mod);
	}

	public static long power(long n, long exp) {
		return Power.binary(n, exp);
	}

	public static int power(int n, int exp) {
		return Power.binary(n, exp);
	}
}
