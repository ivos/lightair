package net.sf.lightair.internal.auto;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.security.MessageDigest;

public class Hash {

	private static MessageDigest md5 = DigestUtils.getDigest(MessageDigestAlgorithms.MD5);

	public static int generate(String source, int digits) {
		if (digits > 4) {
			throw new IllegalArgumentException("Maximum supported digits is 4, but got " + digits);
		}

		md5.reset();
		md5.update(source.getBytes());
		byte[] digest = md5.digest();

//		System.out.println("digest " + digest.length + " " + Hex.encodeHexString(digest));
		int hash = 0x10000 * digest[0] + 0x100 * digest[1] + digest[2];
//		System.out.println("hash " + hash);
		int divisor = (int) Math.pow(10, digits);
		return Math.abs(hash % divisor);
	}
}
