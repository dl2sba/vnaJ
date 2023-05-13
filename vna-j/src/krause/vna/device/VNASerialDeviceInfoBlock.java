/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 *
 *	This file: VNASerialDeviceInfoBlock.java
 *  Part of:   vna-j
 */

package krause.vna.device;

import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;

/**
 * @author Dietmar
 * 
 */
public abstract class VNASerialDeviceInfoBlock extends VNADeviceInfoBlock {
	public static final  String PROPERTIES_OPEN_TIMEOUT = "openTimeout";
	public static final int DEFAULT_OPEN_TIMEOUT = 5000;

	public static final String PROPERTIES_READ_TIMEOUT = "readTimeout";
	public static final int DEFAULT_READ_TIMEOUT = 20000;

	public static final String PROPERTIES_AFTER_COMMAND_DELAY = "afterCommandDelay";
	public static final int DEFAULT_AFTER_COMMAND_DELAY = 50;

	public static final String PROPERTIES_BAUDRATE = "baudRate";
	public static final int DEFAULT_PROPERTIES_BAUDRATE = 115200;

	private int openTimeout;
	private int readTimeout;
	private int afterCommandDelay;
	private int baudrate;

	@Override
	public void reset() {
		super.reset();
		setOpenTimeout(DEFAULT_OPEN_TIMEOUT);
		setReadTimeout(DEFAULT_READ_TIMEOUT);
		setAfterCommandDelay(DEFAULT_AFTER_COMMAND_DELAY);
		setBaudrate(DEFAULT_PROPERTIES_BAUDRATE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * krause.vna.device.VNADeviceInfoBlock#restore(krause.vna.config.VNAConfig,
	 * java.lang.String)
	 */
	@Override
	public void restore(TypedProperties config, String prefix) {
		TraceHelper.entry(this, "restore");
		// get the parent class values
		super.restore(config, prefix);

		// now read new values and you the default values set in reset() call
		setAfterCommandDelay(config.getInteger(prefix + PROPERTIES_AFTER_COMMAND_DELAY, getAfterCommandDelay()));
		setOpenTimeout(config.getInteger(prefix + PROPERTIES_OPEN_TIMEOUT, getOpenTimeout()));
		setReadTimeout(config.getInteger(prefix + PROPERTIES_READ_TIMEOUT, getReadTimeout()));
		setBaudrate(config.getInteger(prefix + PROPERTIES_BAUDRATE, getBaudrate()));

		TraceHelper.exit(this, "restore");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * krause.vna.device.VNADeviceInfoBlock#store(krause.vna.config.VNAConfig,
	 * java.lang.String)
	 */
	@Override
	public void store(TypedProperties config, String prefix) {
		TraceHelper.entry(this, "store");
		super.store(config, prefix);

		config.putInteger(prefix + PROPERTIES_AFTER_COMMAND_DELAY, getAfterCommandDelay());
		config.putInteger(prefix + PROPERTIES_OPEN_TIMEOUT, getOpenTimeout());
		config.putInteger(prefix + PROPERTIES_READ_TIMEOUT, getReadTimeout());
		config.putInteger(prefix + PROPERTIES_BAUDRATE, getBaudrate());
		TraceHelper.exit(this, "store");
	}

	public int getOpenTimeout() {
		return openTimeout;
	}

	public void setOpenTimeout(int openTimeout) {
		this.openTimeout = openTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public int getAfterCommandDelay() {
		return afterCommandDelay;
	}

	public void setAfterCommandDelay(int afterCommandDelay) {
		this.afterCommandDelay = afterCommandDelay;
	}

	public void setBaudrate(int baudrate) {
		this.baudrate = baudrate;
	}

	public int getBaudrate() {
		return baudrate;
	}
}
