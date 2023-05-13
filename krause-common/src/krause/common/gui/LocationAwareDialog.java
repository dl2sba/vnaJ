package krause.common.gui;

import java.awt.Frame;

import javax.swing.JDialog;

import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;

public class LocationAwareDialog extends JDialog {
	private String configurationPrefix = null;
	private TypedProperties properties = null;

	public LocationAwareDialog(Frame owner, String string) {
		super(owner, string);
		TraceHelper.exit(this, "LocationAwareDialog");
		TraceHelper.exit(this, "LocationAwareDialog");
	}

	@Override
	public void dispose() {
		TraceHelper.entry(this, "dispose");
		if (getConfigurationPrefix() != null && getProperties() != null) {
			getProperties().storeWindowPosition(getConfigurationPrefix(), this);
			getProperties().storeWindowSize(getConfigurationPrefix(), this);
		}
		super.dispose();
		TraceHelper.exit(this, "dispose");
	}

	/**
	 * @return the configurationPrefix
	 */
	public String getConfigurationPrefix() {
		return this.configurationPrefix;
	}

	/**
	 * @param configurationPrefix
	 *            the configurationPrefix to set
	 */
	public void setConfigurationPrefix(String configurationPrefix) {
		this.configurationPrefix = configurationPrefix;
	}

	/**
	 * @return the properties
	 */
	public TypedProperties getProperties() {
		return this.properties;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(TypedProperties properties) {
		this.properties = properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Window#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean b) {
		TraceHelper.entry(this, "setVisible");

		super.setVisible(b);

		if (getConfigurationPrefix() != null && getProperties() != null) {
			if (b) {
				getProperties().restoreWindowPosition(getConfigurationPrefix(), this, getLocation());
				getProperties().restoreWindowSize(getConfigurationPrefix(), this, getSize());
			} else {
				getProperties().storeWindowPosition(getConfigurationPrefix(), this);
				getProperties().storeWindowSize(getConfigurationPrefix(), this);
			}
		}
		TraceHelper.exit(this, "setVisible");
	}
}
