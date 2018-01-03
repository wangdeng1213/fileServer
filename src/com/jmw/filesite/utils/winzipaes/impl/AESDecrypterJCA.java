package com.jmw.filesite.utils.winzipaes.impl;

import java.util.Arrays;
import java.util.zip.ZipException;

/**
 * Decrypter adapter for the Java Cryptography Architecture.
 *
 * @author Matthew Dempsky <mdempsky@google.com>
 */
public class AESDecrypterJCA implements AESDecrypter {

	private AESUtilsJCA utils;

	public void init(String password, int keySize, byte[] salt, byte[] passwordVerifier) throws ZipException {
		this.utils = new AESUtilsJCA(password, keySize, salt);
		if (!Arrays.equals(passwordVerifier, utils.getPasswordVerifier()))
			throw new ZipException("Password verification failed");
	}

	public void decrypt(byte[] in, int length) {
		utils.authUpdate(in, length);
		utils.cryptUpdate(in, length);
	}

	public byte[] getFinalAuthentication() {
		return utils.getFinalAuthentifier();
	}
}
