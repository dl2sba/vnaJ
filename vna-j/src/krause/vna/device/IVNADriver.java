/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.device;

import java.util.List;

import krause.common.exception.DialogNotImplementedException;
import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;
import krause.common.validation.ValidationResults;
import krause.vna.background.IVNABackgroundTaskStatusListener;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;

/**
 * This interface must be implemented for every analyser used inside vna/J.
 * 
 * @author Dietmar
 * 
 */
public interface IVNADriver {
	/**
	 * returns the internal representation of the given frequency
	 * 
	 * @param frequency
	 * @return
	 */
	public long calculateInternalFrequencyValue(long frequency);

	/**
	 * Method destroy.
	 */
	public void destroy();

	/**
	 * 
	 * @return a number of scan ranges to be used during calibration
	 */
	public VNACalibrationRange[] getCalibrationRanges();

	/**
	 * 
	 * @return
	 */
	public VNAScanMode getDefaultMode();

	/**
	 * Returns a analyzer specific string containing a version information.
	 * 
	 * @return the firmware id string returned by the analyzer
	 */
	public String getDeviceFirmwareInfo();

	/**
	 * returns the device info block for this special device
	 * 
	 * @return
	 */
	public VNADeviceInfoBlock getDeviceInfoBlock();

	/**
	 * Returns the power supply status of the analyzer.
	 * 
	 * @return the power supply status string returned by the analyzer
	 */
	public Double getDeviceSupply();

	/**
	 * Returns the temperature status of the analyzer.
	 * 
	 * @return the temperature status returned by the analyzer
	 */
	public Double getDeviceTemperature();

	/**
	 * 
	 * @return
	 */
	public String getDriverConfigPrefix();

	/**
	 * 
	 */
	public IVNADriverMathHelper getMathHelper();

	/**
	 * Provide a string list of all available ports suitable the specific driver subclass. The selected port is then stored in VNAConfig object and read by the specific driver in init() method.
	 * 
	 * @return List of all suitable ports
	 * 
	 */
	public List<String> getPortList() throws ProcessingException;

	/**
	 * @return the portname
	 */
	public String getPortname();

	/**
	 * get a device specific preset of the calibration ranges.
	 * 
	 * @return
	 */
	public VNACalibrationRange[] getSpecificCalibrationRanges();

	/**
	 * Sets up the basic properties of the driver. Open the defined port in properties of the driver. Measurement related properties (start/stop frequency etc.) are not set in this method.
	 * 
	 * @throws InitializationException
	 */
	public void init() throws InitializationException;

	/**
	 * Checks, whether the scan range, scan mode and number of samples is supported by the analyser.
	 * 
	 * @param numSamples
	 * @param range
	 * @param mode
	 * @return true if supported
	 */
	public boolean isScanSupported(int numSamples, VNAFrequencyRange range, VNAScanMode mode);

	/**
	 * @param transMissionMode
	 * @param frequency
	 *            in Hz
	 * @param samples
	 * @param steps
	 *            in Hz
	 * @throws ProcessingException
	 */
	public VNASampleBlock scan(VNAScanMode scanMode, long frequencyLow, long frequencyHigh, int samples, IVNABackgroundTaskStatusListener listener) throws ProcessingException;

	/**
	 * @param portname
	 *            the portname to set
	 */
	public void setPortname(String portname);

	/**
	 * shows a dialog, that display internal information about this specific driver.
	 * 
	 * @param pMF
	 */
	public void showDriverDialog(VNAMainFrame pMF);

	/**
	 * shows a dialog, that display internal information about this specific driver.
	 * 
	 * @param pMF
	 */
	public void showDriverNetworkDialog(VNAMainFrame pMF);

	/**
	 * shows the generator dialog driver.
	 * 
	 * @param pMF
	 */
	public void showGeneratorDialog(VNAMainFrame pMF) throws DialogNotImplementedException;

	/**
	 * Use the VNA as a signal generator
	 * 
	 * @param frequencyI
	 * @param frequencyQ
	 * @param attenuationI
	 * @param attenuationQ
	 * @param phase
	 * @param mainAttenuation
	 * @throws ProcessingException
	 */
	public void startGenerator(long frequencyI, long frequencyQ, int attenuationI, int attenuationQ, int phase, int mainAttenuation) throws ProcessingException;

	/**
	 * Stop the generator. Must be called after startGenerator
	 * 
	 * @throws ProcessingException
	 *             If something fails
	 */
	public void stopGenerator() throws ProcessingException;

	/**
	 * Validates the given scan range for the specific analyser.
	 * 
	 * @param startFreq
	 * @param stopFreq
	 * @return Validation results containing the found errors
	 */
	public ValidationResults validateScanRange(VNAScanRange range);
	

	/**
	 * Check whether the specific device is present at the selected port
	 * 
	 * @return true if present
	 */
	public abstract boolean checkForDevicePresence(boolean viaSlowConnection);



}