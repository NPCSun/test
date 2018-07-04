package com.sun.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;

/**
 * 1、公钥加密，私钥解密用于信息加密 2、私钥加密，公钥解密用于数字签名
 */
public class BCEncryptUtil {

	public static final String ALGORITHM = "RSA";

	public static final String PADDING = "RSA/ECB/PKCS1Padding";// BCEncryptUtil/NONE/NoPadding

	public static final String PROVIDER = "BC";

	// 定义公钥关键词
	public static final String KEY_RSA_PUBLICKEY = "RSAPublicKey";

	// 定义私钥关键词
	public static final String KEY_RSA_PRIVATEKEY = "RSAPrivateKey";

	static final String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhFIdzUn4cWD1vOV5T5S+QDPW8qnY4qzda7YUye1bbKjyy67inCOuI/Rtj5ruBlz6QFQCBzYKfbT8IhOMIb//ENlY0T0LAgs82eWscvxta5lgXlZWeeNt0zz/YxbCRlXRg2e1CjBir7TL0fZ8mwVC/9lXtY93BC2UrT9xS972MkaGKIqwosn8T9CaqtT1cTTg3w6gOwshLa3zDtjMVp5u18bwPDJOvHCf1evNej7HsFoWNg8StU6PVxJpRBKmU97Wlm9lLP0bukikMYObp2l/CzseJMOre6owO9PTvGA14fkOMuA0E/KpmyAixZ6EpvpMkbo/hbI44lvjHXxNOxwfYwIDAQAB";

	static final String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCEUh3NSfhxYPW85XlPlL5AM9byqdjirN1rthTJ7VtsqPLLruKcI64j9G2Pmu4GXPpAVAIHNgp9tPwiE4whv/8Q2VjRPQsCCzzZ5axy/G1rmWBeVlZ5423TPP9jFsJGVdGDZ7UKMGKvtMvR9nybBUL/2Ve1j3cELZStP3FL3vYyRoYoirCiyfxP0Jqq1PVxNODfDqA7CyEtrfMO2MxWnm7XxvA8Mk68cJ/V6816PsewWhY2DxK1To9XEmlEEqZT3taWb2Us/Ru6SKQxg5unaX8LOx4kw6t7qjA709O8YDXh+Q4y4DQT8qmbICLFnoSm+kyRuj+FsjjiW+MdfE07HB9jAgMBAAECggEADu7Fdjl21DTBBsRO4HBE2DIBe/k3BL3FbzZpOjCTNLwMSng+EqjkKiKXirFNU2KCy2evouiyXmViXuYd1mE4g8pDf7mH2H80KtMElyVto8r3WS4dLDxCVKh5mdEjs5RTxKSbhb7YJEQfDF7oyQXa/cylXVQHdm0+bh7OxmUvG5U2+0ib/EtsPo+yjQDRjPChZ3KNtCZA7Ei+d+sQl3OSCAsqWiKas79488971fCjNjRRi+7a9mfhNJZr6LulT9cPdEgj1GN8saEG2FdVw5dyqRv5mq4VcmyINAAe4VcksVu0l+nngNeYU7lvaXI4c4/+b3v3jS7qTaSlq+HV8y5u4QKBgQDt9P1t0EZmoyI+Ow+YaCnqnF9ztRA7o5j1Si9Q9vl8nir9J6NdxPUa4GeRD1SYXgdcOasqoxjHHGgN00KrP7yVzzyMWrVoRCw109FXbq0vQzj/ot0C7QSwGHQfjrd90u24zBJC4+QjULkHWamqv2z+F6v8ZGVoZVpR7/gnW4NpdwKBgQCOWpwZzxK6BOC4NPM90o4ibKMWsVFBG4njk9+ZeCUaQFoZ8M3ok/9w6HyiIagaURfke3n1dRsS+kwxGm7assnOTNHtBGx9+tQA8MvyJ/DYjcRGbbKYeqQGa1u6OD/6XEmR6ze9losXjTb0jhkPbJcRTGvj1Z6FAxCupmcl1pV0dQKBgDAMJNP0lxKIZBSutkJu3e/abUeeys1QBkWZGh6+D7hC86k0RL9dUqR/pUncD5fIfLH5jv9H+WvS54vLGY4ci4awVqh8dF6+TTL9NyrxVRTS/QJZL0k09JpeBayNk61bVtbWleVdwKYE2aeLSkAI8QgJXZfT6cn/lRIwYyoHR2yXAoGAPm9vV8KCrCPHjANtTAg1XtPXE/ThdnTlnXMV9vHDFCh1XDtJlGCVAKh3QYURfbljiUq+yvF51nEBSegWBsWzzU/UIuh1zSteIKt8R9FMyS4kj989HbNsjYQ4zwwsw1oGyoEoCXclukate8V3KFSwTV3/VAY1aJFXl8JUKzxagKECgYEAlkUie7XeH+XMD5Als5fRiOGBkUL7VEubXF5gwtZ46ztwmL4d670F+SPz9bSVOjzU6TC+MJzUW4b/etlNxWszb9VkhtaQe/hKedyenSjxy/Bk2f7QRggWUprWaJ695t9n67+4p4gZ1geeIp6NeJY/NMbGJHdX3CTNqTtPq+Kyi7I=";

