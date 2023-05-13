/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.data.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.calibrated.VNACalibrationPoint;
import krause.vna.data.calibrated.VNACalibrationPointHelper;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.device.IVNADriver;

public class VNACalibrationBlockHelper {

	static final VNACalibrationBlockHelper instance = new VNACalibrationBlockHelper();
	static final VNAConfig config = VNAConfig.getSingleton();

	/**
	 * 
	 */
	private VNACalibrationBlockHelper() {
	}

	/**
	 * write the data out to the defined filename
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean save(VNACalibrationBlock block, String myFileName) {
		TraceHelper.entry(instance, "save");
		boolean result = false;
		if (!myFileName.endsWith(".cal")) {
			myFileName = myFileName + ".cal";
		}
		TraceHelper.text(instance, "save", "Trying to write to [" + myFileName + "]");

		try (FileOutputStream fos = new FileOutputStream(myFileName); ObjectOutputStream encoder = new ObjectOutputStream(fos);) {
			// write calibration block
			encoder.writeObject(VNACalibrationBlock.CALIBRATION_FILETYPE_5);
			encoder.writeObject(block.getAnalyserType());
			encoder.writeObject(block.getComment());
			encoder.writeObject(block.getStartFrequency());
			encoder.writeObject(block.getStopFrequency());
			encoder.writeObject(block.getNumberOfSteps());
			encoder.writeObject(block.getNumberOfOverscans());
			encoder.writeObject(block.getScanMode());
			// write calibrations samples
			encoder.writeObject(block.getCalibrationData4Load());
			encoder.writeObject(block.getCalibrationData4Open());
			encoder.writeObject(block.getCalibrationData4Short());
			encoder.writeObject(block.getCalibrationData4Loop());
			//
			block.setFile(new File(myFileName));
			//
			result = true;
		} catch (Exception e) {
			ErrorLogHelper.exception(instance, "save", e);
			ErrorLogHelper.text(instance, "save", e.getMessage());
		}
		TraceHelper.exitWithRC(instance, "save", result);
		return result;
	}

	/**
	 * load the calibration raw data from the file. If set in DIB, remove the switch-points.
	 * 
	 * @param file
	 *            the file to read from
	 * @param driver
	 *            the driver to use for switch-point removal
	 * @return the loaded data
	 * @throws ProcessingException
	 *             thrown if loading fails
	 */
	public static VNACalibrationBlock loadCalibrationRAWData(final File file, final IVNADriver driver) throws ProcessingException {
		final String methodName = "loadCalibrationRAWData";
		TraceHelper.entry(instance, methodName);
		VNACalibrationBlock calBlock = null;
		final String myFileName = file.getAbsolutePath();
		TraceHelper.text(instance, methodName, "Trying to read from [" + myFileName + "]");

		try (FileInputStream fis = new FileInputStream(myFileName); ObjectInputStream decoder = new ObjectInputStream(fis);) {

			//
			calBlock = new VNACalibrationBlock();
			// read header
			readHeader(decoder, calBlock);
			calBlock.setFile(file);
			calBlock.setMathHelper(driver.getMathHelper());

			// read calibration points
			VNASampleBlock blkLoad = (VNASampleBlock) decoder.readObject();
			VNASampleBlock blkOpen = (VNASampleBlock) decoder.readObject();
			VNASampleBlock blkShort = (VNASampleBlock) decoder.readObject();
			VNASampleBlock blkLoop = (VNASampleBlock) decoder.readObject();

			// remove switch points
			if (driver.getDeviceInfoBlock().isPeakSuppression()) {
				long[] switchPoints = driver.getDeviceInfoBlock().getSwitchPoints();

				if (switchPoints != null) {
					// yes
					if (blkLoad != null) {
						VNASampleBlockHelper.removeSwitchPoints(blkLoad, switchPoints);
					}
					if (blkOpen != null) {
						VNASampleBlockHelper.removeSwitchPoints(blkOpen, switchPoints);
					}
					if (blkShort != null) {
						VNASampleBlockHelper.removeSwitchPoints(blkShort, switchPoints);
					}
					if (blkLoop != null) {
						VNASampleBlockHelper.removeSwitchPoints(blkLoop, switchPoints);
					}
				}
			}
			// read calibration points
			calBlock.setCalibrationData4Load(blkLoad);
			calBlock.setCalibrationData4Open(blkOpen);
			calBlock.setCalibrationData4Short(blkShort);
			calBlock.setCalibrationData4Loop(blkLoop);

		} catch (IOException | ClassNotFoundException e) {
			ErrorLogHelper.exception(instance, methodName, e);
			throw new ProcessingException(e);
		}
		TraceHelper.exit(instance, methodName);
		return calBlock;
	}

