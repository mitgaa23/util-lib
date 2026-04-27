package me.mitgaa23.util_lib.math.functions.intMath;

public final class Logarithm {
	private Logarithm() {
	}

	public static int base2(long n) {
		return Long.SIZE - 1 - Long.numberOfLeadingZeros(n);
	}

	public static int base2(int n) {
		return Integer.SIZE - 1 - Integer.numberOfLeadingZeros(n);
	}
}
