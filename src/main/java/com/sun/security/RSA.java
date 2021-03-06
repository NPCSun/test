package com.sun.security;

/**
 * Created by sun on 2018/3/7 下午9:53.
 */

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * 1、公钥加密，私钥解密用于信息加密 2、私钥加密，公钥解密用于数字签名
 */
public class RSA {
	/**
	 * 测试方法
	 *
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public static void main(String[] args)
			throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, SignatureException {
		String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhFIdzUn4cWD1vOV5T5S+QDPW8qnY4qzda7YUye1bbKjyy67inCOuI/Rtj5ruBlz6QFQCBzYKfbT8IhOMIb//ENlY0T0LAgs82eWscvxta5lgXlZWeeNt0zz/YxbCRlXRg2e1CjBir7TL0fZ8mwVC/9lXtY93BC2UrT9xS972MkaGKIqwosn8T9CaqtT1cTTg3w6gOwshLa3zDtjMVp5u18bwPDJOvHCf1evNej7HsFoWNg8StU6PVxJpRBKmU97Wlm9lLP0bukikMYObp2l/CzseJMOre6owO9PTvGA14fkOMuA0E/KpmyAixZ6EpvpMkbo/hbI44lvjHXxNOxwfYwIDAQAB";
		String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCEUh3NSfhxYPW85XlPlL5AM9byqdjirN1rthTJ7VtsqPLLruKcI64j9G2Pmu4GXPpAVAIHNgp9tPwiE4whv/8Q2VjRPQsCCzzZ5axy/G1rmWBeVlZ5423TPP9jFsJGVdGDZ7UKMGKvtMvR9nybBUL/2Ve1j3cELZStP3FL3vYyRoYoirCiyfxP0Jqq1PVxNODfDqA7CyEtrfMO2MxWnm7XxvA8Mk68cJ/V6816PsewWhY2DxK1To9XEmlEEqZT3taWb2Us/Ru6SKQxg5unaX8LOx4kw6t7qjA709O8YDXh+Q4y4DQT8qmbICLFnoSm+kyRuj+FsjjiW+MdfE07HB9jAgMBAAECggEADu7Fdjl21DTBBsRO4HBE2DIBe/k3BL3FbzZpOjCTNLwMSng+EqjkKiKXirFNU2KCy2evouiyXmViXuYd1mE4g8pDf7mH2H80KtMElyVto8r3WS4dLDxCVKh5mdEjs5RTxKSbhb7YJEQfDF7oyQXa/cylXVQHdm0+bh7OxmUvG5U2+0ib/EtsPo+yjQDRjPChZ3KNtCZA7Ei+d+sQl3OSCAsqWiKas79488971fCjNjRRi+7a9mfhNJZr6LulT9cPdEgj1GN8saEG2FdVw5dyqRv5mq4VcmyINAAe4VcksVu0l+nngNeYU7lvaXI4c4/+b3v3jS7qTaSlq+HV8y5u4QKBgQDt9P1t0EZmoyI+Ow+YaCnqnF9ztRA7o5j1Si9Q9vl8nir9J6NdxPUa4GeRD1SYXgdcOasqoxjHHGgN00KrP7yVzzyMWrVoRCw109FXbq0vQzj/ot0C7QSwGHQfjrd90u24zBJC4+QjULkHWamqv2z+F6v8ZGVoZVpR7/gnW4NpdwKBgQCOWpwZzxK6BOC4NPM90o4ibKMWsVFBG4njk9+ZeCUaQFoZ8M3ok/9w6HyiIagaURfke3n1dRsS+kwxGm7assnOTNHtBGx9+tQA8MvyJ/DYjcRGbbKYeqQGa1u6OD/6XEmR6ze9losXjTb0jhkPbJcRTGvj1Z6FAxCupmcl1pV0dQKBgDAMJNP0lxKIZBSutkJu3e/abUeeys1QBkWZGh6+D7hC86k0RL9dUqR/pUncD5fIfLH5jv9H+WvS54vLGY4ci4awVqh8dF6+TTL9NyrxVRTS/QJZL0k09JpeBayNk61bVtbWleVdwKYE2aeLSkAI8QgJXZfT6cn/lRIwYyoHR2yXAoGAPm9vV8KCrCPHjANtTAg1XtPXE/ThdnTlnXMV9vHDFCh1XDtJlGCVAKh3QYURfbljiUq+yvF51nEBSegWBsWzzU/UIuh1zSteIKt8R9FMyS4kj989HbNsjYQ4zwwsw1oGyoEoCXclukate8V3KFSwTV3/VAY1aJFXl8JUKzxagKECgYEAlkUie7XeH+XMD5Als5fRiOGBkUL7VEubXF5gwtZ46ztwmL4d670F+SPz9bSVOjzU6TC+MJzUW4b/etlNxWszb9VkhtaQe/hKedyenSjxy/Bk2f7QRggWUprWaJ695t9n67+4p4gZ1geeIp6NeJY/NMbGJHdX3CTNqTtPq+Kyi7I=";

		String str = "Hello BC";
		// 公钥加密，私钥解密
		long begin = System.currentTimeMillis();
		String enStr1 = RSA.encryptByPublic(str, publicKey);
		long end = System.currentTimeMillis();
		System.out.println("公钥加密耗时:\t" + (end-begin));
		System.out.println("公钥加密后：" + enStr1);

		begin = System.currentTimeMillis();
		String deStr1 = RSA.decryptByPrivate(enStr1, privateKey);
		end = System.currentTimeMillis();
		System.out.println("私钥机密耗时:\t" + (end-begin));
		System.out.println("私钥解密后：" + deStr1);

	}

	// 定义加密方式 RSA/ECB/PKCS1Padding
	public static final String KEY_RSA = "RSA";



	// 定义公钥关键词
	public static final String KEY_RSA_PUBLICKEY = "RSAPublicKey";

	// 定义私钥关键词
	public static final String KEY_RSA_PRIVATEKEY = "RSAPrivateKey";

	// 定义签名算法
	private final static String KEY_RSA_SIGNATURE = "MD5withRSA";

	/**
	 * 生成公私密钥对
	 *
	 * @throws NoSuchAlgorithmException
	 */
	public static Map<String, Object> init() throws NoSuchAlgorithmException {
		Map<String, Object> map = null;
		KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_RSA);
		// 设置密钥对的bit数，越大越安全，但速度减慢，一般使用512或1024
		generator.initialize(2048);
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
	 * @param key
	 *            需要Base64解码的字符串
	 * @return 字节数组
	 */
	public static byte[] decryptBase64(String key) {
		return Base64.getDecoder().decode(key);
	}