	/**
	 * Read the data from the previously opened file
	 * 
	 * @param file
	 *            the file to read from
	 * @param driver
	 *            the drivers math helper is added to the cal block
	 * @return the loaded cal block
	 * @throws ProcessingException
	 *             thrown if loading fails
	 */
	public static VNACalibrationBlock load(final File file, final IVNADriver driver, final VNACalibrationKit calKit) throws ProcessingException {
		final String methodName = "load";
		TraceHelper.entry(instance, methodName);

		final VNACalibrationBlock calBlock = loadCalibrationRAWData(file, driver);

		// calculate the average calibration temperature
		calBlock.calculateCalibrationTemperature();

		//
		VNACalibrationContext calContext = calBlock.getMathHelper().createCalibrationContextForCalibrationPoints(calBlock, calKit);
		calBlock.getMathHelper().createCalibrationPoints(calContext, calBlock);

		TraceHelper.exit(instance, methodName);
		return calBlock;
	}

	/**
	 * read the relevant header fields from the given stream
	 * 
	 * @param decoder
	 * @param rc
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static void readHeader(ObjectInputStream decoder, VNACalibrationBlock rc) throws IOException, ClassNotFoundException {
		TraceHelper.entry(instance, "readHeader");
		// read header
		String at = (String) decoder.readObject();

		if (VNACalibrationBlock.CALIBRATION_FILETYPE_5.equals(at) || VNACalibrationBlock.CALIBRATION_FILETYPE_4.equals(at)) {
			TraceHelper.text(instance, "readHeader", "New record type [" + at + "] detected");
			rc.setAnalyserType((String) decoder.readObject());
			rc.setComment((String) decoder.readObject());
			rc.setStartFrequency((Long) decoder.readObject());
			rc.setStopFrequency((Long) decoder.readObject());
			rc.setNumberOfSteps((Integer) decoder.readObject());
			rc.setNumberOfOverscans((Integer) decoder.readObject());
			rc.setScanMode((VNAScanMode) decoder.readObject());
		} else if (VNACalibrationBlock.CALIBRATION_FILETYPE_3.equals(at)) {
			TraceHelper.text(instance, "readHeader", "New record type [" + at + "] detected");
			rc.setAnalyserType((String) decoder.readObject());
			rc.setComment("");
			rc.setStartFrequency((Long) decoder.readObject());
			rc.setStopFrequency((Long) decoder.readObject());
			rc.setNumberOfSteps((Integer) decoder.readObject());
			rc.setNumberOfOverscans((Integer) decoder.readObject());
			rc.setScanMode((VNAScanMode) decoder.readObject());
		} else if (VNACalibrationBlock.CALIBRATION_FILETYPE_2.equals(at)) {
			TraceHelper.text(instance, "readHeader", "New record type [" + at + "] detected");
			rc.setAnalyserType((String) decoder.readObject());
			rc.setComment("");
			rc.setStartFrequency((Integer) decoder.readObject());
			rc.setStopFrequency((Integer) decoder.readObject());
			rc.setNumberOfSteps((Integer) decoder.readObject());
			rc.setNumberOfOverscans((Integer) decoder.readObject());
			rc.setScanMode((VNAScanMode) decoder.readObject());
		} else {
			TraceHelper.text(instance, "readHeader", "Old record type [" + at + "] detected");
			rc.setAnalyserType(at);
			rc.setComment("");
			rc.setStartFrequency((Integer) decoder.readObject());
			rc.setStopFrequency((Integer) decoder.readObject());
			rc.setNumberOfSteps((Integer) decoder.readObject());
			rc.setNumberOfOverscans(1);
			Boolean b = (Boolean) decoder.readObject();
			rc.setScanMode(Boolean.TRUE.equals(b) ? VNAScanMode.MODE_TRANSMISSION : VNAScanMode.MODE_REFLECTION);
		}
		TraceHelper.exit(instance, "readHeader");
	}

	/**
	 * read the data from the previously defined filename and use readHeader() to get content
	 * 
	 * @param filename
	 * @return
	 * @throws ProcessingException
	 */
	public static VNACalibrationBlock loadHeader(File file) throws ProcessingException {
		TraceHelper.entry(instance, "loadHeader");
		VNACalibrationBlock rc = null;
		String myFileName = file.getAbsolutePath();

		TraceHelper.text(instance, "loadHeader", "Trying to read header from [" + myFileName + "]");

		try (FileInputStream fis = new FileInputStream(myFileName); ObjectInputStream decoder = new ObjectInputStream(fis);) {
			//
			rc = new VNACalibrationBlock();
			// read header
			readHeader(decoder, rc);

			// set reference to input
			rc.setFile(file);
		} catch (Exception e) {
			ErrorLogHelper.exception(instance, "loadHeader", e);
			throw new ProcessingException(e);
		}
		TraceHelper.exit(instance, "loadHeader");
		return rc;
	}

