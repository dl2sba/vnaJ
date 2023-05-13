package krause.vna.library;

import java.io.File;

import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.IVNABackgroundTaskStatusListener;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.device.VNADriverFactory;

public class VNALibraryRunner implements IVNABackgroundTaskStatusListener {
	private IVNADriver driver = null;
	private VNACalibrationBlock mainCalibrationBlock = null;
	private VNAConfig config = VNAConfig.getSingleton();

	/**
	 * Execute a scan and return RAW data
	 * 
	 * @param fStart
	 *            start frequency in Hz. Must match the analyser specs.
	 * @param fStop
	 *            stop frequency in Hz. Must match the analyser specs.
	 * @param numSteps
	 *            Number of steps to scan. Must be &gt; 0
	 * @param scanMode
	 *            For reflection mode use "REFL". For transmission mode use "TRAN"
	 * @return the scan data in case of success
	 * @throws ProcessingException
	 *             in case of error
	 */
	public VNASampleBlock scanRaw(long fStart, long fStop, int numSteps, String scanMode) throws ProcessingException {
		TraceHelper.entry(this, "scan");

		final VNAScanMode lScanMode = VNAScanMode.restoreFromString(scanMode);
		
		// now read one bunch of data from device
		final VNASampleBlock data = this.driver.scan(lScanMode, fStart, fStop, numSteps, this);

		// data present?
		if (data == null) {
			throw new ProcessingException("No data set on jobresult");
		}

		TraceHelper.exit(this, "scan");
		return data;
	}
	
	/**
	 * Execute a scan and returned calibrated scan data
	 * 
	 * @param fStart
	 *            start frequency in Hz. Must match the analyser specs.
	 * @param fStop
	 *            stop frequency in Hz. Must match the analyser specs.
	 * @param numSteps
	 *            Number of steps to scan. Must be &gt; 0
	 * @param scanMode
	 *            For reflection mode use "REFL". For transmission mode use "TRAN"
	 * @return the scan data in case of success
	 * @throws ProcessingException
	 *             in case of error
	 */
	public VNACalibratedSampleBlock scan(long fStart, long fStop, int numSteps, String scanMode) throws ProcessingException {
		TraceHelper.entry(this, "scan");

		final VNAScanMode lScanMode = VNAScanMode.restoreFromString(scanMode);

		final VNACalibrationBlock rcb = VNACalibrationBlockHelper.createResizedCalibrationBlock(this.mainCalibrationBlock, fStart, fStop, numSteps);
		if (rcb == null) {
			throw new ProcessingException("No calibration block set.");
		}

		// now read one bunch of data from device
		final VNASampleBlock data = this.driver.scan(lScanMode, fStart, fStop, numSteps, this);

		// data present?
		if (data == null) {
			throw new ProcessingException("No data set on jobresult");
		}

		final IVNADriverMathHelper mathHelper = data.getMathHelper();
		if (mathHelper == null) {
			throw new ProcessingException("No mathHelper set on data");
		}

		// filter raw data
		mathHelper.applyFilter(data.getSamples());

		// create cal context for this scan
		final VNACalibrationContext context = mathHelper.createCalibrationContextForCalibratedSamples(rcb);
		context.setConversionTemperature(data.getDeviceTemperature());

		// put calibrated data into pool for further processing
		final VNACalibratedSampleBlock calSamples = mathHelper.createCalibratedSamples(context, data);

		TraceHelper.exit(this, "scan");
		return calSamples;
	}

	/**
	 * start the frequency generator
	 * 
	 * @param frequencyI
	 *            for all analysers
	 * @param frequencyQ
	 *            only for miniVNApro
	 * @param attenuationI
	 *            for all analysers
	 * @param attenuationQ
	 *            only for miniVNApro
	 * @param phase
	 *            only for miniVNApro. phase between I and Q
	 * @param mainAttenuation
	 *            only for miniVNApro. General attenuation for I and Q
	 * @throws ProcessingException
	 *             in case of error
	 */
	public void startGenerator(long frequencyQ, long frequencyI, int attenuationI, int attenuationQ, int phase, int mainAttenuation) throws ProcessingException {
		TraceHelper.entry(this, "startGenerator");

		// now turn generator on
		this.driver.startGenerator(frequencyI, frequencyQ, attenuationI, attenuationQ, phase, mainAttenuation);

		TraceHelper.exit(this, "startGenerator");
	}

	/**
	 * Stop the frequency generator
	 * 
	 * @throws ProcessingException
	 *  In case of an error
	 */
	public void stopGenerator() throws ProcessingException {
		TraceHelper.entry(this, "stopGenerator");

		// now turn generator on
		this.driver.stopGenerator();

		TraceHelper.exit(this, "stopGenerator");
	}

	@Override
	public void publishProgress(int percentage) {
		// no used
	}

	public void loadCalibrationFileByName(String fileName) throws ProcessingException {
		TraceHelper.entry(this, "loadCalibrationFileByName");

		final File file = new File(fileName);
		this.mainCalibrationBlock = VNACalibrationBlockHelper.load(file, this.driver, new VNACalibrationKit());

		TraceHelper.exit(this, "loadCalibrationFileByName");
	}

	public void loadDriverByName(String driverName, String portName) throws ProcessingException {
		TraceHelper.entry(this, "loadDriverByName");
		try {
			this.driver = VNADriverFactory.getSingleton().getDriverForShortName(driverName);
			this.config.setPortName(this.driver, portName);
			this.driver.init();
		} catch (InitializationException e) {
			throw new ProcessingException("init driver failed");
		}

		TraceHelper.exit(this, "loadDriverByName");
	}

	public void shutdown() {
		if (this.driver != null) {
			this.driver.destroy();
		}
		this.driver = null;
		this.mainCalibrationBlock = null;
	}
}
