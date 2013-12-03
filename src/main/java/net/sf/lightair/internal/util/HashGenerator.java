package net.sf.lightair.internal.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashGenerator {

	/**
	 * Generate hash number from a string with precision on the given number of
	 * digits.
	 * <p>
	 * Digits are allowed to be from 2 to 3.
	 * 
	 * @param source
	 *            String to hash
	 * @param digits
	 *            Number of precision digits to return
	 * @return Integer number that is a hash of the source with given precision
	 *         digits
	 */
	public int generateHash(String source, int digits) {
		if (digits < 2 || digits > 3) {
			throw new IllegalArgumentException(
					"Digits must be from 2 to 3, but was " + digits);
		}
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			final byte[] digest = md5.digest(source.getBytes());
			// System.out.println("digest " + digest.length + " "+
			// Hex.encodeHexString(digest));
			int hash = 0x10000 * digest[0] + 0x100 * digest[1] + digest[2];
			// System.out.println("hash " + hash);
			int divisor = (int) Math.pow(10, digits);
			return Math.abs(hash % divisor);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Algorithm MD5 is not accessible.", e);
		}
	}

}
