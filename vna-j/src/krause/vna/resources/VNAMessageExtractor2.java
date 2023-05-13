/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: VNAMessageExtractor.java
 *  Part of:   vna-j
 */

package krause.vna.resources;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import krause.util.SortedProperties;

/**
 * @author Dietmar
 * 
 */
public class VNAMessageExtractor2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		extractMessagesForLocale(new Locale("cs", "CZ"));
		extractMessagesForLocale(new Locale("de", "DE"));
		extractMessagesForLocale(new Locale("es", "ES"));
		extractMessagesForLocale(new Locale("fr", "FR"));
		extractMessagesForLocale(new Locale("hu", "HU"));
		extractMessagesForLocale(new Locale("it", "IT"));
		extractMessagesForLocale(new Locale("ja", "JP"));
		extractMessagesForLocale(new Locale("nl", "NL"));
		extractMessagesForLocale(new Locale("pl", "PL"));
		extractMessagesForLocale(new Locale("ru", "RUS"));
		extractMessagesForLocale(new Locale("sv", "SE"));
		extractMessagesForLocale(new Locale("en", "US"));
	}

	public static void extractMessagesForLocale(Locale locale) {
		SortedProperties localeProps = new SortedProperties();

		Locale.setDefault(locale);

		ResourceBundle bundle = ResourceBundle.getBundle(VNAMessages.BUNDLE_NAME);

		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String val = bundle.getString(key);

			if (key.endsWith(".Command")) {
			} else if (key.endsWith(".Image")) {
			} else if (key.endsWith(".Key")) {
			} else if (key.endsWith(".URL")) {
			} else if (key.endsWith(".copyright")) {
			} else if (key.endsWith(".date")) {
			} else if (key.endsWith(".version")) {
			} else {
				localeProps.put(key, val);
			}
		}

		String filename = "temp/VNAMessage_" + locale.getLanguage().toLowerCase() + ".properties";
		OutputStream os;
		try {
			os = new FileOutputStream(filename);
			localeProps.store(os, "Extracted by VNAMessageExtractor2 for " + locale.toString()+"\n\rPlease use only\n\r  http://propedit.sourceforge.jp/propertieseditor.jnlp\n\rto edit this file!!!");

			System.out.println("File [" + filename + "] saved");
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
