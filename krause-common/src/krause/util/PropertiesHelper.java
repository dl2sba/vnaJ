/**
 * Copyright (C) 2014 Dietmar Krause, DL2SBA
 * 
 */
package krause.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import krause.common.exception.PropertyNotFoundException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;

public class PropertiesHelper {

	private PropertiesHelper() {
		final String methodName = "PropertiesHelper";
		TraceHelper.entry(this, methodName);
		TraceHelper.exit(this, methodName);
	}

	/**
	 * Returns the Properties read from the file with the given file name and file extension.
	 * 
	 * @param fullFileName
	 *            the name of the properties file
	 * 
	 * @return the generated Properties with the properties read from properties file
	 */
	public static Properties load(String fullFileName) {
		try {
			Properties properties = new Properties();
			InputStream in = PropertiesLoader.getResourceAsStream(fullFileName);
			try {
				properties.load(in);
			} finally {
				in.close();
			}
			return properties;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Returns a newly created Properties instance using all key/value pairs of the given Properties starting with the given key.
	 * 
	 * @param props
	 *            the original properties
	 * @param key
	 *            the string which all property keys has to start with to copy the according property to the new instance
	 * 
	 * @return the created poperties instance
	 */
	public static Properties createProperties(Properties props, String key) {
		return createProperties(props, key, false);
	}

	/**
	 * Returns a newly created Properties instance using all key/value pairs of the given Properties starting with the given key.
	 * <p>
	 * Chops off the given key in the keys of the new Properties instance if requested.
	 * 
	 * @param props
	 *            the original properties
	 * @param key
	 *            the string which all property keys has to start with to copy the according property to the new instance
	 * @param chopOffKey
	 *            if true, of all new property keys the given key will be chopped off
	 * 
	 * @return the created properties instance
	 */
	public static Properties createProperties(Properties props, String key, boolean chopOffKey) {
		Properties newProps = new Properties();
		Enumeration<Object> keys = props.keys();
		String oldKey = null;
		String newKey = null;
		while (keys.hasMoreElements()) {
			oldKey = (String) keys.nextElement();
			if (oldKey.startsWith(key)) {
				newKey = oldKey;
				if (chopOffKey) {
					newKey = oldKey.substring(key.length(), oldKey.length());
				}
				newProps.put(newKey, props.getProperty(oldKey));
			}
		}
		return newProps;
	}

	/**
	 * Returns the property in the given Properties for the given key.
	 * 
	 * @param props
	 *            the Properties to search in
	 * @param key
	 *            the key to search for
	 * 
	 * @return the found Property
	 * 
	 * @exception PropertyNotFoundException
	 *                if the property in the given Properties for the given key can not be found or it is empty
	 */
	public static String getProperty(Properties props, String key) throws PropertyNotFoundException {
		String prop = props.getProperty(key);
		if (prop == null || prop.length() == 0) {
			throw new PropertyNotFoundException(key);
		}
		return prop;
	}

	/**
	 * Load the properties from the given xml file. If the file is not found or not readable, then return the default properties
	 * 
	 * @param fileName
	 * @param defaultProperties
	 * @return
	 */
	public static Properties loadXMLProperties(final String userFileName, final Properties defaultProperties) {
		Properties rc = new Properties();
		try (InputStream is = new FileInputStream(userFileName)) {
			rc.loadFromXML(is);
		} catch (IOException e) {
			rc = defaultProperties;
		}
		return rc;
	}

	/**
	 * Saves the properties in an XML file
	 * 
	 * @param props
	 * @param fileName
	 */
	public static void saveXMLProperties(Properties props, String userFileName) {
		final String methodName = "saveXMLProperties";
		TraceHelper.entry("", methodName, userFileName);
		try (OutputStream os = new FileOutputStream(userFileName)) {
			props.storeToXML(os, new Date().toString());
		} catch (IOException e) {
			ErrorLogHelper.exception("", "saveXMLProperties", e);
		}
		TraceHelper.exit("", methodName);
	}

}