	private static final Base64 base64 = new Base64();

	//Cipher创建耗时很大，Cipher是有状态的，所以需要加锁，实际应用中，可以用对象池提高tps
	private static Cipher cipher;

	static {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		try {
			cipher = Cipher.getInstance(PADDING, PROVIDER);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成公私密钥对
	 *
	 * @throws NoSuchAlgorithmException
	 */
	public static Map<String, Object> init() throws NoSuchAlgorithmException {
		Map<String, Object> map = null;
		KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
		// 设置密钥对的bit数，越大越安全，但速度减慢，一般使用512或1024
		generator.initialize(1024);
		KeyPair keyPair = generator.generateKeyPair();
		// 获取公钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		// 获取私钥
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		// 将密钥对封装为Map
		map = new HashMap<String, Object>();
		map.put(KEY_RSA_PUBLICKEY, publicKey);
		map.put(KEY_RSA_PRIVATEKEY, privateKey);

		return map;
	}

	/**
	 * 获取Base64编码的公钥字符串
	 */
	public static String getPublicKey(Map<String, Object> map) {
		String str = "";
		Key key = (Key) map.get(KEY_RSA_PUBLICKEY);
		str = encryptBase64(key.getEncoded());
		return str;
	}

	/**
	 * 获取Base64编码的私钥字符串
	 */
	public static String getPrivateKey(Map<String, Object> map) {
		String str = "";
		Key key = (Key) map.get(KEY_RSA_PRIVATEKEY);
		str = encryptBase64(key.getEncoded());
		return str;
	}

	/**
	 * BASE64 解码
	 *
	 * @param data
	 *            需要Base64解码的字符串
	 * @return 字节数组
	 */
	public static byte[] decryptBase64(String data) {
		return base64.decode(data);
	}

	/**
	 * BASE64 编码
	 *
	 * @param data
	 *            需要Base64编码的字节数组
	 * @return 字符串
	 */
	public static String encryptBase64(byte[] data) {
		return base64.encodeToString(data);
	}

	/**
	 * 公钥加密
	 * @param input
	 * @param publicKeyStr
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchProviderException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public synchronized static String encryptByPublic(String input, String publicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		long begin = System.currentTimeMillis();

		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decryptBase64(publicKeyStr));
		KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
		PublicKey publicKey = factory.generatePublic(keySpec);

		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		byte[] data = input.getBytes("UTF-8");

		String result = encryptBase64(cipher.doFinal(data));

		long end = System.currentTimeMillis();
		//System.out.println("公钥加密耗时:\t" + (end-begin));

		return result;
	}

	/**
	 * 私钥解密
	 *
	 * @param encryptedStr
	 * @param privateKeyStr
	 * @return
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws UnsupportedEncodingException
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 */
	public synchronized static String decryptByPrivate(String encryptedStr, String privateKeyStr)
			throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException, NoSuchProviderException {
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decryptBase64(privateKeyStr));
		KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
		PrivateKey privateKey = factory.generatePrivate(keySpec);

		//final Cipher cipher = Cipher.getInstance(PADDING, PROVIDER);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);

		byte[] data = decryptBase64(encryptedStr);

		return new String(cipher.doFinal(data), "UTF-8");

	}

	public static void main(String[] args) {
		final String[] phones = new String[] {"86-18698551980", "86-18698551981", "86-18698551982", "86-18698551983", "86-18698551984"};
		final Random rand = new Random();

		StringBuilder stringBuilder;
		String input;

		while(true){
			stringBuilder = new StringBuilder("201708141536410936741912:" + phones[rand.nextInt(phones.length)] + ":");
			stringBuilder.append(System.currentTimeMillis());
			input = stringBuilder.toString();
			String enStr = null;
			try {
				enStr = BCEncryptUtil.encryptByPublic(input, publicKey);
				String deStr = BCEncryptUtil.decryptByPrivate(enStr, privateKey);
				if(!input.equals(deStr)){
					System.err.println("加密解密数据比对不一致");
				}else{
					System.out.println("加密解密数据比对一致");
				}
				Thread.sleep(2000);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} // end while
	}

}
