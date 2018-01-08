package com.wang.registry.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author wangju
 *
 */
public class RegistryCenterUtil {
	private static final Logger LOGGER = Logger.getLogger(RegistryCenterUtil.class);

	public static Properties readProperties(String fileName) throws IOException {
		InputStream in = null;
		try {
			String classPath = Thread.currentThread().getContextClassLoader().getResource("").toString().substring(6)
					.replace("/", File.separator);
			String filePath = classPath + File.separator + fileName;
			Properties prop = new Properties();
			in = new BufferedInputStream(new FileInputStream(filePath));
			prop.load(in);
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
