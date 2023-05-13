package krause.vna.headless;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import krause.common.exception.InitializationException;
import krause.common.exception.InvalidParameterException;
import krause.common.exception.ProcessingException;
import krause.common.validation.ValidationResults;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.IVNABackgroundTaskStatusListener;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.data.helper.VNASampleBlockHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNADriverFactory;
import krause.vna.device.VNAScanRange;
import krause.vna.export.CSVExporter;
import krause.vna.export.SnPExporter;
import krause.vna.export.VNAExporter;
import krause.vna.export.XLSExporter;
import krause.vna.export.XMLExporter;
import krause.vna.export.ZPlotsExporter;
import krause.vna.resources.VNAMessages;

public class VNAHeadlessRunner implements IVNABackgroundTaskStatusListener {
	public static final String P_FSTART = "fstart";
	public static final String P_FSTOP = "fstop";
	public static final String P_FSTEPS = "fsteps";
	public static final String P_DRIVERID = "driverId";
	public static final String P_DRIVERPORT = "driverPort";
	public static final String P_CALFILENAME = "calfile";
	public static final String P_SCANMODE = "scanmode";
	private static final String P_EXPORTS = "exports";
	private static final String P_AVERAGE = "average";
	private static final String P_EXPORT_SNP = "snp";
	private static final String P_EXPORT_XML = "xml";
	private static final String P_EXPORT_ZPLOTS = "zplots";
	private static final String P_EXPORT_XLS = "xls";
	private static final String P_EXPORT_CSV = "csv";
	private static final String P_EXPORT_DIRECTORY = "exportDirectory";
	private static final String P_EXPORT_FILENAME = "exportFilename";
	private static final String P_KEEP_GENON = "keepGeneratorOn";
	private static final String P_NUMSCANS = "numberOfScans";

	private final VNADataPool datapool = VNADataPool.getSingleton();
	private final VNAConfig config = VNAConfig.getSingleton();

	private long fStart;
	private long fStop;
	private int fSteps;
	private int driverId;
	private int average;
	private int numberOfScans;

	private String calFileName = null;
	private String driverPort = null;
	private String exportFilename = null;
	private String exportDirectory = null;

	private boolean exportSNP = false;
	private boolean exportXML = false;
	private boolean exportCSV = false;
	private boolean exportXLS = false;
	private boolean exportZPLOTS = false;
	private boolean keepGenOn = false;

	private void doScan() throws ProcessingException {
		TraceHelper.entry(this, "doScan");

		// read from fields
		final VNAScanRange range = new VNAScanRange(fStart, fStop, fSteps);
		datapool.setFrequencyRange(range);

		// now ask the driver whether the range is fine
		ValidationResults valRes = datapool.getDriver().validateScanRange(range);

		// scan range valid?
		if (!valRes.isEmpty()) {
			throw new ProcessingException("Scan range not valid");
		}

		if (!datapool.getDriver().isScanSupported(fSteps, range, datapool.getScanMode())) {
			throw new ProcessingException("Scanmode, frequency range or #steps not supported");
		}

		final VNACalibrationBlock rcb = VNACalibrationBlockHelper.createResizedCalibrationBlock(datapool.getMainCalibrationBlock(), fStart, fStop, fSteps);
		datapool.setResizedCalibrationBlock(rcb);

		if (datapool.getResizedCalibrationBlock() == null) {
			throw new ProcessingException("No calibration block set.");
		}

		for (int j = 0; j < numberOfScans; ++j) {

			//
			final List<VNASampleBlock> blocks = new ArrayList<>();
			long startTime = System.currentTimeMillis();

			for (int i = 0; i < average; ++i) {
				// now read one bunch of data from device
				final VNASampleBlock data = datapool.getDriver().scan(datapool.getScanMode(), fStart, fStop, fSteps, this);
				blocks.add(data);
			}

			System.out.println(MessageFormat.format(VNAMessages.getString("Message.headless.15"), System.currentTimeMillis() - startTime));

			final VNASampleBlock data = VNASampleBlockHelper.calculateAverageSampleBlock(blocks);

			// data present?
			if (data == null) {
				throw new ProcessingException("No data set on jobresult");
			}

			// store in global pool
			datapool.setRawData(data);

			final IVNADriverMathHelper mathHelper = data.getMathHelper();
			if (mathHelper == null) {
				throw new ProcessingException("No mathHelper set on data");
			}

			// filter raw data
			mathHelper.applyFilter(data.getSamples());

			// create cal context for this scan
			final VNACalibrationContext context = mathHelper.createCalibrationContextForCalibratedSamples(datapool.getResizedCalibrationBlock());
			context.setConversionTemperature(data.getDeviceTemperature());

			// put calibrated data into pool for further processing
			final VNACalibratedSampleBlock calSamples = mathHelper.createCalibratedSamples(context, data);
			datapool.setCalibratedData(calSamples);

			// Now export the data
			startTime = System.currentTimeMillis();
			if (exportCSV) {
				internalAutoExport(new CSVExporter(null));
			}
			if (exportSNP) {
				internalAutoExport(new SnPExporter(null));
			}
			if (exportXLS) {
				internalAutoExport(new XLSExporter(null));
			}
			if (exportXML) {
				internalAutoExport(new XMLExporter(null));
			}
			if (exportZPLOTS) {
				internalAutoExport(new ZPlotsExporter(null));
			}
			System.out.println(MessageFormat.format(VNAMessages.getString("Message.headless.16"), System.currentTimeMillis() - startTime));
		}
		// turn off the generator
		if (!keepGenOn) {
			datapool.getDriver().stopGenerator();
			System.out.println(VNAMessages.getString("Message.headless.14"));
		}

		TraceHelper.exit(this, "doScan");
	}

