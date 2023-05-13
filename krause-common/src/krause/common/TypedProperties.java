package krause.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Properties;

public class TypedProperties extends Properties {

	public boolean getBoolean(String key, boolean defValue) {
		boolean rc = defValue;

		String val = getProperty(key);
		if (val != null) {
			try {
				rc = Boolean.parseBoolean(val);
			} catch (NumberFormatException e) {
				;
			}
		}
		return rc;
	}

	public Color getColor(String prefix, Color colDef) {
		return new Color(Integer.parseInt(getProperty(prefix, Integer.toString(colDef.getRGB()))));
	}

	public double getDouble(String key, double defValue) {
		double rc = defValue;

		String val = getProperty(key);
		try {
			rc = Double.parseDouble(val);
		} catch (Exception e) {
			;
		}
		return rc;
	}

	public float getFloat(String key, float defValue) {
		float rc = defValue;

		String val = getProperty(key);
		try {
			rc = Float.parseFloat(val);
		} catch (Exception e) {
			;
		}
		return rc;
	}

	public int getInteger(String key, int defValue) {
		int rc = defValue;

		String val = getProperty(key);
		try {
			rc = Integer.parseInt(val);
		} catch (NumberFormatException e) {
			;
		}
		return rc;
	}

	public Integer getInteger(String key, Integer defValue) {
		Integer rc = defValue;

		String val = getProperty(key);
		try {
			rc = Integer.valueOf(val);
		} catch (NumberFormatException e) {
			;
		}
		return rc;
	}

	public long getLong(String key, long defValue) {
		long rc = defValue;

		String val = getProperty(key);
		try {
			rc = Long.parseLong(val);
		} catch (NumberFormatException e) {
			;
		}
		return rc;
	}

	public Long getInteger(String key, Long defValue) {
		Long rc = defValue;

		String val = getProperty(key);
		try {
			rc = Long.valueOf(val);
		} catch (NumberFormatException e) {
			;
		}
		return rc;
	}

	public void putBoolean(String key, boolean value) {
		setProperty(key, Boolean.toString(value));
	}

	public void putColor(String prefix, Color color) {
		setProperty(prefix, Integer.toString(color.getRGB()));
	}

	public void putDouble(String key, double value) {
		setProperty(key, Double.toString(value));
	}

	public void putInteger(String key, int value) {
		setProperty(key, Integer.toString(value));
	}

	public void putLong(String key, long value) {
		setProperty(key, Long.toString(value));
	}

	public void restoreWindowPosition(String prefix, Component wnd, Point point) {
		int x = getInteger(prefix + ".X", (int) point.getX());
		int y = getInteger(prefix + ".Y", (int) point.getY());

		Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();

		if (x + wnd.getWidth() > sz.width) {
			x = sz.width - wnd.getWidth();
		}
		if (y + wnd.getHeight() > sz.height) {
			y = sz.height - wnd.getHeight();
		}
		wnd.setLocation(x, y);
	}

	public void restoreWindowSize(String prefix, Component wnd, Dimension sz) {
		int w = getInteger(prefix + ".Width", (int) sz.getWidth());
		int h = getInteger(prefix + ".Height", (int) sz.getHeight());
		wnd.setSize(w, h);
		wnd.setPreferredSize(wnd.getSize());
	}

	public void storeWindowPosition(String prefix, Component wnd) {
		putInteger(prefix + ".X", wnd.getX());
		putInteger(prefix + ".Y", wnd.getY());
	}

	public void storeWindowSize(String prefix, Component wnd) {
		putInteger(prefix + ".Width", wnd.getWidth());
		putInteger(prefix + ".Height", wnd.getHeight());
	}
}
