package krause.vna.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.device.VNADriverFactorySymbols;

public class VNASampleBlock implements Serializable {
	public static final String ANALYSER_TYPE_UNKNOWN = "99";

	private static final VNAConfig config = VNAConfig.getSingleton();

	// new one temperature on Double
	private static final long serialVersionUID = -231006451661112222L;

	private String analyserType = VNADriverFactorySymbols.TYPE_UNKOWN;
	private transient Double deviceSupply = null;
	private Double deviceTemperature = null;
	private transient IVNADriverMathHelper mathHelper = null;
	private int numberOfSteps = 0;
	private int numberOfOverscans = 0;
	private VNABaseSample[] samples = null;
	private VNAScanMode scanMode;
	private long startFrequency = 0;
	private long stopFrequency = 0;

	public VNASampleBlock() {
	}

	public VNASampleBlock(VNACalibrationBlock calibration) {
		setStartFrequency(calibration.getStartFrequency());
		setStopFrequency(calibration.getStopFrequency());
		setNumberOfSteps(calibration.getNumberOfSteps());
		setScanMode(calibration.getScanMode());
		setAnalyserType(calibration.getAnalyserType());
		setSamples(new VNABaseSample[getNumberOfSteps()]);
		setDeviceTemperature(calibration.getTemperature());
	}

	public void dump() {
		TraceHelper.entry(this, "dump");

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("vnaJ");

		// add header rows
		int rowNum = 1;
		int cell = 0;
		HSSFRow row = sheet.createRow(rowNum++);
		row.createCell(cell++).setCellValue(new HSSFRichTextString("Frequency"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("Loss"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("Angle"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("P1"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("P2"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("P3"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("P4"));

		// add data rows
		for (int i = 0; i < samples.length; ++i) {
			VNABaseSample data = samples[i];
			cell = 0;
			row = sheet.createRow(rowNum);
			row.createCell(cell++).setCellValue(data.getFrequency());
			row.createCell(cell++).setCellValue(data.getLoss());
			row.createCell(cell++).setCellValue(data.getAngle());
			if (data.hasPData()) {
				row.createCell(cell++).setCellValue(data.getP1());
				row.createCell(cell++).setCellValue(data.getP2());
				row.createCell(cell++).setCellValue(data.getP3());
				row.createCell(cell++).setCellValue(data.getP4());
			}
			++rowNum;
		}

		// autofit column size
		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);

		File fi = null;
		FileOutputStream fileOut = null;
		try {
			fi = File.createTempFile("raw_", ".xls", new File(config.getExportDirectory()));
			fileOut = new FileOutputStream(fi);
			wb.write(fileOut);
		} catch (IOException e) {
			ErrorLogHelper.exception(this, "dump", e);
		} finally {
			if (fileOut != null) {
				try {
					fileOut.close();
				} catch (IOException e) {
					ErrorLogHelper.exception(this, "dump", e);
				}
			}
		}
		TraceHelper.exit(this, "dump");
	}

	/**
	 * @return the analyserType
	 */
	public String getAnalyserType() {
		return analyserType;
	}

	public Double getDeviceSupply() {
		return deviceSupply;
	}

	public Double getDeviceTemperature() {
		return deviceTemperature;
	}

	/**
	 * @return the mathHelper
	 */
	public IVNADriverMathHelper getMathHelper() {
		return mathHelper;
	}

	/**
	 * @return the numberOfSteps
	 */
	public int getNumberOfSteps() {
		return numberOfSteps;
	}

	/**
	 * @return the samples
	 */
	public VNABaseSample[] getSamples() {
		return samples;
	}

	public VNAScanMode getScanMode() {
		return scanMode;
	}

	public long getStartFrequency() {
		return startFrequency;
	}

	public long getStopFrequency() {
		return stopFrequency;
	}

	/**
	 * @param analyserType
	 *            the analyserType to set
	 */
	public void setAnalyserType(String analyserType) {
		this.analyserType = analyserType;
	}

	public void setDeviceSupply(Double deviceSupply) {
		this.deviceSupply = deviceSupply;
	}

	public void setDeviceTemperature(Double deviceTemperature) {
		this.deviceTemperature = deviceTemperature;
	}

	/**
	 * @param mathHelper
	 *            the mathHelper to set
	 */
	public void setMathHelper(IVNADriverMathHelper mathHelper) {
		this.mathHelper = mathHelper;
	}

	/**
	 * @param numberOfSteps
	 *            the numberOfSteps to set
	 */
	public void setNumberOfSteps(int numberOfSteps) {
		this.numberOfSteps = numberOfSteps;
	}

	/**
	 * @param samples
	 *            the samples to set
	 */
	public void setSamples(VNABaseSample[] samples) {
		this.samples = samples;
	}

	public void setScanMode(VNAScanMode scanMode) {
		this.scanMode = scanMode;
	}

	public void setStartFrequency(long startFrequency) {
		this.startFrequency = startFrequency;
	}

	public void setStopFrequency(long stopFrequency) {
		this.stopFrequency = stopFrequency;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VNASampleBlock [numberOfSteps=" + numberOfSteps + ", #samples=" + samples.length + ", startFrequency=" + startFrequency + ", stopFrequency=" + stopFrequency + "]";
	}

	public int getNumberOfOverscans() {
		return numberOfOverscans;
	}

	public void setNumberOfOverscans(int numberOfOverscans) {
		this.numberOfOverscans = numberOfOverscans;
	}
}
