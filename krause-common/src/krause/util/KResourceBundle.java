package krause.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import krause.util.ras.logging.ErrorLogHelper;

public class KResourceBundle {

	private ResourceBundle innerBundle = null;
	private String bundleName = null;

	public KResourceBundle(String bundleName) {
		this.bundleName = bundleName;
	}

	public String getString(String key) {
		String rc;
		if (innerBundle == null) {
			innerBundle = ResourceBundle.getBundle(bundleName);
		}
		try {
			rc = innerBundle.getString(key);
		} catch (MissingResourceException ex1) {
			rc = "?:" + key + ":?";
			ErrorLogHelper.text(innerBundle, "getString", "Ressource [" + key + "] missing");
		}
		return rc;
	}

	public static KResourceBundle getBundle(String bundleName) {
		return new KResourceBundle(bundleName);
	}
}
