package me.mitgaa23.util_lib.math.functions.bigMath;

import me.mitgaa23.util_lib.logging.Log;
import me.mitgaa23.util_lib.math.modules.IntMath;

import java.math.BigInteger;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class RhoFactorization {
	public static final int LOG_START_SIZE = 1 << 16;
	private static final Logger LOGGER = Log.get(RhoFactorization.class);

	static {
		LOGGER.setLevel(Level.INFO);
	}

	private RhoFactorization() {
	}

	public static SemiPrime factorize(BigInteger n) {
		Random rnd = new Random();

		while (true) {
			BigInteger factor = tryFactorization(n, rnd);

			if (!BigInteger.ONE.equals(factor)) {
				final SemiPrime factors = new SemiPrime(n, factor, n.divide(factor));
				LOGGER.info("Rho factorization: %s".formatted(factors));

				return factors;
			}

			LOGGER.warning("Rho factorization failed, retrying ...");
		}
	}

	private static BigInteger tryFactorization(BigInteger n, Random rnd) {
		final BigInteger magicConstant = n.sqrt().pow(n.bitLength()).mod(n);
		final BigInteger c = magicConstant.add(randBigInt(BigInteger.ONE, magicConstant, rnd));

		final long minBatchSize = 1 << (IntMath.log2(n.bitLength()) + 1);

		long blockSize = 1;
		BigInteger rabbit = c.sqrt().add(randBigInt(BigInteger.ONE, n, rnd));
		BigInteger factor = BigInteger.ONE;

		// Wiederhole, wenn der berechnete Faktor nicht 1 ist
		while (BigInteger.ONE.equals(factor)) {
			// Schildkröte zieht hinter dem Hasen her
			final BigInteger turtle = rabbit;

			final long loggedBlockSize = blockSize;
			if (blockSize >= LOG_START_SIZE) {
				LOGGER.finest(() -> "Started processing block with size: %d".formatted(loggedBlockSize));
			}

			// Der Hase macht blockSize Schritte
			for (long i = 0; i < blockSize; i++) {
				rabbit = f(rabbit, c, n);
			}

			// Verarbeite Blöcke bis ein Faktor gefunden wurde
			long block = 0;
			while (block < blockSize && BigInteger.ONE.equals(factor)) {
				BigInteger product = BigInteger.ONE;

				// Jeder Batch ist kürzer als der vorherige
				long batchSize = Math.min(minBatchSize, blockSize - block);

				// Hase macht batchSize Schritte
				for (long i = 0; i < batchSize; i++) {
					// Hase macht einen Schritt
					rabbit = f(rabbit, c, n);

					// Differenz zwischen Hasen und Schildkröte
					final BigInteger diff = turtle.subtract(rabbit).abs();

					// Multiplizieren der Differenz von oben mit dem Gesamtprodukt
					// und rechne mod n
					product = product.multiply(diff).mod(n);
				}

				// GCD (GGT, größter gemeinsamer Teiler) von n und dem Produkt berechnen.
				// Wenn der GCD nicht 1 ist, dann haben wir eine Kollision im zyklus
				// und somit auch einen Faktor von n.
				factor = n.gcd(product);

				// Erhöhe den Block um die gemachten Schritte
				block += batchSize;
			}


			if (blockSize >= LOG_START_SIZE) {
				LOGGER.finest(() -> "Completed block with size: %d".formatted(loggedBlockSize));
			}

			blockSize <<= 1;
		}

		return factor.min(n.divide(factor));
	}

	private static BigInteger randBigInt(BigInteger lower, BigInteger upper, Random rnd) {
		BigInteger num = new BigInteger(upper.bitLength(), rnd);

		return num.mod(upper.subtract(lower)).add(lower);
	}

	private static BigInteger f(BigInteger x, BigInteger c, BigInteger n) {
		return x.multiply(x).add(c).mod(n);
	}
}