	/**
	 * BASE64 编码
	 *
	 * @param key
	 *            需要Base64编码的字节数组
	 * @return 字符串
	 */
	public static String encryptBase64(byte[] key) {
		return new String(Base64.getEncoder().encode(key));
	}

	/**
	 * 公钥加密
	 *
	 * @param encryptingStr
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public static String encryptByPublic(String encryptingStr, String publicKeyStr)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		// 将公钥由字符串转为UTF-8格式的字节数组
		byte[] publicKeyBytes = decryptBase64(publicKeyStr);
		// 获得公钥
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
		// 取得待加密数据
		byte[] data = encryptingStr.getBytes("UTF-8");
		KeyFactory factory;
		factory = KeyFactory.getInstance(KEY_RSA);
		PublicKey publicKey = factory.generatePublic(keySpec);
		// 对数据加密
		//Cipher cipher = Cipher.getInstance(factory.getAlgorithm());
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		// 返回加密后由Base64编码的加密信息
		return encryptBase64(cipher.doFinal(data));

	}

	/**
	 * 私钥解密
	 *
	 * @param encryptedStr
	 * @param privateKey
	 * @return
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws UnsupportedEncodingException
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 */
	public static String decryptByPrivate(String encryptedStr, String privateKeyStr)
			throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
		// 对私钥解密
		byte[] privateKeyBytes = decryptBase64(privateKeyStr);
		// 获得私钥
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
		// 获得待解密数据
		byte[] data = decryptBase64(encryptedStr);
		KeyFactory factory = KeyFactory.getInstance(KEY_RSA);
		PrivateKey privateKey = factory.generatePrivate(keySpec);
		// 对数据解密  "RSA/ECB/PKCS1Padding"
		//Cipher cipher = Cipher.getInstance(factory.getAlgorithm());
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		// 返回UTF-8编码的解密信息
		return new String(cipher.doFinal(data), "UTF-8");

	}

	/**
	 * 私钥加密
	 *
	 * @param encryptingStr
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public static String encryptByPrivate(String encryptingStr, String privateKeyStr)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
		byte[] privateKeyBytes = decryptBase64(privateKeyStr);
		// 获得私钥
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
		// 取得待加密数据
		byte[] data = encryptingStr.getBytes("UTF-8");
		KeyFactory factory = KeyFactory.getInstance(KEY_RSA);
		PrivateKey privateKey = factory.generatePrivate(keySpec);
		// 对数据加密
		Cipher cipher = Cipher.getInstance(factory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		// 返回加密后由Base64编码的加密信息
		return encryptBase64(cipher.doFinal(data));

	}

	/**
	 * 公钥解密
	 *
	 * @param encryptedStr
	 * @param privateKey
	 * @return
	 */
	public static String decryptByPublic(String encryptedStr, String publicKeyStr) {
		try {
			// 对公钥解密
			byte[] publicKeyBytes = decryptBase64(publicKeyStr);
			// 取得公钥
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
			// 取得待加密数据
			byte[] data = decryptBase64(encryptedStr);
			KeyFactory factory = KeyFactory.getInstance(KEY_RSA);
			PublicKey publicKey = factory.generatePublic(keySpec);
			// 对数据解密
			Cipher cipher = Cipher.getInstance(factory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			// 返回UTF-8编码的解密信息
			return new String(cipher.doFinal(data), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 用私钥对加密数据进行签名
	 *
	 * @param encryptedStr
	 * @param privateKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public static String sign(String encryptedStr, String privateKey)
			throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
		String str = "";
		// 将私钥加密数据字符串转换为字节数组
		byte[] data = encryptedStr.getBytes();
		// 解密由base64编码的私钥
		byte[] bytes = decryptBase64(privateKey);
		// 构造PKCS8EncodedKeySpec对象
		PKCS8EncodedKeySpec pkcs = new PKCS8EncodedKeySpec(bytes);
		// 指定的加密算法
		KeyFactory factory = KeyFactory.getInstance(KEY_RSA);
		// 取私钥对象
		PrivateKey key = factory.generatePrivate(pkcs);
		// 用私钥对信息生成数字签名
		Signature signature = Signature.getInstance(KEY_RSA_SIGNATURE);
		signature.initSign(key);
		signature.update(data);
		str = encryptBase64(signature.sign());
		return str;
	}

	/**
	 * 校验数字签名
	 *
	 * @param encryptedStr
	 * @param publicKey
	 * @param sign
	 * @return 校验成功返回true，失败返回false
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public static boolean verify(String encryptedStr, String publicKey, String sign)
			throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
		boolean flag = false;
		// 将私钥加密数据字符串转换为字节数组
		byte[] data = encryptedStr.getBytes();
		// 解密由base64编码的公钥
		byte[] bytes = decryptBase64(publicKey);
		// 构造X509EncodedKeySpec对象
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
		// 指定的加密算法
		KeyFactory factory = KeyFactory.getInstance(KEY_RSA);
		// 取公钥对象
		PublicKey key = factory.generatePublic(keySpec);
		// 用公钥验证数字签名
		Signature signature = Signature.getInstance(KEY_RSA_SIGNATURE);
		signature.initVerify(key);
		signature.update(data);
		flag = signature.verify(decryptBase64(sign));
		return flag;
	}
}
