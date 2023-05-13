package krause.vna.device;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.complex.Complex;

import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;

public abstract class VNADeviceInfoBlock implements Serializable {
	public static final int FILTER_MODE1 = 1;
	public static final int FILTER_MODE2 = 2;
	public static final int FILTER_NONE = 0;
	public static final int DEFAULT_FILTERMODE = FILTER_MODE1;
	public static final int DEFAULT_LOCAL_TIMEOUT = 1000;
	public static final double DEFAULT_REFERENCE_RESISTANCE_IMAG = 0.0;

	public static final double DEFAULT_REFERENCE_RESISTANCE_REAL = 50.0;

	public static final long ONE_KHZ = 1000L;
	public static final long ONE_MHZ = 1000L * ONE_KHZ;
	public static final long ONE_GHZ = 1000L * ONE_MHZ;

	public static final String PROPERTIES_DDSTICKS = "ddsTicks";
	public static final String PROPERTIES_FILTERMODE = "filterMode";
	public static final String PROPERTIES_FREQUENCY_MAX = "freqMax";

	public static final String PROPERTIES_FREQUENCY_MIN = "freqMin";
	public static final String PROPERTIES_LOSS_MAX = "lossMax";

	public static final String PROPERTIES_LOSS_MIN = "lossMin";
	public static final String PROPERTIES_NUMBEROFSAMPLES4CALIB = "nOfSamples4Calibration";
	public static final String PROPERTIES_NUMBEROFOVERSCAN4CALIB = "nOfOversamples4Calibration";

	public static final String PROPERTIES_PEAKSUPPRESSION = "peakSuppression";
	public static final String PROPERTIES_REFERENCECHANNEL = "useReferenceChannel";
	public static final String PROPERTIES_REFERENCE_RESISTANCE_IMAG = "referenceImag";

	public static final String PROPERTIES_REFERENCE_RESISTANCE_REAL = "referenceReal";

	public static final String PROPERTIES_FIRMWARE_FILE_FILTER = "firmwareFileFilter";

	private long ddsTicksPerMHz;
	private int filterMode;
	private String longName;

	private long maxFrequency;
	private double maxLoss;
	private double maxPhase;
	private long minFrequency;
	private double minLoss;
	private double minPhase;
	private int numberOfOverscans4Calibration;
	private int numberOfSamples4Calibration;
	private boolean peakSuppression;
	private boolean referenceChannel;
	private Complex referenceResistance;
	private String firmwareFileFilter;

	private transient Map<VNAScanMode, VNAScanModeParameter> scanModeParameters = new HashMap<VNAScanMode, VNAScanModeParameter>();

	// naming
	private String shortName;

	private String type;

	public void addScanModeParameter(VNAScanModeParameter pParm) {
		scanModeParameters.put(pParm.getMode(), pParm);
	}

	public void clearScanModeParameters() {
		scanModeParameters.clear();
	}

	public long getDdsTicksPerMHz() {
		return ddsTicksPerMHz;
	}

	public int getFilterMode() {
		return filterMode;
	}

	public String getLongName() {
		return longName;
	}

	public long getMaxFrequency() {
		return maxFrequency;
	}

	public double getMaxLoss() {
		return maxLoss;
	}

	public double getMaxPhase() {
		return maxPhase;
	}

	public long getMinFrequency() {
		return minFrequency;
	}

	public double getMinLoss() {
		return minLoss;
	}

	public double getMinPhase() {
		return minPhase;
	}

	public int getNumberOfOverscans4Calibration() {
		return numberOfOverscans4Calibration;
	}

	public int getNumberOfSamples4Calibration() {
		return numberOfSamples4Calibration;
	}

	public Complex getReferenceResistance() {
		return referenceResistance;
	}

	public VNAScanModeParameter getScanModeParameterForMode(VNAScanMode pScanMode) {
		return scanModeParameters.get(pScanMode);
	}

	public Map<VNAScanMode, VNAScanModeParameter> getScanModeParameters() {
		return scanModeParameters;
	}

	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * returns a list of frequencies point, which should be excluded from measured data
	 * 
	 * @return array of frequencies to exclude
	 */
	public long[] getSwitchPoints() {
		return null;
	}

	public String getType() {
		return type;
	}

	public boolean isPeakSuppression() {
		return peakSuppression;
	}

	/**
	 * Reset the DIB to the default values
	 */
	public void reset() {
		numberOfSamples4Calibration = VNAGenericDriver.MINIMUM_SCAN_POINTS;
		numberOfOverscans4Calibration = 1;

		peakSuppression = false;

		ddsTicksPerMHz = 1000000;

		filterMode = DEFAULT_FILTERMODE;

		minFrequency = ONE_MHZ;
		maxFrequency = ONE_GHZ;
		minLoss = 0;
		maxLoss = -90;
		minPhase = 0;
		maxPhase = 90;

		firmwareFileFilter = "*.hex";

		referenceResistance = new Complex(DEFAULT_REFERENCE_RESISTANCE_REAL, DEFAULT_REFERENCE_RESISTANCE_IMAG);
	}

