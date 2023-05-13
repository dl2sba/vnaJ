/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.device;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import krause.common.exception.InitializationException;
import krause.common.validation.ValidationResult;
import krause.common.validation.ValidationResult.ValidationType;
import krause.common.validation.ValidationResults;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAScanMode;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;
import krause.vna.gui.calibrate.mode1.VNACalibrationRangeComparator;
import krause.vna.resources.VNAMessages;

/**
 * A generic base driver for all VNA subclasses.
 * 
 */
public abstract class VNAGenericDriver implements IVNADriver {
	public static final int MINIMUM_SCAN_POINTS = 1000;

	public static final int MAXIMUM_SCAN_POINTS = 30000;

	protected VNAConfig config = VNAConfig.getSingleton();

	private VNADeviceInfoBlock deviceInfoBlock = null;

	private IVNADriverMathHelper mathHelper = null;
	private String portname = null;

	/**
	 * 
	 * @return
	 */
	protected String generateScanRangeFilename() {
		// build filename
		String fName = VNAConfig.getSingleton().getPresetsDirectory();
		fName += "/CalRanges_";
		fName += getDeviceInfoBlock().getShortName();
		fName += ".txt";

		return fName;
	}

	@Override
	public final VNACalibrationRange[] getCalibrationRanges() {
		VNACalibrationRange[] rc = null;
		TraceHelper.entry(this, "getCalibrationRanges");

		// try to load the scan ranges from external file
		rc = loadCalibrationRanges();

		if (rc == null) {
			rc = getSpecificCalibrationRanges();
		}
		saveCalibrationRanges(rc);
		TraceHelper.entry(this, "getCalibrationRanges");
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#getDefaultMode()
	 */
	public VNAScanMode getDefaultMode() {
		return VNAScanMode.MODE_REFLECTION;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#getDeviceFirmwareInfo()
	 */
	public String getDeviceFirmwareInfo() {
		final String methodName = "getDeviceFirmwareInfo";
		TraceHelper.entry(this, methodName);
		TraceHelper.exitWithRC(this, methodName, "null");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#getDeviceInfoBlock()
	 */
	public VNADeviceInfoBlock getDeviceInfoBlock() {
		return deviceInfoBlock;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#getDeviceSupply()
	 */
	public Double getDeviceSupply() {
		final String methodName = "getDevicePowerStatus";
		TraceHelper.entry(this, methodName);
		TraceHelper.exitWithRC(this, methodName, "null");
		return null;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#getDeviceTemperature()
	 */
	public Double getDeviceTemperature() {
		final String methodName = "getDeviceTemperature";
		TraceHelper.entry(this, methodName);
		TraceHelper.exitWithRC(this, methodName, "null");
		return null;
	}

	public final IVNADriverMathHelper getMathHelper() {
		return mathHelper;
	}

	/**
	 * @return the portname
	 */
	public String getPortname() {
		return portname;
	}

	@Override
	public void init() throws InitializationException {
		TraceHelper.entry(this, "init");
		setPortname(config.getPortName(this));
		TraceHelper.exit(this, "init");
	}

	/**
	 * 
	 * @return
	 */
	protected VNACalibrationRange[] loadCalibrationRanges() {
		final String methodeName = "loadCalibrationRanges";
		VNACalibrationRange[] rc = null;
		TraceHelper.entry(this, methodeName);

		// try to load the scan ranges from external file
		long min = getDeviceInfoBlock().getMinFrequency();
		long max = getDeviceInfoBlock().getMaxFrequency();

		// build filename
		String fName = generateScanRangeFilename();

		FileInputStream fstream = null;
		DataInputStream dis = null;
		BufferedReader br = null;
		List<VNACalibrationRange> listRanges = new ArrayList<VNACalibrationRange>();

		try {
			// try to open file
			fstream = new FileInputStream(fName);
			// Get the object of DataInputStream
			dis = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(dis));
			String line;
			// Read File Line By Line
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0) {
					String[] parts = line.split("[\t ]");
					if (parts.length == 4) {
						long start = Long.parseLong(parts[0]);
						long stop = Long.parseLong(parts[1]);
						int steps = Integer.parseInt(parts[2]);
						int overscans = Integer.parseInt(parts[3]);
						VNACalibrationRange sr = new VNACalibrationRange(start, stop, steps, overscans);
						listRanges.add(sr);
					} else {
						ErrorLogHelper.text(this, methodeName, "Line [" + line + "] ignored");
					}
				} else {
					ErrorLogHelper.text(this, methodeName, "Empty line ignored");
				}
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			ErrorLogHelper.exception(this, methodeName, e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					ErrorLogHelper.exception(this, methodeName, e);
				}
			}
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
					ErrorLogHelper.exception(this, methodeName, e);
				}
			}
			if (fstream != null) {
				try {
					fstream.close();
				} catch (IOException e) {
					ErrorLogHelper.exception(this, methodeName, e);
				}
			}
		}
		TraceHelper.text(this, methodeName, "File read");

		Collections.sort(listRanges, new VNACalibrationRangeComparator());

		TraceHelper.text(this, methodeName, "Ranges sorted");

		boolean ok = true;

		if (listRanges.size() > 0) {
			if (listRanges.get(0).getStart() != min) {
				ErrorLogHelper.text(this, methodeName, "First range must start at [" + min + "]");
				ok = false;
			}
			if (listRanges.get(listRanges.size() - 1).getStop() != max) {
				ErrorLogHelper.text(this, methodeName, "Last range must end at [" + max + "]");
				ok = false;
			}

			for (int i = 1; i < listRanges.size(); ++i) {
				VNAScanRange asr = listRanges.get(i);
				// check inside
				if (!((asr.getStart() >= min) && (asr.getStart() < asr.getStop()) && (asr.getStop() <= max) && (asr.getNumScanPoints() >= 10) && (asr.getNumScanPoints() <= MAXIMUM_SCAN_POINTS))) {
					ErrorLogHelper.text(this, methodeName, "Range [" + asr + "] not valid");
					ok = false;
				}

				// check again previous
				VNAScanRange prevSr = listRanges.get(i - 1);
				if (prevSr.getStop() + 1 != asr.getStart()) {
					ErrorLogHelper.text(this, methodeName, "Range [" + prevSr + "] and [" + asr + "] are not not consecutively");
					ok = false;
				}
			}
		}

		TraceHelper.text(this, methodeName, "Validation result=" + ok);

		if (ok && listRanges.size() > 0) {
			rc = listRanges.toArray(new VNACalibrationRange[listRanges.size()]);
		} else {
			File f = new File(fName);
			String fNameNew = fName + ".bak";
			f.renameTo(new File(fNameNew));
			ErrorLogHelper.text(this, methodeName, "Old rangefile renamed from [" + fName + "] to [" + fNameNew + "]");
		}
		TraceHelper.entry(this, methodeName);
		return rc;
	}

