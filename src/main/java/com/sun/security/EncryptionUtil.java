package com.sun.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by sun on 2018/3/9 下午1:44.
 */
public class EncryptionUtil {

	/**
	 * String to hold name of the encryption algorithm.
	 */
	public static final String ALGORITHM = "RSA";

	/**
	 * String to hold name of the encryption padding.
	 */
	public static final String PADDING = "RSA/ECB/PKCS1Padding";// RSA/NONE/NoPadding

	/**
	 * String to hold name of the security provider.
	 */
	public static final String PROVIDER = "BC";

	/**
	 * String to hold the name of the private key file.
	 */
	public static final String PRIVATE_KEY_FILE = "/root/private.key";

	/**
	 * String to hold name of the public key file.
	 */
	public static final String PUBLIC_KEY_FILE = "/root/public.key";

	static {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	/**
	 * Generate key which contains a pair of private and public key using 1024
	 * bytes. Store the set of keys in Prvate.key and Public.key files.
	 *
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void generateKey() {
		try {

			final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(
					ALGORITHM, PROVIDER);
			keyGen.initialize(1024);
			final KeyPair key = keyGen.generateKeyPair();

			File privateKeyFile = new File(PRIVATE_KEY_FILE);
			File publicKeyFile = new File(PUBLIC_KEY_FILE);

			// Create files to store public and private key
			if (privateKeyFile.getParentFile() != null) {
				privateKeyFile.getParentFile().mkdirs();
			}
			privateKeyFile.createNewFile();

			if (publicKeyFile.getParentFile() != null) {
				publicKeyFile.getParentFile().mkdirs();
			}
			publicKeyFile.createNewFile();

			// Saving the Public key in a file
			ObjectOutputStream publicKeyOS = new ObjectOutputStream(
					new FileOutputStream(publicKeyFile));
			publicKeyOS.writeObject(key.getPublic());
			publicKeyOS.close();

			// Saving the Private key in a file
			ObjectOutputStream privateKeyOS = new ObjectOutputStream(
					new FileOutputStream(privateKeyFile));
			privateKeyOS.writeObject(key.getPrivate());
			privateKeyOS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * The method checks if the pair of public and private key has been
	 * generated.
	 *
	 * @return flag indicating if the pair of keys were generated.
	 */
	public static boolean areKeysPresent() {

		File privateKey = new File(PRIVATE_KEY_FILE);
		File publicKey = new File(PUBLIC_KEY_FILE);

		if (privateKey.exists() && publicKey.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * Encrypt the plain text using public key.
	 *
	 * @param text
	 *            : original plain text
	 * @param key
	 *            :The public key
	 * @return Encrypted text
	 * @throws java.lang.Exception
	 */
	public static byte[] encrypt(String text, PublicKey key) {
		byte[] cipherText = null;
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance(PADDING, PROVIDER);

			// encrypt the plain text using the public key
			cipher.init(Cipher.ENCRYPT_MODE, key);
			cipherText = cipher.doFinal(text.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipherText;
	}

	/**
	 * Decrypt text using private key.
	 *
	 * @param text
	 *            :encrypted text
	 * @param key
	 *            :The private key
	 * @return plain text
	 * @throws java.lang.Exception
	 */
	public static String decrypt(byte[] text, PrivateKey key) {
		byte[] dectyptedText = null;
		try {
			final Cipher cipher = Cipher.getInstance(PADDING, PROVIDER);

			cipher.init(Cipher.DECRYPT_MODE, key);

			dectyptedText = cipher.doFinal(text);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return new String(dectyptedText);
	}

	/**
	 * ThumbnailatorTest the EncryptionUtil
	 */
	public static void main(String[] args) {

		try {
			if (!areKeysPresent()) {
				generateKey();
			}

			final String originalText = "hello bouncycastle";
			ObjectInputStream inputStream = null;

			inputStream = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
			final PublicKey publicKey = (PublicKey) inputStream.readObject();

			final byte[] cipherText = encrypt(originalText, publicKey);

			// use String to hold cipher binary data
			Base64 base64 = new Base64();
			String cipherTextBase64 = base64.encodeToString(cipherText);

			byte[] cipherTextArray = base64.decode(cipherTextBase64);

			// Decrypt the cipher text using the private key.
			inputStream = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
			final PrivateKey privateKey = (PrivateKey) inputStream.readObject();

			final String plainText = decrypt(cipherText, privateKey);
			//
			System.out.println("Original:\t" + originalText);
			System.out.println("Encrypted:\t" + cipherTextBase64);
			System.out.println("Decrypted:\t" + plainText);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
