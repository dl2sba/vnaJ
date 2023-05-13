/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.config;

import java.util.Properties;

import krause.util.PropertiesHelper;

public class VNASystemConfig {
	public final static String VNA_PREFIX = "VNA.";

	public enum OS_PLATFORM {
		ALL, WINDOWS, MAC, UNIX
	};

	public VNASystemConfig() {

	}

	private static Properties systemProperties = PropertiesHelper.load("system.properties");

	public static String getVNA_HOME_DIR() {
		return System.getProperty("user.home") + System.getProperty("file.separator") + systemProperties.getProperty("VNA_HOME_DIR");
	}

	public static String getVNA_UPDATEURL() {
		return systemProperties.getProperty("VNA_UPDATEURL");
	}

	public static OS_PLATFORM getPlatform() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.startsWith("mac os x")) {
			return OS_PLATFORM.MAC;
		} else if (os.startsWith("windows")) {
			return OS_PLATFORM.WINDOWS;
		} else {
			return OS_PLATFORM.ALL;
		}
	}
}
