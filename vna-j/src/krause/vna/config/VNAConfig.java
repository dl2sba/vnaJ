/**
 * Copyright (C) 2020 Dietmar Krause, DL2SBA
 */
package krause.vna.config;

import java.awt.Color;
import java.io.File;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.math3.complex.Complex;

import krause.common.TypedProperties;
import krause.util.PropertiesHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNAGenericDriverSymbols;

/**
 * This is the general config object the the VNA application. It is handled with the singleton pattern.
 * 
 * This config contains accessors for the general stuff like window positions etc.
 * 
 * @author Dietmar Krause
 * 
 */
public class VNAConfig extends TypedProperties {
	private static VNAConfig singleton = null;

	/**
	 * Return the one and only instance of the config object
	 * 
	 * @return the only instance of this class
	 */
	public static VNAConfig getSingleton() {
		if (singleton == null) {
			synchronized (VNAConfig.class) {
				if (singleton == null) {
					try {
						singleton = new VNAConfig();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		return singleton;
	}

	/**
	 * Return the one and only instance of the config object
	 * 
	 * @return the only instance of this class
	 */
	public static TypedProperties init(String name, Properties defaultProperties) {
		if (singleton == null) {
			synchronized (VNAConfig.class) {
				if (singleton == null) {
					try {
						singleton = new VNAConfig();
						singleton.load(name, defaultProperties);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		return singleton;
	}

	private boolean mac = false;

	private String propertiesFileName = null;

	private boolean windows = false;

	protected VNAConfig() {
		super();
		TraceHelper.entry(this, "VNAConfig");

		String os = System.getProperty("os.name").toLowerCase();
		mac = (os.indexOf("mac") != -1);
		windows = (os.indexOf("windows") != -1);
		//
		String s = getVNAConfigDirectory();
		if (!new File(s).exists()) {
			System.out.println("INFO::Createing Config-Directory: " + s);
			// now create home directory for configs
			new File(s).mkdirs();
		}

		//
		s = getVNACalibrationDirectory();
		if (!new File(s).exists()) {
			System.out.println("INFO::Createing Calibration-Directory: " + s);
			new File(s).mkdirs();
		}

		s = getPresetsDirectory();
		if (!new File(s).exists()) {
			System.out.println("INFO::Createing Presets-Directory: " + s);
			new File(s).mkdirs();
		}

		s = getAutoExportDirectory();
		if (!new File(s).exists()) {
			System.out.println("INFO::Createing Autoexport-Directory: " + s);
			new File(s).mkdirs();
		}

		s = getExportDirectory();
		if (!new File(s).exists()) {
			System.out.println("INFO::Createing Export-Directory: " + s);
			new File(s).mkdirs();
		}

		s = getReferenceDirectory();
		if (!new File(s).exists()) {
			System.out.println("INFO::Createing Reference-Directory: " + s);
			new File(s).mkdirs();
		}
		TraceHelper.exit(this, "VNAConfig");
	}

	public String getAutoExportDirectory() {
		return getProperty(VNASystemConfig.VNA_PREFIX + "autoExportDirectory", VNASystemConfig.getVNA_HOME_DIR() + System.getProperty("file.separator") + "export");
	}

	public String getAutoExportFilename() {
		return getProperty(VNASystemConfig.VNA_PREFIX + "autoExportFilename", "VNA_{0,date,yyMMdd}_{0,time,HHmmss}");
	}

	public int getAutoExportFormat() {
		return getInteger(VNASystemConfig.VNA_PREFIX + "autoExportFormat", 0);
	}

	public String getCalibrationExportDirectory() {
		return getProperty(VNASystemConfig.VNA_PREFIX + "calibrationExportDirectory", VNASystemConfig.getVNA_HOME_DIR() + System.getProperty("file.separator") + "export");
	}

	public Color getColorBandmap() {
		return new Color(Integer.parseInt(getProperty("Color.Bandmap", Integer.toString(Color.DARK_GRAY.getRGB()))));
	}

	public Color getColorDiagram() {
		return new Color(Integer.parseInt(getProperty("Color.Diagram", Integer.toString(Color.BLACK.getRGB()))));
	}

	public Color getColorDiagramLines() {
		return new Color(Integer.parseInt(getProperty("Color.DiagramLines", Integer.toString(Color.LIGHT_GRAY.getRGB()))));
	}

	public Color getColorMarker(int i) {
		return new Color(Integer.parseInt(getProperty("Color.Marker." + i, Integer.toString(Color.WHITE.getRGB()))));
	}

	public Color getColorReference() {
		return new Color(Integer.parseInt(getProperty("Color.Reference", Integer.toString(Color.WHITE.getRGB()))));
	}

	public Color getColorScaleLeft() {
		return new Color(Integer.parseInt(getProperty("Color.ScaleLeft", Integer.toString(Color.GREEN.getRGB()))));
	}

	public Color getColorScaleRight() {
		return new Color(Integer.parseInt(getProperty("Color.ScaleRight", Integer.toString(Color.CYAN.getRGB()))));
	}

	public String getExportComment() {
		return getProperty(VNASystemConfig.VNA_PREFIX + "exportComment");
	}

	public String getExportDecimalSeparator() {
		return getProperty(VNASystemConfig.VNA_PREFIX + "exportDecimalSeparator", ".");
	}

	public int getExportDiagramHeight() {
		return getInteger(VNASystemConfig.VNA_PREFIX + "ExportDiagramHeight", 1024);
	}

	public int getExportDiagramWidth() {
		return getInteger(VNASystemConfig.VNA_PREFIX + "ExportDiagramWidth", 1280);
	}

	public String getExportDirectory() {
		return getProperty(VNASystemConfig.VNA_PREFIX + "exportDirectory", VNASystemConfig.getVNA_HOME_DIR() + System.getProperty("file.separator") + "export");
	}

	public String getExportFilename() {
		return getProperty(VNASystemConfig.VNA_PREFIX + "exportFileName", "Export");
	}

	public String getExportTitle() {
		return getProperty(VNASystemConfig.VNA_PREFIX + "exportTitle");
	}

	public int getExportTitleFontSize() {
		return getInteger(VNASystemConfig.VNA_PREFIX + "exportTitleFontSize", 24);
	}

	public String getFlashFilename() {
		return getProperty(VNASystemConfig.VNA_PREFIX + "flashFilename", "");
	}

	public int getFontSizeTextMarker() {
		return getInteger("FontSizeTextMarkers", 15);
	}

	public long getGeneratorFrequency() {
		return getLong("GeneratorFrequency", 1000000);
	}

	public String getInstallationDirectory() {
		return getProperty(VNASystemConfig.VNA_PREFIX + "installDirectory", System.getProperty("user.home"));
	}

	public String getLastRawComment() {
		return getProperty(VNASystemConfig.VNA_PREFIX + "lastRawComment", "");
	}

	/**
	 * @return
	 */
	public Locale getLocale() {
		// TraceHelper.entry(this, "getLocale");
		Locale rc = null;

		String language = getProperty("selectedLanguage");
		String country = getProperty("selectedCountry");

		if (language != null && country != null) {
			rc = new Locale(language, country);
		}
		// TraceHelper.exit(this, "getLocale");
		return rc;
	}

	public int getMarkerSize() {
		return getInteger(VNASystemConfig.VNA_PREFIX + "MarkerSize", 2);
	}

	public int getNumberOfOversample() {
		return getInteger(VNASystemConfig.VNA_PREFIX + "numberOfOversample", 1);
	}

	/**
	 * @return the numberOfSamples
	 */
	public int getNumberOfSamples() {
		return Integer.parseInt(getProperty("NumberOfSamples", "600"));
	}

	public String getPortName(IVNADriver driver) {
		return getProperty(driver.getDriverConfigPrefix() + VNAGenericDriverSymbols.PORTNAME);
	}

	public String getPresetsDirectory() {
		return getProperty(VNASystemConfig.VNA_PREFIX + "presetsDirectory", VNASystemConfig.getVNA_HOME_DIR() + System.getProperty("file.separator") + "presets/.");
	}

	public String getPropertiesFileName() {
		return propertiesFileName;
	}

	public String getReferenceDirectory() {
		return getProperty(VNASystemConfig.VNA_PREFIX + "referenceDirectory", VNASystemConfig.getVNA_HOME_DIR() + System.getProperty("file.separator") + "reference/.");
	}

	public double getPortExtensionVf() {
		return getDouble(VNASystemConfig.VNA_PREFIX + "portExtensionVf", 1.0);
	}

	public double getPortExtensionCableLength() {
		return getDouble(VNASystemConfig.VNA_PREFIX + "portExtensionCableLength", 0.0);
	}

	public boolean isPortExtensionEnabled() {
		return getBoolean(VNASystemConfig.VNA_PREFIX + "portExtensionState", false);
	}

	public void setPortExtensionVf(double vf) {
		putDouble(VNASystemConfig.VNA_PREFIX + "portExtensionVf", vf);
	}

	public void setPortExtensionCableLength(double len) {
		putDouble(VNASystemConfig.VNA_PREFIX + "portExtensionCableLength", len);
	}

	public void setPortExtensionState(boolean state) {
		putBoolean(VNASystemConfig.VNA_PREFIX + "portExtensionState", state);
	}

	public Complex getSmithReference() {
		double i = getDouble(VNASystemConfig.VNA_PREFIX + "smithReferenceImaginary", 0.0);
		double r = getDouble(VNASystemConfig.VNA_PREFIX + "smithReferenceReal", 50.0);
		return new Complex(r, i);
	}

	public String getVNACalibrationDirectory() {
		return VNASystemConfig.getVNA_HOME_DIR() + System.getProperty("file.separator") + "calibration";
	}

	public String getVNAConfigDirectory() {
		return VNASystemConfig.getVNA_HOME_DIR() + System.getProperty("file.separator") + "config";
	}

	public String getCalibrationKitFilename() {
		return getVNAConfigDirectory() + System.getProperty("file.separator") + "calibrationKits.xml";
	}

	public String getGaussianFilterFileName() {
		return getVNAConfigDirectory() + System.getProperty("file.separator") + "Gaussian.txt";
	}

	public Properties getVNADeviceConfigSymbols(String prefix) {
		return PropertiesHelper.createProperties(this, prefix, true);
	}

	/**
	 * @return
	 */
	public String getVNADriverType() {
		return getProperty(VNASystemConfig.VNA_PREFIX + "type");
	}

	public boolean isAskOnExit() {
		return getBoolean("askOnExit", true);
	}

	/**
	 * @return the whether autoscale is enabled
	 */
	public boolean isAutoscaleEnabled() {
		return getBoolean("AutoScale", true);
	}

	public boolean isControlPanelClose() {
		return getBoolean("controlClosesApp", false);
	}

	public boolean isExportOverwrite() {
		return getBoolean("exportOverwrite", true);
	}

	public boolean isExportRawData() {
		return getBoolean("exportRawData", false);
	}

	public boolean isMac() {
		return mac;
	}

	public boolean isMarkerModeLine() {
		return getBoolean(VNASystemConfig.VNA_PREFIX + "MarkerModeLine", false);
	}

	public boolean isApplyGaussianFilter() {
		return getBoolean(VNASystemConfig.VNA_PREFIX + "ApplyGaussianFilter", false);
	}

	public boolean isMarkerPanelClose() {
		return getBoolean("markerClosesApp", false);
	}

	public boolean isPrintFooter() {
		return getBoolean("PrintFooter", true);
	}

	public boolean isPrintMainLegend() {
		return getBoolean("PrintMainLegend", true);
	}

	public boolean isPrintMarkerDataHorizontal() {
		return getBoolean("PrintMarkerDataHorizontal", false);
	}

	public boolean isPrintMarkerDataInDiagramm() {
		return getBoolean("PrintMarkerDataInDiagramm", false);
	}

	public boolean isPrintSubLegend() {
		return getBoolean("PrintSubLegend", false);
	}

	public boolean isScanAfterTableSelect() {
		return getBoolean("scanAfterTableSelect", true);
	}

	public boolean isScanAfterZoom() {
		return getBoolean("scanAfterZoom", true);
	}

	public boolean isShowBandmap() {
		return getBoolean(VNASystemConfig.VNA_PREFIX + "ShowBandMap", false);
	}

	public boolean isTurnOffGenAfterScan() {
		return getBoolean("turnOffGenAfterScan", true);
	}

	public boolean isWindows() {
		return windows;
	}

	private void load(String name, Properties defaultProperties) {
		TraceHelper.entry(this, "load", name);

		// build the name of the properties file
		propertiesFileName = getVNAConfigDirectory() + "/" + name;
		// first try to load the properties of the application
		putAll(PropertiesHelper.loadXMLProperties(propertiesFileName, defaultProperties));

		// now check, whether one of the properties is overwritten in the system
		// properties
		for (Entry<Object, Object> entry : System.getProperties().entrySet()) {
			String key = (String) entry.getKey();
			if (key.startsWith("java.")) {
			} else if (key.startsWith("awt.")) {
			} else if (key.startsWith("os.")) {
			} else if (key.startsWith("file.")) {
			} else if (key.startsWith("line.")) {
			} else if (key.startsWith("path.")) {
			} else if (key.startsWith("sun.")) {
			} else if (key.startsWith("user.")) {
			} else {
				put(entry.getKey(), entry.getValue());
			}
		}
		TraceHelper.exit(this, "load");
	}

	/**
	 * saves the properties to an xml file
	 * 
	 */
	public void save() {
		TraceHelper.entry(this, "save");
		PropertiesHelper.saveXMLProperties(this, propertiesFileName);
		TraceHelper.exit(this, "save");
	}

	public void setAskOnExit(boolean val) {
		putBoolean("askOnExit", val);
	}

	/**
	 * @param text
	 */
	public void setAutoExportDirectory(String text) {
		setProperty(VNASystemConfig.VNA_PREFIX + "autoExportDirectory", text);
	}

	/**
	 * @param text
	 */
	public void setAutoExportFilename(String text) {
		setProperty(VNASystemConfig.VNA_PREFIX + "autoExportFilename", text);
	}

	public void setAutoExportFormat(int val) {
		putInteger(VNASystemConfig.VNA_PREFIX + "autoExportFormat", val);
	}

	public void setAutoscaleEnabled(boolean val) {
		setProperty("AutoScale", Boolean.toString(val));
	}

	public void setCalibrationExportDirectory(String text) {
		setProperty(VNASystemConfig.VNA_PREFIX + "calibrationExportDirectory", text);
	}

	public void setColorBandmap(Color color) {
		setProperty("Color.Bandmap", Integer.toString(color.getRGB()));
	}

	public void setColorDiagram(Color color) {
		setProperty("Color.Diagram", Integer.toString(color.getRGB()));
	}

	public void setColorDiagramLines(Color color) {
		setProperty("Color.DiagramLines", Integer.toString(color.getRGB()));
	}

	public void setColorMarker(int i, Color color) {
		setProperty("Color.Marker." + i, Integer.toString(color.getRGB()));
	}

	public void setColorReference(Color color) {
		setProperty("Color.Reference", Integer.toString(color.getRGB()));
	}

	public void setColorScaleLeft(Color color) {
		setProperty("Color.ScaleLeft", Integer.toString(color.getRGB()));
	}

	public void setColorScaleRight(Color color) {
		setProperty("Color.ScaleRight", Integer.toString(color.getRGB()));
	}

	public void setControlPanelClose(boolean val) {
		putBoolean("controlClosesApp", val);
	}

	public void setExportComment(String comment) {
		setProperty(VNASystemConfig.VNA_PREFIX + "exportComment", comment);
	}

	public void setExportDecimalSeparator(String name) {
		setProperty(VNASystemConfig.VNA_PREFIX + "exportDecimalSeparator", name);
	}

	public void setExportDiagramHeight(int h) {
		putInteger(VNASystemConfig.VNA_PREFIX + "ExportDiagramHeight", h);
	}

	public void setExportDiagramWidth(int w) {
		putInteger(VNASystemConfig.VNA_PREFIX + "ExportDiagramWidth", w);
	}

	public void setExportDirectory(String name) {
		setProperty(VNASystemConfig.VNA_PREFIX + "exportDirectory", name);
	}

	public void setExportFilename(String name) {
		setProperty(VNASystemConfig.VNA_PREFIX + "exportFileName", name);
	}

	public void setExportOverwrite(boolean val) {
		setProperty("exportOverwrite", Boolean.toString(val));
	}

	public void setExportRawData(boolean val) {
		setProperty("exportRawData", Boolean.toString(val));
	}

	public void setExportTitle(String name) {
		setProperty(VNASystemConfig.VNA_PREFIX + "exportTitle", name);
	}

	public void setExportTitleFontSize(int size) {
		putInteger(VNASystemConfig.VNA_PREFIX + "exportTitleFontSize", size);
	}

	public void setFlashFilename(String name) {
		setProperty(VNASystemConfig.VNA_PREFIX + "flashFilename", name);
	}

	public void setFontSizeTextMarker(int s) {
		putInteger("FontSizeTextMarkers", s);
	}

	public void setGeneratorFrequency(long freq) {
		putLong("GeneratorFrequency", freq);
	}

	public void setInstallationDirectory(String name) {
		setProperty(VNASystemConfig.VNA_PREFIX + "installDirectory", name);
	}

	public void setLastRawComment(String comment) {
		setProperty(VNASystemConfig.VNA_PREFIX + "lastRawComment", comment);
	}

	/**
	 * @param loc
	 */
	public void setLocale(Locale loc) {
		TraceHelper.entry(this, "setLocale");
		if (loc == null) {
			remove("selectedLanguage");
			remove("selectedCountry");
		} else {
			setProperty("selectedLanguage", loc.getLanguage());
			setProperty("selectedCountry", loc.getCountry());
		}
		TraceHelper.exit(this, "setLocale");
	}

	public void setMarkerModeLine(boolean mode) {
		putBoolean(VNASystemConfig.VNA_PREFIX + "MarkerModeLine", mode);
	}

	public void setApplyGaussianFilter(boolean gauss) {
		putBoolean(VNASystemConfig.VNA_PREFIX + "ApplyGaussianFilter", gauss);
	}

	public void setMarkerPanelClose(boolean val) {
		putBoolean("markerClosesApp", val);
	}

	public void setMarkerSize(int size) {
		putInteger(VNASystemConfig.VNA_PREFIX + "MarkerSize", size);
	}

	public void setNumberOfOversample(int num) {
		putInteger(VNASystemConfig.VNA_PREFIX + "numberOfOversample", num);
	}

	/**
	 * @param numberOfSamples
	 *            the numberOfSamples to set
	 */
	public void setNumberOfSamples(int numberOfSamples) {
		setProperty("NumberOfSamples", Integer.toString(numberOfSamples));
	}

	public void setPortName(IVNADriver driver, String port) {
		setProperty(driver.getDriverConfigPrefix() + VNAGenericDriverSymbols.PORTNAME, port);
	}

	public void setPresetsDirectory(String name) {
		setProperty(VNASystemConfig.VNA_PREFIX + "presetsDirectory", name);
	}

	public void setPrintFooter(boolean val) {
		putBoolean("PrintFooter", val);
	}

	public void setPrintMainLegend(boolean val) {
		putBoolean("PrintMainLegend", val);
	}

	public void setPrintMarkerDataHorizontal(boolean val) {
		putBoolean("PrintMarkerDataHorizontal", val);
	}

	public void setPrintMarkerDataInDiagramm(boolean val) {
		putBoolean("PrintMarkerDataInDiagramm", val);
	}

	public void setPrintSubLegend(boolean val) {
		putBoolean("PrintSubLegend", val);
	}

	public void setReferenceDirectory(String name) {
		setProperty(VNASystemConfig.VNA_PREFIX + "referenceDirectory", name);
	}

	public void setScanAfterTableSelect(boolean val) {
		putBoolean("scanAfterTableSelect", val);
	}

	public void setScanAfterZoom(boolean val) {
		putBoolean("scanAfterZoom", val);
	}

	public void setShowBandmap(boolean val) {
		putBoolean(VNASystemConfig.VNA_PREFIX + "ShowBandMap", val);
	}

	public void setSmithReference(Complex val) {
		putDouble(VNASystemConfig.VNA_PREFIX + "smithReferenceImaginary", val.getImaginary());
		putDouble(VNASystemConfig.VNA_PREFIX + "smithReferenceReal", val.getReal());
	}

	public void setTurnOffGenAfterScan(boolean val) {
		setProperty("turnOffGenAfterScan", Boolean.toString(val));
	}

	public void setVNADriverType(String typ) {
		setProperty(VNASystemConfig.VNA_PREFIX + "type", typ);
	}

	public void setPhosphor(boolean val) {
		putBoolean(VNASystemConfig.VNA_PREFIX + "Phosphor", val);
	}

	public void setResizeLocked(boolean val) {
		putBoolean(VNASystemConfig.VNA_PREFIX + "ResizeLocked", val);
	}

	public boolean isPhosphor() {
		return getBoolean(VNASystemConfig.VNA_PREFIX + "Phosphor", false);
	}

	public boolean isResizeLocked() {
		return getBoolean(VNASystemConfig.VNA_PREFIX + "ResizeLocked", false);
	}

	public void setAverage(int value) {
		putInteger(VNASystemConfig.VNA_PREFIX + "Average", value);
	}

	public int getAverage() {
		return getInteger(VNASystemConfig.VNA_PREFIX + "Average", 1);
	}

	public void setScanSpeed(int value) {
		putInteger(VNASystemConfig.VNA_PREFIX + "ScanSpeed", value);
	}

	public int getScanSpeed() {
		return getInteger(VNASystemConfig.VNA_PREFIX + "ScanSpeed", 1);
	}

	public void setThemeID(int id) {
		final String methodName = "setThemeID";
		TraceHelper.entry(this, methodName, "id=%d", id);

		putInteger(VNASystemConfig.VNA_PREFIX + "Theme", id);
		TraceHelper.exit(this, methodName);
	}

	public int getThemeID() {
		final String methodName = "getThemeID";
		TraceHelper.entry(this, methodName);

		int rc = getInteger(VNASystemConfig.VNA_PREFIX + "Theme", 0);
		TraceHelper.exitWithRC(this, methodName, "id=%d", rc);
		return rc;
	}

	public String getCurrentCalSetID() {
		return getProperty(VNASystemConfig.VNA_PREFIX + "CurrentCalSet", null);
	}

	public void setCurrentCalSetID(final String id) {
		setProperty(VNASystemConfig.VNA_PREFIX + "CurrentCalSet", id);
	}

	public String getSmithChartConfigFilename() {
		return getVNAConfigDirectory() + System.getProperty("file.separator") + "SmithChartCircles.txt";
	}
}