	/**
	 * copy the calibration points from the full blown main calibration block into a resized calibration block
	 * 
	 * @param pMainCalibrationBlock
	 *            the source
	 * @param pStartFreq
	 *            the start frequency for the new block
	 * @param pStopFreq
	 *            the stop frequency for the new block
	 * @param targetSteps
	 *            the number of steps for the new block
	 * @return
	 */
	public static VNACalibrationBlock createResizedCalibrationBlock(final VNACalibrationBlock pMainCalibrationBlock, final long pStartFreq, final long pStopFreq, final int targetSteps) {
		final String methodName = "createResizedCalibrationBlock";
		TraceHelper.entry(instance, methodName);
		TraceHelper.text(instance, methodName, "start=%10d", pStartFreq);
		TraceHelper.text(instance, methodName, "stop =%10d", pStopFreq);
		TraceHelper.text(instance, methodName, "steps=%d", targetSteps);

		// create new block
		final VNACalibrationBlock rc = new VNACalibrationBlock();

		// check matching data
		if (pStartFreq < pMainCalibrationBlock.getStartFrequency()) {
			ErrorLogHelper.text(instance, methodName, "frequency [%d] too low for calibration source [%d]", pStartFreq, pMainCalibrationBlock.getStartFrequency());
			return rc;
		}
		if (pStopFreq > pMainCalibrationBlock.getStopFrequency()) {
			ErrorLogHelper.text(instance, methodName, "frequency [%d] too low for calibration source [%d]", pStopFreq, pMainCalibrationBlock.getStopFrequency());
			return rc;
		}

		// with required data
		rc.setStartFrequency(pStartFreq);
		rc.setStopFrequency(pStopFreq);
		rc.setNumberOfSteps(targetSteps);
		rc.setAnalyserType(pMainCalibrationBlock.getAnalyserType());
		rc.setMathHelper(pMainCalibrationBlock.getMathHelper());
		rc.setScanMode(pMainCalibrationBlock.getScanMode());
		rc.setTemperature(pMainCalibrationBlock.getTemperature());
		//
		final VNACalibrationPoint[] source = pMainCalibrationBlock.getCalibrationPoints();
		final VNACalibrationPoint[] target = new VNACalibrationPoint[targetSteps];

		long freqStep = (pStopFreq - pStartFreq) / targetSteps;
		TraceHelper.text(instance, methodName, "freq step=%d", freqStep);

		// now for each target step
		long targetFreq = pStartFreq;
		int sourceIndex = 0;
		int sourceSteps = source.length;
		for (int targetIndex = 0; targetIndex < targetSteps; ++targetIndex) {
			// now search a source point
			// but only between last found source index and the one greater
			// targetFrequency
			while ((sourceIndex < sourceSteps) && (source[sourceIndex].getFrequency() < targetFreq)) {
				++sourceIndex;
			}

			// check for end condition
			if (sourceIndex >= sourceSteps) {
				sourceIndex = sourceSteps - 1;
			}

			// do we have an exact match?
			if (source[sourceIndex].getFrequency() == targetFreq) {
				// yes so we can copy source to target block
				target[targetIndex] = source[sourceIndex];
				// TraceHelper.text(instance, methodName, "assign %4d > %-4d exact frequency match", sourceIndex, targetIndex)
			} else {
				// no
				// f at sourceIndex must be > f
				VNACalibrationPoint p1 = source[sourceIndex - 1];
				VNACalibrationPoint p2 = source[sourceIndex];

				// create interpolated one
				target[targetIndex] = VNACalibrationPointHelper.interpolate(p1, p2, targetFreq);

				// TraceHelper.text(instance, methodName, "assign %4d > %4d %10d > %-10d interpolated", sourceIndex, targetIndex,
				// source[sourceIndex].getFrequency(), targetFreq)
			}
			targetFreq += freqStep;
		}

		// all done
		rc.setCalibrationPoints(target);

		TraceHelper.exit(instance, methodName);
		return rc;
	}
}
