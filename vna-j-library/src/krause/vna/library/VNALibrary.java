package krause.vna.library;

import java.util.List;
import java.util.Locale;

import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.LogManager;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADriverFactory;
import krause.vna.library.config.VNAConfigLibraryDefaultProperties;
import krause.vna.resources.VNAMessages;
import purejavacomm.PureJavaComm;

public class VNALibrary {

	private VNALibraryRunner runner;

	/**
	 * Instantiate the library
	 * 
	 * @throws InitializationException
	 *             In case of error
	 */
	public VNALibrary() throws InitializationException {

		System.out.println("INFO::Java version...........[" + System.getProperty("java.version") + "]");
		System.out.println("INFO::Java runtime.version...[" + System.getProperty("java.runtime.version") + "]");
		System.out.println("INFO::Java vm.version........[" + System.getProperty("java.vm.version") + "]");
		System.out.println("INFO::Java vm.vendor.........[" + System.getProperty("java.vm.vendor") + "]");
		System.out.println("INFO::OS.....................[" + System.getProperty("os.arch") + " " + System.getProperty("os.name") + " " + System.getProperty("os.version") + "]");
		System.out.println("INFO::Country/Language.......[" + Locale.getDefault().getCountry() + "/" + Locale.getDefault().getLanguage() + "/" + Locale.getDefault().getDisplayVariant() + "]");
		System.out.println("INFO::Application version....[" + VNAMessages.getString("Application.version") + "]");
		System.out.println("INFO::            date ......[" + VNAMessages.getString("Application.date") + "]");
		System.out.println("INFO::User ..................[" + System.getProperty("user.name") + "]");
		System.out.println("INFO::User.home .............[" + System.getProperty("user.home") + "]");
		System.out.println("INFO::User.dir ..............[" + System.getProperty("user.home") + "]");
		System.out.println("INFO::Serial library version [" + PureJavaComm.getVersion() + "/" + PureJavaComm.getFork() + "]");

		// force init of config instance
		VNAConfig.init("vna.settings.library.xml", new VNAConfigLibraryDefaultProperties());

		VNAConfig config = VNAConfig.getSingleton();

		System.out.println("INFO::Configuration setup done");

		Locale loc = config.getLocale();
		if (loc != null) {
			Locale.setDefault(loc);
		}

		// force init of log manager
		LogManager.getSingleton().initialize(VNAConfig.getSingleton());

		System.out.println("INFO::Logging setup done");

		// force init of data pool
		VNADataPool.init(VNAConfig.getSingleton());

		System.out.println("INFO::Data setup done");
		System.out.println("INFO::Available drivers in this library");
		final List<IVNADriver> driverList = VNADriverFactory.getSingleton().getDriverList();
		for (IVNADriver aDriver : driverList) {
			System.out.println("INFO::    [" + aDriver.getDeviceInfoBlock().getShortName() + "]");
			System.out.println("INFO::       fStart = " + aDriver.getDeviceInfoBlock().getMinFrequency()  + " Hz");
			System.out.println("INFO::       fStop  = " + aDriver.getDeviceInfoBlock().getMaxFrequency()  + " Hz");
		}
		System.out.println("INFO::Library setup done");

		//
		this.runner = new VNALibraryRunner();
	}

	/**
	 * Shutdown the library. Must be called prior to application termination
	 */
	public void shutdown() {
		this.runner.shutdown();
	}

	/**
	 * Load the desired driver.
	 * 
	 * @param driverName
	 *            Currently these drivers-names are supported.
	 *            <ul>
	 *            <li>miniVNA-pro</li>
	 *            <li>miniVNA-pro-LF</li>
	 *            <li>miniVNA-pro-extender</li>
	 *            <li>miniVNA-pro2</li>
	 *            <li>miniVNA</li>
	 *            <li>miniVNA-LF</li>
	 *            <li>miniVNA V2</li>
	 *            <li>miniVNA Tiny</li>
	 *            <li>MetroVNA</li>
	 *            <li>VNArduino</li>
	 *            <li>MAX6</li>
	 *            <li>MAX6-500MHz</li>
	 *            <li>Sample</li>
	 *            </ul>
	 * @param portName
	 *            The name of the serial port. Must match the runtime platform syntax. On Windows i.e. "COM3"
	 * @throws ProcessingException
	 *             In case of error
	 */
	public void loadDriverByName(final String driverName, final String portName) throws ProcessingException {
		this.runner.loadDriverByName(driverName, portName);
	}

	/**
	 * Load the calibration file from the specified file path. The calibration file must match selected driver and scan mode.
	 * 
	 * @param fileName
	 *            the complete filename of the cal file
	 * @throws ProcessingException
	 *             in case of error
	 */
	public void loadCalibrationFile(final String fileName) throws ProcessingException {
		this.runner.loadCalibrationFileByName(fileName);
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
		VNACalibratedSampleBlock rc;

		rc = this.runner.scan(fStart, fStop, numSteps, scanMode);

		return rc;
	}

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
		VNASampleBlock rc;

		rc = this.runner.scanRaw(fStart, fStop, numSteps, scanMode);

		return rc;
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
		this.runner.startGenerator(frequencyQ, frequencyI, attenuationI, attenuationQ, phase, mainAttenuation);
	}

	/**
	 * stop the frequency generator
	 * 
	 * @throws ProcessingException
	 *             in case of error
	 */
	public void stopGenerator() throws ProcessingException {
		this.runner.stopGenerator();
	}
}
