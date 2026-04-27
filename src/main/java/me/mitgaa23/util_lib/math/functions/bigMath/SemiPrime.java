package me.mitgaa23.util_lib.math.functions.bigMath;

import java.math.BigInteger;

public record SemiPrime(BigInteger n, BigInteger p, BigInteger q) {
	public SemiPrime {
		if (!n.equals(p.multiply(q))) {
			throw new IllegalStateException("Product of %d and %d is not %d.".formatted(p, q, n));
		}

		BigInteger oldP = p;
		p = p.min(q);
		q = oldP.max(q);
	}

	@Override
	public String toString() {
		return "%s{%d * %d = %d}".formatted(getClass().getSimpleName(), p, q, n);
	}
}