	/**
	 * @return
	 * @throws ProcessingException
	 * 
	 */
	public void loadDriver() throws ProcessingException {
		TraceHelper.entry(this, "loadDriver");
		try {
			IVNADriver drv = VNADriverFactory.getSingleton().getDriverForType("" + driverId);
			if (drv == null) {
				throw new ProcessingException("driver load failed for id [" + driverId + "]");
			}
			datapool.setDriver(drv);
			config.setPortName(drv, driverPort);
			System.out.println(MessageFormat.format(VNAMessages.getString("Message.headless.9"), datapool.getDriver().getDeviceInfoBlock().getLongName()));
			drv.init();
			System.out.println(MessageFormat.format(VNAMessages.getString("Message.headless.8"), datapool.getDriver().getPortname()));
			System.out.println(MessageFormat.format(VNAMessages.getString("Message.headless.10"), datapool.getDriver().getDeviceInfoBlock().getMinFrequency(), datapool.getDriver().getDeviceInfoBlock().getMaxFrequency()));

		} catch (InitializationException e) {
			throw new ProcessingException("init driver failed");
		}

		TraceHelper.exit(this, "loadDriver");
	}

	/**
	 * Try to load all calibration blocks defined in configuration
	 * 
	 * @throws ProcessingException
	 */
	public void loadCalibrationBlock() throws ProcessingException {
		TraceHelper.entry(this, "loadCalibrationBlock");

		final VNADeviceInfoBlock dib = datapool.getDriver().getDeviceInfoBlock();
		final VNACalibrationKit kit = datapool.getCalibrationKit();
		final String pathname = calFileName;
		final File file = new File(pathname);
		final VNACalibrationBlock block = VNACalibrationBlockHelper.load(file, datapool.getDriver(), kit);
		datapool.setMainCalibrationBlock(block);

		if (!block.blockMatches(dib)) {
			throw new ProcessingException("cal block not matching driver");
		}

		if (!block.getScanMode().equals(datapool.getScanMode())) {
			throw new ProcessingException("cal block not matching scan mode");
		}

		TraceHelper.exit(this, "loadCalibrationBlock");
	}

	@Override
	public void publishProgress(int percentage) {
		final String methodName = "publishProgress";
		TraceHelper.entry(this, methodName);
		TraceHelper.exit(this, methodName);
	}

