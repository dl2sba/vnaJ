package krause.vna.device.serial.vnarduino;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import krause.util.ras.logging.ErrorLogHelper;

public class VNADriverSerialVNArduinoMessages {
	private static ResourceBundle localeBundle = null;
	public static final String BUNDLE_NAME = "krause.vna.device.serial.vnarduino.driver_serial_vnarduino";

	private VNADriverSerialVNArduinoMessages() {
	}

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
}
