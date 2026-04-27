package me.mitgaa23.util_lib.math.modules;

import me.mitgaa23.util_lib.math.functions.bigMath.SemiPrime;
import me.mitgaa23.util_lib.math.functions.bigMath.RhoFactorization;

import java.math.BigInteger;

public final class BigMath {
	private BigMath() {
	}

	public static SemiPrime factorizeWithRho(BigInteger n) {
		return RhoFactorization.factorize(n);
	}
}
