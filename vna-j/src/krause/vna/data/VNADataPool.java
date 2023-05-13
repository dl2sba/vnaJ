/**
 * Copyright (C) 2020 Dietmar Krause, DL2SBA
 */
package krause.vna.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrationkit.VNACalSetHelper;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.data.reference.VNAReferenceDataBlock;
import krause.vna.device.IVNADriver;

/**
 * This is the general data object the the VNA application. It is handled with the singleton pattern.
 * 
 * @author Dietmar Krause
 * 
 */
public class VNADataPool {
	public static final String KEY_DEVICETYPE = "deviceType";
	public static final String KEY_FREQUENCYRANGE = "frequencyRange";
	public static final String KEY_MAIN_CALBLK = "mainCalibrationBlock";
	public static final String KEY_MAIN_REFLECTION_CALBLK = "mainCalibrationBlockReflection";
	public static final String KEY_RESIZED_CALBLK = "resizedCalibrationBlock";
	public static final String KEY_SCANMODE = "scanMode";
	public static final String PROPERTIES_PREFIX = "VNADeviceConfig.";
	public static final String PROPERTIES_START_FREQUENCY = PROPERTIES_PREFIX + "StartFrequency";
	public static final String PROPERTIES_STOP_FREQUENCY = PROPERTIES_PREFIX + "StopFrequency";
	public static final String PROPERTIES_TRANSMISSION_MODE = PROPERTIES_PREFIX + "TransmissionMode";

	private static VNADataPool singleton = null;

	/**
	 * Return the one and only instance of the config object
	 * 
	 * @return the only instance of this class
	 */
	public static synchronized VNADataPool getSingleton() {
		if (singleton == null) {
			singleton = new VNADataPool();
		}
		return singleton;
	}

	/**
	 * Return the one and only instance of the config object
	 * 
	 * @return the only instance of this class
	 */
	public static synchronized VNADataPool init(VNAConfig pConfig) {
		if (singleton == null) {
			singleton = new VNADataPool();
			singleton.load(pConfig);
		}
		return singleton;
	}

	private VNACalibratedSampleBlock calibratedData = null;

	private String deviceType = null;
	private IVNADriver driver = null;
	private VNAFrequencyRange frequencyRange = null;
	private VNACalibrationBlock mainCalibrationBlock = null;
	private Map<String, VNACalibrationBlock> mainCalibrationBlocks = new HashMap<>();
	private VNASampleBlock rawData = null;

	private List<VNASampleBlock> rawDataBlocks = new ArrayList<>();
	private VNAReferenceDataBlock referenceData = null;
	private VNACalibrationBlock resizedCalibrationBlock = null;
	private VNAScanMode scanMode = null;
	private VNACalibrationKit currentCalSet = null;

	/**
	 * pulled down to inhibit instantiation outside the singleton pattern
	 */
	protected VNADataPool() {
		super();
	}

	public void clearCalibratedData() {
		this.calibratedData = null;
	}

	/**
	 * 
	 */
	public void clearCalibrationBlocks() {
		TraceHelper.entry(this, "clearCalibrationBlocks");
		if (mainCalibrationBlock != null) {
			mainCalibrationBlock = null;
		}
		if (mainCalibrationBlocks != null) {
			mainCalibrationBlocks.clear();
		}
		TraceHelper.exit(this, "clearCalibrationBlocks");

	}

	/**
	 * 
	 */
	public void clearResizedCalibrationBlock() {
		TraceHelper.entry(this, "clearResizedCalibrationBlock");
		this.resizedCalibrationBlock = null;
		TraceHelper.exit(this, "clearResizedCalibrationBlock");
	}

	public VNACalibratedSampleBlock getCalibratedData() {
		return calibratedData;
	}

	public String getDeviceType() {
		return deviceType;
	}

	/**
	 * @return the driver
	 */
	public IVNADriver getDriver() {
		return driver;
	}

	public VNAFrequencyRange getFrequencyRange() {
		return frequencyRange;
	}

	/**
	 * @return the mainCalibrationBlock
	 */
	public VNACalibrationBlock getMainCalibrationBlock() {
		return mainCalibrationBlock;
	}

	/**
	 * @return the mainCalibrationBlockReflection
	 */
	public VNACalibrationBlock getMainCalibrationBlockForMode(VNAScanMode mode) {
		return mainCalibrationBlocks.get(mode.key());
	}

	public Map<String, VNACalibrationBlock> getMainCalibrationBlocks() {
		return mainCalibrationBlocks;
	}

	/**
	 * get the current raw data for the last scan. This data is calculated from the last raw blocks.
	 * 
	 * @return
	 */
	public VNASampleBlock getRawData() {
		return rawData;
	}

	/**
	 * get the last scanned raw blocks
	 * 
	 * @return
	 */
	public List<VNASampleBlock> getRawDataBlocks() {
		return rawDataBlocks;
	}

	public VNAReferenceDataBlock getReferenceData() {
		return referenceData;
	}

