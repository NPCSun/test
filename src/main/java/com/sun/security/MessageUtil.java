package com.sun.security;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;

public class MessageUtil {
	//证书库类型
	private String ksType = "JKS";//JKS  PKCS12

	private static CertificateFactory certFactory;
	private static X509Certificate cert;

	static{
		// 添加BouncyCastle作为安全提供
		Security.addProvider(new BouncyCastleProvider());
		try {
			certFactory = CertificateFactory.getInstance("X.509", new BouncyCastleProvider());
			InputStream inputStream = new FileInputStream("/root/sunfei.crt");
			cert = (X509Certificate) certFactory.generateCertificate(inputStream);
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成数字签名
	 * @param srcMsg 源信息
	 * @param charSet 字符编码
	 * @param certPath 证书路径
	 * @param certPwd 证书密码
	 * @return
	 */
	public byte[] signMessage(String srcMsg, String charSet, String certPath, String certPwd) {
		String priKeyName = null;
		long begin = System.currentTimeMillis();
		char passphrase[] = certPwd.toCharArray();

		try {
			// 加载证书
			KeyStore keyStore = KeyStore.getInstance(ksType);
			keyStore.load(new FileInputStream(certPath), passphrase);

			if (keyStore.aliases().hasMoreElements()) {
				priKeyName = keyStore.aliases().nextElement();
			}

			Certificate cert = (Certificate) keyStore.getCertificate(priKeyName);
			X509Certificate cerx509 = (X509Certificate) cert;

			// 获取私钥
			PrivateKey prikey = (PrivateKey) keyStore.getKey(priKeyName, passphrase);

			List<Certificate> certList = new ArrayList<Certificate>();
			certList.add(cerx509);
			Store certs = new JcaCertStore(certList);

			CMSTypedData msg = (CMSTypedData) new CMSProcessableByteArray(srcMsg.getBytes(charSet));

			//MD2withRSA MD5withRSA SHA1withRSA SHA224withRSA SHA256withRSA SHA384withRSA SHA512withRSA
			CMSSignedDataGenerator cmsSignedDataGenerator = new CMSSignedDataGenerator();
			ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA512withRSA")
											.build(prikey);

			SignerInfoGenerator signerInfoGenerator = new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().build()).build(sha1Signer, cerx509);
			cmsSignedDataGenerator.addSignerInfoGenerator(signerInfoGenerator);

			cmsSignedDataGenerator.addCertificates(certs);

			CMSSignedData sigData = cmsSignedDataGenerator.generate(msg, true);
			System.out.println("加密耗时：" + (System.currentTimeMillis()-begin));
			return Base64.encode(sigData.getEncoded());

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 验证数字签名
	 * @param signedData
	 * @return
	 */
	public boolean signedDataVerify(byte[] signedData) {
		boolean verifyRet = true;
		try {
			long begin = System.currentTimeMillis();
			// 新建PKCS#7签名数据处理对象
			CMSSignedData sign = new CMSSignedData(signedData);

			// 添加BouncyCastle作为安全提供
			Security.addProvider(new BouncyCastleProvider());

			// 获得证书信息
			Store certs = sign.getCertificates();

			// 获得签名者信息
			SignerInformationStore signers = sign.getSignerInfos();
			Collection signersCollection = signers.getSigners();
			Iterator it = signersCollection.iterator();

			// 当有多个签名者信息时需要全部验证
			while (it.hasNext()) {
				SignerInformation signer = (SignerInformation) it.next();

				// 证书链
				Collection certCollection = certs.getMatches(signer.getSID());
				Iterator certIt = certCollection.iterator();
				X509CertificateHolder cert = (X509CertificateHolder) certIt
						.next();

				// 验证数字签名
				if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder()
						.setProvider("BC").build(cert))) {
					verifyRet = true;
				} else {
					verifyRet = false;
				}
			}
			System.out.println("解密耗时：" + (System.currentTimeMillis()-begin));
		} catch (Exception e) {
			verifyRet = false;
			e.printStackTrace();
			System.out.println("验证数字签名失败");
		}
		return verifyRet;
	}

	/**
	 * 数字信封加密-（des加密数据，rsa加密密码）
	 * @param srcMsg 源信息
	 * @param certPath 证书路径
	 * @param charSet 字符编码
	 * @return
	 * @throws Exception
	 */
	public String envelopeMessage(String srcMsg, String certPath, String charSet) throws Exception {
		System.out.println("加密前：" + srcMsg);
		long begin = System.currentTimeMillis();
		Security.addProvider(new BouncyCastleProvider());
		//添加数字信封
		CMSTypedData msg = new CMSProcessableByteArray(srcMsg.getBytes(charSet));

		CMSEnvelopedDataGenerator edGen = new CMSEnvelopedDataGenerator();

		edGen.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(
				cert).setProvider("BC"));

		CMSEnvelopedData ed = edGen.generate(msg,
				new JceCMSContentEncryptorBuilder(PKCSObjectIdentifiers.rc4)
						.setProvider("BC").build());

		System.out.println("加密耗时：" + (System.currentTimeMillis()-begin));
		String rslt = new String(Base64.encode(ed.getEncoded()));
		System.out.println("加密后：" + rslt);
		return rslt;
	}

	/**
	 * 解密数据
	 * @param encode 加密后的密文
	 * @param certPath 证书路径
	 * @param certPwd 证书密码
	 * @param charSet 字符编码
	 * @return
	 * @throws Exception
	 */
	public String openEnvelope(String encode, String certPath, String certPwd, String charSet) throws Exception {
		//获取密文
		CMSEnvelopedData ed = new CMSEnvelopedData(Base64.decode(encode.getBytes()));

		RecipientInformationStore recipients = ed.getRecipientInfos();

		Collection c = recipients.getRecipients();
		Iterator it = c.iterator();

		// 加载证书
		KeyStore ks = KeyStore.getInstance(ksType);
		ks.load(new FileInputStream(certPath), certPwd.toCharArray());

		String priKeyName = null;
		if (ks.aliases().hasMoreElements()) {
			priKeyName = ks.aliases().nextElement();
		}

		// 获取私钥
		PrivateKey prikey = (PrivateKey) ks.getKey(priKeyName, certPwd.toCharArray());

		byte[] recData = null;
		//解密
		if (it.hasNext()) {
			RecipientInformation recipient = (RecipientInformation) it.next();

			recData = recipient.getContent(new JceKeyTransEnvelopedRecipient(
					prikey).setProvider("BC"));
		}

		return new String(recData, charSet);
	}

	public MessageUtil() {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	public static void main(String[] args) throws Exception {
		MessageUtil messageUtil = new MessageUtil();

		//客户端用公钥加密 传至服务端 私钥解密
		String data = "客户端用公钥加密 传至服务端 私钥解密";
		byte[] sign = messageUtil.signMessage(data, "UTF-8", "/root/sunfei", "123456");
		messageUtil.signedDataVerify(Base64.decode(sign));
		String encryptPassword = messageUtil.envelopeMessage(data, "/root/sunfei.crt", "UTF-8");
		String decryptPassword = messageUtil.openEnvelope(encryptPassword, "/root/sunfei", "123456", "UTF-8");
		System.out.println("解密后：" + decryptPassword);

	}

}