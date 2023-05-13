package krause.vna.device.serial.std.lf;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import krause.util.ras.logging.ErrorLogHelper;

public class VNADriverSerialStdLfMessages {
	private static ResourceBundle localeBundle = null;
	public static String BUNDLE_NAME = "krause.vna.device.serial.std.lf.driver_serial_std_lf";

	public static ResourceBundle getBundle() {
		return localeBundle;
	}

	public static String getString(String key) {
		String rc;
		if (localeBundle == null) {
			localeBundle = ResourceBundle.getBundle(BUNDLE_NAME);
		}
		try {
			rc = localeBundle.getString(key);
		} catch (MissingResourceException ex1) {
			rc = key;
			ErrorLogHelper.text(localeBundle, "getString", "Ressource [" + key + "] missing");
		}
		return rc;
	}

	public static String getBUNDLE_NAME() {
		return BUNDLE_NAME;
	}
}
