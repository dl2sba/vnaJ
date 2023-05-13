/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.data.calibrated;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNAScanModeParameter;

public class VNACalibrationBlock implements Serializable {

	// old freq based on int
	// private static final long serialVersionUID = -2310064516699996630L;
	final public static String CALIBRATION_FILETYPE_2 = "__V2";

	// new freq based on long
	// private static final long serialVersionUID = -2310064516699999999L;
	final public static String CALIBRATION_FILETYPE_3 = "__V3";

	// new freq based on long and comment field
	// private static final long serialVersionUID = -2310064516699999988L;
	final public static String CALIBRATION_FILETYPE_4 = "__V4";

	// new temperature during calibration
	private static final long serialVersionUID = -2310064516699988988L;
	public final static String CALIBRATION_FILETYPE_5 = "__V5";

	private String analyserType = VNASampleBlock.ANALYSER_TYPE_UNKNOWN;
	private VNASampleBlock calibrationData4Load = null;
	private VNASampleBlock calibrationData4Loop = null;
	private VNASampleBlock calibrationData4Open = null;
	private VNASampleBlock calibrationData4Short = null;
	private transient VNACalibrationPoint[] calibrationPoints = null;
	private String comment = null;
	private transient File file = null;
	private transient IVNADriverMathHelper mathHelper = null;
	private int numberOfOverscans = 1;
	private int numberOfSteps = 100;

	private VNAScanMode scanMode = VNAScanMode.MODE_TRANSMISSION;
	private long startFrequency = 1000000;
	private long stopFrequency = 100000000;
	private transient Double temperature = null;

	public VNACalibrationBlock() {
	}

	/**
	 * Initialize this calibration block using the data from the goiven sample block
	 * 
	 * @param block
	 */
	public VNACalibrationBlock(VNASampleBlock block) {
		setAnalyserType(block.getAnalyserType());
		setMathHelper(block.getMathHelper());
		setNumberOfSteps(block.getNumberOfSteps());
		setStartFrequency(block.getStartFrequency());
		setStopFrequency(block.getStopFrequency());
		setScanMode(block.getScanMode());
		setNumberOfOverscans(block.getNumberOfOverscans());
	}

	/**
	 * 
	 * @param pType
	 * @param pNoS
	 * @param pStart
	 * @param pStop
	 * @param pMode
	 * @return
	 */
	public boolean blockMatches(String pType, int pNoS, long pStart, long pStop, VNAScanMode pMode) {
		boolean rc = true;
		rc &= (getAnalyserType().equals(pType));
		rc &= (getStartFrequency() == pStart);
		rc &= (getStopFrequency() == pStop);
		rc &= (getScanMode().equals(pMode));
		return rc;
	}

	/**
	 * Check, if the calibration block is usable for this driver/dib
	 * 
	 * @param dib
	 * @return
	 */
	public boolean blockMatches(VNADeviceInfoBlock dib) {

		boolean rc = true;
		rc &= (getAnalyserType().equals(dib.getType()));
		rc &= (getStartFrequency() == dib.getMinFrequency());
		rc &= (getStopFrequency() == dib.getMaxFrequency());
		return rc;
	}

	/**
	 * Checks if this calibration block is usable for this driver/dib and mode
	 * 
	 * @param dib
	 * @param pScanMode
	 * @return
	 */
	public boolean blockMatches(VNADeviceInfoBlock dib, VNAScanMode pScanMode) {
		return blockMatches(dib.getType(), dib.getNumberOfSamples4Calibration(), dib.getMinFrequency(), dib.getMaxFrequency(), pScanMode);
	}

	/**
	 * 
	 * @return
	 */
	public void calculateCalibrationTemperature() {
		final String methodName = "calculateCalibrationTemperature";
		TraceHelper.entry(this, methodName);

		// determine average calibration temp
		int i = 0;
		double temp = 0;
		if ((getCalibrationData4Load() != null) && (getCalibrationData4Load().getDeviceTemperature() != null)) {
			++i;
			temp += getCalibrationData4Load().getDeviceTemperature();
		}
		if ((getCalibrationData4Loop() != null) && (getCalibrationData4Loop().getDeviceTemperature() != null)) {
			++i;
			temp += getCalibrationData4Loop().getDeviceTemperature();
		}
		if ((getCalibrationData4Open() != null) && (getCalibrationData4Open().getDeviceTemperature() != null)) {
			++i;
			temp += getCalibrationData4Open().getDeviceTemperature();
		}
		if ((getCalibrationData4Short() != null) && (getCalibrationData4Short().getDeviceTemperature() != null)) {
			++i;
			temp += getCalibrationData4Short().getDeviceTemperature();
		}

		if (i > 0) {
			temp /= i;
			TraceHelper.exitWithRC(this, methodName, temp);
			temperature = Double.valueOf(temp);
		} else {
			TraceHelper.exit(this, methodName);
			temperature = null;
		}
	}

	/**
	 * @return the analyserType
	 */
	public String getAnalyserType() {
		return analyserType;
	}