	/**
	 * @return the resizedCalibrationBlock
	 */
	public VNACalibrationBlock getResizedCalibrationBlock() {
		return resizedCalibrationBlock;
	}

	/**
	 * @return the mode
	 */
	public VNAScanMode getScanMode() {
		return scanMode;
	}

	/**
	 * load the datapool from the config object
	 * 
	 * @param pConfig
	 */
	private void load(VNAConfig pConfig) {
		TraceHelper.entry(this, "load");
		this.frequencyRange = new VNAFrequencyRange(pConfig.getLong(PROPERTIES_START_FREQUENCY, 1000000), pConfig.getLong(PROPERTIES_STOP_FREQUENCY, 180000000));

		int sm = pConfig.getInteger(PROPERTIES_TRANSMISSION_MODE, VNAScanMode.MODENUM_UNKNOWN);
		if (sm != VNAScanMode.MODENUM_UNKNOWN) {
			scanMode = new VNAScanMode(sm);
		}
		this.deviceType = pConfig.getVNADriverType();

		String csn = pConfig.getCurrentCalSetID();
		// set default calset
		this.currentCalSet = new VNACalibrationKit();

		// search for selected calset
		for (VNACalibrationKit aCalSet : new VNACalSetHelper().load(pConfig.getCalibrationKitFilename())) {
			if (aCalSet.getId().equals(csn)) {
				TraceHelper.text(this, "load", "Using calibration set [" + aCalSet.getName() + "]");

				this.currentCalSet = aCalSet;
				break;
			}
		}

		TraceHelper.exit(this, "load");
	}

	/**
	 * save the datapool to the config object
	 * 
	 * @param pConfig
	 */
	public void save(VNAConfig pConfig) {
		TraceHelper.entry(this, "save");
		pConfig.putLong(PROPERTIES_START_FREQUENCY, this.frequencyRange.getStart());
		pConfig.putLong(PROPERTIES_STOP_FREQUENCY, this.frequencyRange.getStop());
		if (scanMode != null) {
			pConfig.putInteger(PROPERTIES_TRANSMISSION_MODE, scanMode.getMode());
		}
		pConfig.setVNADriverType(deviceType);
		TraceHelper.exit(this, "save");
	}

	public void setCalibratedData(VNACalibratedSampleBlock calibratedData) {
		this.calibratedData = calibratedData;
	}

	/**
	 * 
	 * @param newDeviceType
	 */
	public void setDeviceType(String newDeviceType) {
		deviceType = newDeviceType;
	}

	/**
	 * @param driver
	 *            the driver to set
	 */
	public void setDriver(IVNADriver driver) {
		this.driver = driver;
	}

	/**
	 * 
	 * @param start
	 * @param stop
	 */
	public void setFrequencyRange(long start, long stop) {
		setFrequencyRange(new VNAFrequencyRange(start, stop));
	}

	/**
	 * 
	 * @param pNewFrequencyRange
	 */
	public void setFrequencyRange(VNAFrequencyRange pNewFrequencyRange) {
		frequencyRange = pNewFrequencyRange;
	}

	/**
	 * @param newMainCalibrationBlock
	 *            the mainCalibrationBlock to set
	 */
	public void setMainCalibrationBlock(VNACalibrationBlock newMainCalibrationBlock) {
		TraceHelper.entry(this, "setMainCalibrationBlock");
		this.mainCalibrationBlock = newMainCalibrationBlock;
		TraceHelper.exit(this, "setMainCalibrationBlock");
	}

	/**
	 * @param newMainCalibrationBlockReflection
	 *            the mainCalibrationBlockReflection to set
	 */
	public void setMainCalibrationBlockForMode(final VNACalibrationBlock mcb) {
		TraceHelper.entry(this, "setMainCalibrationBlockForMode", "" + mcb.getScanMode().key());
		mainCalibrationBlocks.put(mcb.getScanMode().key(), mcb);
		TraceHelper.exit(this, "setMainCalibrationBlockForMode");
	}

	public void setRawData(final VNASampleBlock rawData) {
		this.rawData = rawData;
	}

	public void setRawDataBlocks(final List<VNASampleBlock> rawDataBlocks) {
		this.rawDataBlocks = rawDataBlocks;
	}

	public void setReferenceData(final VNAReferenceDataBlock referenceData) {
		this.referenceData = referenceData;
	}

	/**
	 * @param resizedCalibrationBlock
	 *            the resizedCalibrationBlock to set
	 */
	public void setResizedCalibrationBlock(final VNACalibrationBlock newResizedCalibrationBlock) {
		this.resizedCalibrationBlock = newResizedCalibrationBlock;
	}

	/**
	 * 
	 * @param newMode
	 */
	public void setScanMode(VNAScanMode newMode) {
		scanMode = newMode;
	}

	public VNACalibrationKit getCalibrationKit() {
		return currentCalSet;
	}

	public void setCalibrationKit(VNACalibrationKit calSet) {
		this.currentCalSet = calSet;
	}
}
