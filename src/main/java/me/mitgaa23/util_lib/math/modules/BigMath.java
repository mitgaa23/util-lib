package me.mitgaa23.util_lib.math.modules;

import me.mitgaa23.util_lib.math.functions.RhoFactorization;

import java.math.BigInteger;

public final class BigMath {
	private BigMath() {
	}

	public static RhoFactorization.RhoFactors factorize(BigInteger n) {
		return RhoFactorization.factorize(n);
	}
}