	/**
	 * restore the data from the given properties
	 * 
	 * @param config
	 * @param prefix
	 */
	public void restore(TypedProperties config, String prefix) {
		TraceHelper.entry(this, "restore", prefix);
		setDdsTicksPerMHz(config.getLong(prefix + PROPERTIES_DDSTICKS, getDdsTicksPerMHz()));

		setMinLoss(config.getDouble(prefix + "minLoss", getMinLoss()));
		setMaxLoss(config.getDouble(prefix + "maxLoss", getMaxLoss()));

		setMinPhase(config.getDouble(prefix + "minPhase", getMinPhase()));
		setMaxPhase(config.getDouble(prefix + "maxPhase", getMaxPhase()));

		setMinFrequency(config.getLong(prefix + "minFrequency", getMinFrequency()));
		setMaxFrequency(config.getLong(prefix + "maxFrequency", getMaxFrequency()));

		double real = config.getDouble(prefix + PROPERTIES_REFERENCE_RESISTANCE_REAL, DEFAULT_REFERENCE_RESISTANCE_REAL);
		double imag = config.getDouble(prefix + PROPERTIES_REFERENCE_RESISTANCE_IMAG, DEFAULT_REFERENCE_RESISTANCE_IMAG);
		setReferenceResistance(new Complex(real, imag));

		setNumberOfSamples4Calibration(config.getInteger(prefix + PROPERTIES_NUMBEROFSAMPLES4CALIB, getNumberOfSamples4Calibration()));
		setNumberOfOverscans4Calibration(config.getInteger(prefix + PROPERTIES_NUMBEROFOVERSCAN4CALIB, getNumberOfOverscans4Calibration()));

		setFilterMode(config.getInteger(prefix + PROPERTIES_FILTERMODE, getFilterMode()));
		//

		setPeakSuppression(config.getBoolean(prefix + PROPERTIES_PEAKSUPPRESSION, isPeakSuppression()));
		setReferenceChannel(config.getBoolean(prefix + PROPERTIES_REFERENCECHANNEL, hasReferenceChannel()));
		setFirmwareFileFilter(config.getProperty(prefix + PROPERTIES_FIRMWARE_FILE_FILTER, getFirmwareFileFilter()));

		TraceHelper.exit(this, "restore");
	}

	public void setDdsTicksPerMHz(long ddsTicksPerMHz) {
		this.ddsTicksPerMHz = ddsTicksPerMHz;
	}

	public void setFilterMode(int filterMode) {
		this.filterMode = filterMode;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public void setMaxFrequency(long maxFrequency) {
		this.maxFrequency = maxFrequency;
	}

	public void setMaxLoss(double maxLoss) {
		this.maxLoss = maxLoss;
	}

	public void setMaxPhase(double maxPhase) {
		this.maxPhase = maxPhase;
	}

	public void setMinFrequency(long minFrequency) {
		this.minFrequency = minFrequency;
	}

	public void setMinLoss(double minLoss) {
		this.minLoss = minLoss;
	}

	public void setMinPhase(double minPhase) {
		this.minPhase = minPhase;
	}

	public void setNumberOfOverscans4Calibration(int numberOfOverscans4Calibration) {
		this.numberOfOverscans4Calibration = numberOfOverscans4Calibration;
	}

	public void setNumberOfSamples4Calibration(int numberOfSamples) {
		this.numberOfSamples4Calibration = numberOfSamples;
	}

	public void setPeakSuppression(boolean pPeakSuppression) {
		peakSuppression = pPeakSuppression;
	}

	public void setReferenceResistance(Complex referenceResistance) {
		this.referenceResistance = referenceResistance;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 
	 * @param config
	 * @param prefix
	 */
	public void store(TypedProperties config, String prefix) {
		TraceHelper.entry(this, "store");

		config.putLong(prefix + PROPERTIES_DDSTICKS, getDdsTicksPerMHz());

		config.putDouble(prefix + "minLoss", getMinLoss());
		config.putDouble(prefix + "maxLoss", getMaxLoss());

		config.putDouble(prefix + "minPhase", getMinPhase());
		config.putDouble(prefix + "maxPhase", getMaxPhase());

		config.putLong(prefix + "minFrequency", getMinFrequency());
		config.putLong(prefix + "maxFrequency", getMaxFrequency());

		config.putDouble(prefix + PROPERTIES_REFERENCE_RESISTANCE_REAL, getReferenceResistance().getReal());
		config.putDouble(prefix + PROPERTIES_REFERENCE_RESISTANCE_IMAG, getReferenceResistance().getImaginary());

		config.putInteger(prefix + PROPERTIES_NUMBEROFSAMPLES4CALIB, getNumberOfSamples4Calibration());
		config.putInteger(prefix + PROPERTIES_NUMBEROFOVERSCAN4CALIB, getNumberOfOverscans4Calibration());

		config.putInteger(prefix + PROPERTIES_FILTERMODE, getFilterMode());
		config.putBoolean(prefix + PROPERTIES_PEAKSUPPRESSION, isPeakSuppression());
		config.putBoolean(prefix + PROPERTIES_REFERENCECHANNEL, hasReferenceChannel());
		config.setProperty(prefix + PROPERTIES_FIRMWARE_FILE_FILTER, getFirmwareFileFilter());

		TraceHelper.exit(this, "store");
	}

	public String getFirmwareFileFilter() {
		return firmwareFileFilter;
	}

	public void setFirmwareFileFilter(String firmwareFileFilter) {
		this.firmwareFileFilter = firmwareFileFilter;
	}

	public boolean hasReferenceChannel() {
		return referenceChannel;
	}

	public void setReferenceChannel(boolean useReferenceChannel) {
		this.referenceChannel = useReferenceChannel;
	}

	public abstract int calculateRealBaudrate(final int driverBaudrate);
}
