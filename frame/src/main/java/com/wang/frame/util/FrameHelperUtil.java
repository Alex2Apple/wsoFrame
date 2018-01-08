package com.wang.frame.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author wangju
 *
 */
public class FrameHelperUtil {
	private static final Logger LOGGER = Logger.getLogger(FrameHelperUtil.class);

	public static String MD5(String text) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] b = md5.digest(text.getBytes());
			StringBuffer md5str = new StringBuffer();
			int digital;
			for (int i = 0; i < b.length; i++) {
				digital = b[i];

				if (digital < 0) {
					digital += 256;
				}
				if (digital < 16) {
					md5str.append("0");
				}
				md5str.append(Integer.toHexString(digital));
			}
			return md5str.toString().toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("message digest error by md5", e);
		}

		return text;
	}

	public static String readProperty(String key, String fileName) {
		String pValue = null;
		try {
			pValue = System.getProperty(key);
		} catch (Exception e) {
			LOGGER.error(String.format("Get system property #%s# error", key), e);
		}
		if (pValue == null) {
			try {
				Properties prop = readProperties(fileName);
				pValue = prop.getProperty(key);
				if (pValue == null) {
					throw new Exception(String.format("Get property #%s# error in #%s#", key, fileName));
				}
			} catch (IOException e) {
				LOGGER.error(String.format("Read properties file #%s# error", fileName), e);
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
		}
		return pValue;
	}

	public static Properties readProperties(String fileName) throws IOException {
		InputStream in = null;
		try {
			String classPath = Thread.currentThread().getContextClassLoader().getResource("").toString().substring(6)
					.replace("/", File.separator);
			String filePath = classPath + File.separator + fileName;
			Properties prop = new Properties();
			in = new BufferedInputStream(new FileInputStream(filePath));
			prop.load(in);

			System.setProperties(prop); // 设置到环境变量

			return prop;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				LOGGER.error("readProperties: close input stream error!", e);
			}
		}
	}

	public static InetAddress getLocalHostLANAddress() throws Exception {
		InetAddress candidateAddress = null;
		for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces
				.hasMoreElements();) {
			NetworkInterface iface = ifaces.nextElement();
			for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
				InetAddress inetAddr = inetAddrs.nextElement();
				if (inetAddr.isLoopbackAddress()) {
					continue;
				}
				if (inetAddr.isSiteLocalAddress()) {
					return inetAddr;
				} else if (candidateAddress == null) {
					candidateAddress = inetAddr;
				}
			}
		}
		if (candidateAddress != null) {
			return candidateAddress;
		}
		InetAddress lastAddress = InetAddress.getLocalHost();
		return lastAddress;
	}

	public static boolean isLocalHost(String host) throws Exception {
		InetAddress local = getLocalHostLANAddress();
		return local.getHostAddress().equals(host);
	}
}
