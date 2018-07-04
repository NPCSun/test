package com.sun.security;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Properties;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;


public class ServerAuth {
	private static SSLContext sslContext;

	public static SSLContext getSSLContext() throws Exception {
		//TODO
		Properties properties = new Properties();
		String protocol = properties.getProperty("protocol");
		String serverCer = properties.getProperty("serverCer");
		String serverCerPwd = properties.getProperty("serverCerPwd");
		String serverKeyPwd = properties.getProperty("serverKeyPwd");

		//Key Stroe
		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(new FileInputStream(serverCer),
				serverCerPwd.toCharArray());

		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyManagerFactory.init(keyStore, serverKeyPwd.toCharArray());
		KeyManager[] kms = keyManagerFactory.getKeyManagers();

		TrustManager[] tms = null;
		if (properties.getProperty("authority").equals("2")) {
			String serverTrustCer = properties.getProperty("serverTrustCer");
			String serverTrustCerPwd = properties.getProperty("serverTrustCerPwd");

			//Trust Key Store
			keyStore = KeyStore.getInstance("JKS");
			keyStore.load(new FileInputStream(serverTrustCer),
					serverTrustCerPwd.toCharArray());

			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
			trustManagerFactory.init(keyStore);
			tms = trustManagerFactory.getTrustManagers();
		}
		sslContext = SSLContext.getInstance(protocol);
		sslContext.init(kms, tms, null);

		return sslContext;
	}
}