	/**
	 * 
	 * @param ranges
	 */
	public void saveCalibrationRanges(VNACalibrationRange[] ranges) {
		TraceHelper.entry(this, "saveCalibrationRanges");
		BufferedWriter bw = null;
		FileWriter fw = null;
		File fi = null;
		try {
			fi = new File(generateScanRangeFilename());
			fw = new FileWriter(fi, false);
			bw = new BufferedWriter(fw);
			for (int i = 0; i < ranges.length; ++i) {
				bw.write("" + ranges[i].getStart());
				bw.write(" ");
				bw.write("" + ranges[i].getStop());
				bw.write(" ");
				bw.write("" + ranges[i].getNumScanPoints());
				bw.write(" ");
				bw.write("" + ranges[i].getNumOverScans());
				bw.newLine();
			}
		} catch (Exception e) {

		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					ErrorLogHelper.exception(this, "saveCalibrationRanges", e);
				}
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					ErrorLogHelper.exception(this, "saveCalibrationRanges", e);
				}
			}
		}
		TraceHelper.entry(this, "saveCalibrationRanges");
	}

	public void setDeviceInfoBlock(VNADeviceInfoBlock deviceInfoBlock) {
		this.deviceInfoBlock = deviceInfoBlock;
	}

	public void setMathHelper(IVNADriverMathHelper mathHelper) {
		this.mathHelper = mathHelper;
	}

	/**
	 * @param portname
	 *            the portname to set
	 */
	public void setPortname(String portname) {
		this.portname = portname;
	}

	public ValidationResults validateScanRange(VNAScanRange range) {
		ValidationResults results = new ValidationResults();

		if (range.getStop() - range.getStart() < VNAGenericDriverSymbols.MIN_SCANWIDTH) {
			String msg = VNAMessages.getString("VNAGenericDriver.ScanRange.tooSmall");
			ValidationResult res = new ValidationResult(MessageFormat.format(msg, VNAGenericDriverSymbols.MIN_SCANWIDTH));
			res.setType(ValidationType.ERROR);
			res.setErrorObject(null);
			results.add(res);
		} else {
			int samples = range.getNumScanPoints();

			// calculate number of steps necessary in analyser
			long frequencyStep = (range.getStop() - range.getStart()) / samples;

			// calculate new stop based on start, step and number of samples
			long newStop = range.getStart() + (frequencyStep * samples);
			long newStart = range.getStart();

			// new stop out of reach of device?
			if (newStop > getDeviceInfoBlock().getMaxFrequency()) {
				// yes
				// reset to requested stop
				newStop = range.getStop();

				// now adjust start
				newStart = newStop - (frequencyStep * samples);
			}

			range.setStart(newStart);
			range.setStop(newStop);
		}
		return results;
	}
}