	/**
	 * @return the calibrationData4Load
	 */
	public VNASampleBlock getCalibrationData4Load() {
		return calibrationData4Load;
	}

	public VNASampleBlock getCalibrationData4Loop() {
		return calibrationData4Loop;
	}

	/**
	 * @return the calibrationData4Open
	 */
	public VNASampleBlock getCalibrationData4Open() {
		return calibrationData4Open;
	}

	/**
	 * @return the calibrationData4Short
	 */
	public VNASampleBlock getCalibrationData4Short() {
		return calibrationData4Short;
	}

	/**
	 * @return the calibratedSamples
	 */
	public VNACalibrationPoint[] getCalibrationPoints() {
		return calibrationPoints;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @return the mathHelper
	 */
	public IVNADriverMathHelper getMathHelper() {
		return mathHelper;
	}

	public int getNumberOfOverscans() {
		return numberOfOverscans;
	}

	/**
	 * @return the numberOfSteps
	 */
	public int getNumberOfSteps() {
		return numberOfSteps;
	}

	/**
	 * @return the scanMode
	 */
	public VNAScanMode getScanMode() {
		return scanMode;
	}

	public long getStartFrequency() {
		return startFrequency;
	}

	public long getStopFrequency() {
		return stopFrequency;
	}

	public Double getTemperature() {
		return temperature;
	}

	/**
	 * Checks, whether this calibration lock satisfies the requirements of the given driver
	 * 
	 * @param dib
	 *            describing driver requirement
	 * @return true - satisfies the requirements
	 */
	public boolean satisfiedDeviceInfoBlock(VNADeviceInfoBlock dib) {
		TraceHelper.entry(this, "satisfiedDeviceInfoBlock");

		boolean rc = true;

		VNAScanModeParameter smr = dib.getScanModeParameterForMode(getScanMode());
		if (smr != null) {
			rc &= smr.isRequiresOpen() ? (getCalibrationData4Open() != null) : true;
			rc &= smr.isRequiresShort() ? (getCalibrationData4Short() != null) : true;
			rc &= smr.isRequiresLoad() ? (getCalibrationData4Load() != null) : true;
			rc &= smr.isRequiresLoop() ? (getCalibrationData4Loop() != null) : true;
		}
		TraceHelper.exitWithRC(this, "satisfiedDeviceInfoBlock", rc);
		return rc;
	}

	/**
	 * @param analyserType
	 *            the analyserType to set
	 */
	public void setAnalyserType(String analyserType) {
		this.analyserType = analyserType;
	}

	/**
	 * @param calibrationData4Load
	 *            the calibrationData4Load to set
	 */
	public void setCalibrationData4Load(VNASampleBlock calibrationData4Load) {
		this.calibrationData4Load = calibrationData4Load;
	}

	public void setCalibrationData4Loop(VNASampleBlock calibrationData4Loop) {
		this.calibrationData4Loop = calibrationData4Loop;
	}

	/**
	 * @param calibrationData4Open
	 *            the calibrationData4Open to set
	 */
	public void setCalibrationData4Open(VNASampleBlock calibrationData4Open) {
		this.calibrationData4Open = calibrationData4Open;
	}

	/**
	 * @param calibrationData4Short
	 *            the calibrationData4Short to set
	 */
	public void setCalibrationData4Short(VNASampleBlock calibrationData4Short) {
		this.calibrationData4Short = calibrationData4Short;
	}

	/**
	 * @param calibratedSamples
	 *            the calibratedSamples to set
	 */
	public void setCalibrationPoints(VNACalibrationPoint[] calibratedSamples) {
		this.calibrationPoints = calibratedSamples;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @param mathHelper
	 *            the mathHelper to set
	 */
	public void setMathHelper(IVNADriverMathHelper mathHelper) {
		this.mathHelper = mathHelper;
	}

	public void setNumberOfOverscans(int numberOfOverscans) {
		this.numberOfOverscans = numberOfOverscans;
	}

	/**
	 * @param numberOfSteps
	 *            the numberOfSteps to set
	 */
	public void setNumberOfSteps(int numberOfSteps) {
		this.numberOfSteps = numberOfSteps;
	}

	/**
	 * @param scanMode
	 *            the scanMode to set
	 */
	public void setScanMode(VNAScanMode scanMode) {
		this.scanMode = scanMode;
	}

	public void setStartFrequency(long startFrequency) {
		this.startFrequency = startFrequency;
	}

	public void setStopFrequency(long stopFrequency) {
		this.stopFrequency = stopFrequency;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VNACalibrationBlock [analyserType=" + analyserType + ", calibrationData4Load=" + calibrationData4Load + ", calibrationData4Loop=" + calibrationData4Loop + ", calibrationData4Open=" + calibrationData4Open + ", calibrationData4Short=" + calibrationData4Short + ", calibrationPoints=" + Arrays.toString(calibrationPoints) + ", numberOfOverscans=" + numberOfOverscans + ", numberOfSteps=" + numberOfSteps + ", scanMode=" + scanMode + ", startFrequency=" + startFrequency + ", stopFrequency=" + stopFrequency + "]";
	}
}
