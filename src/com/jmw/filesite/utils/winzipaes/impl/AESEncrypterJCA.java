package com.jmw.filesite.utils.winzipaes.impl;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Encrypter adapter for the Java Cryptography Architecture.
 *
 * @author Matthew Dempsky <mdempsky@google.com>
 */
public class AESEncrypterJCA implements AESEncrypter {

	private byte[] salt;
	private AESUtilsJCA utils;

	public void init(String password, int keySize) {
		salt = createSalt(keySize / 16);
		utils = new AESUtilsJCA(password, keySize, salt);
	}

	public void encrypt(byte[] in, int length) {
		utils.cryptUpdate(in, length);
		utils.authUpdate(in, length);
	}

	public byte[] getSalt() {
		return salt;
	}

	public byte[] getPwVerification() {
		return utils.getPasswordVerifier();
	}

	public byte[] getFinalAuthentication() {
		return utils.getFinalAuthentifier();
	}

	private static final Random RANDOM = new SecureRandom();

	private static byte[] createSalt(int size) {
		byte[] salt = new byte[size];
		RANDOM.nextBytes(salt);
		return salt;
	}

}