	/**
	 * 
	 * @throws ProcessingException
	 */
	private void readCommandlineParameters() throws ProcessingException {
		TraceHelper.entry(this, "readCommandlineParameters");
		try {
			fStart = Long.valueOf(System.getProperty(P_FSTART));
		} catch (NumberFormatException e) {
			throw new InvalidParameterException("Parameter [" + P_FSTART + "] not correctly set", e);
		}

		try {
			fStop = Long.valueOf(System.getProperty(P_FSTOP));
		} catch (NumberFormatException e) {
			throw new InvalidParameterException("Parameter [" + P_FSTOP + "] not correctly set", e);
		}

		try {
			fSteps = Integer.valueOf(System.getProperty(P_FSTEPS));
		} catch (NumberFormatException e) {
			throw new InvalidParameterException("Parameter [" + P_FSTEPS + "] not correctly set", e);
		}

		try {
			driverId = Integer.valueOf(System.getProperty(P_DRIVERID));
		} catch (NumberFormatException e) {
			throw new InvalidParameterException("Parameter [" + P_DRIVERID + "] not correctly set", e);
		}

		try {
			average = Integer.valueOf(System.getProperty(P_AVERAGE, "1"));
		} catch (NumberFormatException e) {
			average = 1;
		}

		try {
			numberOfScans = Integer.valueOf(System.getProperty(P_NUMSCANS, "1"));
		} catch (NumberFormatException e) {
			average = 1;
		}

		calFileName = System.getProperty(P_CALFILENAME);
		if (calFileName == null || calFileName.length() < 5) {
			throw new InvalidParameterException("Parameter [" + P_CALFILENAME + "] not correctly set", null);
		}

		exportDirectory = System.getProperty(P_EXPORT_DIRECTORY);
		if (exportDirectory == null) {
			throw new InvalidParameterException("Parameter [" + P_EXPORT_DIRECTORY + "] not correctly set", null);
		}

		exportFilename = System.getProperty(P_EXPORT_FILENAME);
		if (exportFilename == null) {
			throw new InvalidParameterException("Parameter [" + P_EXPORT_FILENAME + "] not correctly set", null);
		}

		driverPort = System.getProperty(P_DRIVERPORT);
		if (calFileName == null) {
			throw new InvalidParameterException("Parameter [" + P_DRIVERPORT + "] not correctly set", null);
		}

		try {
			datapool.setScanMode(VNAScanMode.restoreFromString(System.getProperty(P_SCANMODE)));
		} catch (ProcessingException e) {
			throw new InvalidParameterException("Parameter [" + P_SCANMODE + "] not correctly set", e);
		}

		keepGenOn = (System.getProperty(P_KEEP_GENON) != null);

		//
		String formats = System.getProperty(P_EXPORTS, P_EXPORT_SNP);
		if (formats != null) {
			exportSNP = formats.contains(P_EXPORT_SNP);
			exportXML = formats.contains(P_EXPORT_XML);
			exportXLS = formats.contains(P_EXPORT_XLS);
			exportCSV = formats.contains(P_EXPORT_CSV);
			exportZPLOTS = formats.contains(P_EXPORT_ZPLOTS);
		}

		System.out.println("INFO::start frequency .......[" + fStart + "]");
		System.out.println("INFO::stop  frequency .......[" + fStop + "]");
		System.out.println("INFO::frequency steps .......[" + fSteps + "]");
		System.out.println("INFO::averaging .............[" + average + "]");
		System.out.println("INFO::number of scans .......[" + numberOfScans + "]");
		System.out.println("INFO::scan mode .............[" + datapool.getScanMode().toString() + "]");
		System.out.println("INFO::calibration file ......[" + calFileName + "]");
		System.out.println("INFO::export directory ......[" + exportDirectory + "]");
		System.out.println("INFO::   SnP ................[" + exportSNP + "]");
		System.out.println("INFO::   XML ................[" + exportXML + "]");
		System.out.println("INFO::   XLS ................[" + exportXLS + "]");
		System.out.println("INFO::   CSV ................[" + exportCSV + "]");
		System.out.println("INFO::   Zplots .............[" + exportZPLOTS + "]");

		TraceHelper.exit(this, "readCommandlineParameters");
	}

	/**
	 * @param csvExporter
	 * @return
	 * @throws ProcessingException
	 */
	private String internalAutoExport(VNAExporter exporter) throws ProcessingException {
		TraceHelper.entry(this, "internalAutoExport");
		String fnp = exportDirectory + System.getProperty("file.separator") + exportFilename;
		String filename = exporter.export(fnp, true);

		System.out.println(MessageFormat.format(VNAMessages.getString("Message.headless.7"), filename));

		TraceHelper.exit(this, "internalAutoExport");
		return filename;
	}

	/**
	 * @throws ProcessingException
	 * 
	 */
	public void run() throws ProcessingException {
		TraceHelper.entry(this, "run");

		try {
			long startTime;

			readCommandlineParameters();

			startTime = System.currentTimeMillis();
			loadDriver();
			System.out.println("INFO::Loaded driver in " + (System.currentTimeMillis() - startTime) + "ms");

			startTime = System.currentTimeMillis();
			loadCalibrationBlock();
			System.out.println("INFO::Loaded calibration data in " + (System.currentTimeMillis() - startTime) + "ms");

			System.out.println(MessageFormat.format(VNAMessages.getString("Message.headless.11"), datapool.getMainCalibrationBlock().getCalibrationPoints().length));
			System.out.println(MessageFormat.format(VNAMessages.getString("Message.headless.12"), fStart, fStop));

			doScan();
			System.out.println(VNAMessages.getString("Message.headless.6"));

		} catch (ProcessingException e) {
			ErrorLogHelper.exception(this, "run", e);
			throw e;
		}
		TraceHelper.exit(this, "run");
	}
}
