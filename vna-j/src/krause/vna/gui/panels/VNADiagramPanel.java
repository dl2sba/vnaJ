/**
 * Copyright (C) 2007 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.panels;

import java.awt.BorderLayout;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.VNABackgroundJob;
import krause.vna.config.VNAConfig;
import krause.vna.data.IVNADataConsumer;
import krause.vna.data.VNAApplicationState.INNERSTATE;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.helper.VNASampleBlockHelper;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.export.CSVExporter;
import krause.vna.export.JpegExporter;
import krause.vna.export.PDFExporter;
import krause.vna.export.SnPExporter;
import krause.vna.export.VNAExporter;
import krause.vna.export.XLSExporter;
import krause.vna.export.XLSRawExporter;
import krause.vna.export.XMLExporter;
import krause.vna.export.ZPlotsExporter;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.panels.marker.VNAMarker;
import krause.vna.gui.scale.VNAFrequencyScale;
import krause.vna.gui.scale.VNAGenericScale;
import krause.vna.gui.scale.VNAMeasurementScale;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;
import krause.vna.resources.VNAMessages;

public class VNADiagramPanel extends JPanel implements IVNADataConsumer, VNAApplicationStateObserver {

	private transient VNAMainFrame mainFrame;
	private VNAFrequencyScale frequencyScale = null;
	private VNAMeasurementScale scaleLeft = null;
	private VNAMeasurementScale scaleRight = null;
	private VNAImagePanel imagePanel = null;
	private VNAScaleSelectPanel scaleSelectPanel = null;
	private VNAConfig config = VNAConfig.getSingleton();
	private final transient VNADataPool datapool = VNADataPool.getSingleton();

	public VNADiagramPanel(VNAMainFrame pMainFrame) {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		TraceHelper.entry(this, "VNADiagramPanel");
		mainFrame = pMainFrame;
		//
		setLayout(new BorderLayout());
		//
		imagePanel = new VNAImagePanel(mainFrame);
		add(imagePanel, BorderLayout.CENTER);
		//
		scaleLeft = new VNAMeasurementScale(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_RETURNLOSS), true, mainFrame.getJFrame());
		add(scaleLeft, BorderLayout.WEST);
		//
		scaleRight = new VNAMeasurementScale(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_RETURNPHASE), false, mainFrame.getJFrame());
		add(scaleRight, BorderLayout.EAST);
		//
		frequencyScale = new VNAFrequencyScale(scaleLeft, scaleRight);
		add(frequencyScale, BorderLayout.PAGE_END);
		//
		scaleSelectPanel = new VNAScaleSelectPanel(mainFrame, scaleLeft, scaleRight);
		add(scaleSelectPanel, BorderLayout.PAGE_START);
		//
		TraceHelper.exit(this, "VNADiagramPanel");
	}

	public void setupColors() {
		scaleSelectPanel.setupColors();
		scaleLeft.setupColors();
		scaleRight.setupColors();
		imagePanel.repaint();
	}

	/**
	 * @return Returns the frequencyScale.
	 */
	public VNAFrequencyScale getScaleFrequency() {
		return frequencyScale;
	}

	/**
	 * @return Returns the scaleLeft.
	 */
	public VNAMeasurementScale getScaleLeft() {
		return scaleLeft;
	}

	/**
	 * @return Returns the scaleRight.
	 */
	public VNAMeasurementScale getScaleRight() {
		return scaleRight;
	}

	/**
	 * @return Returns the innerImage.
	 */
	public VNAImagePanel getImagePanel() {
		return imagePanel;
	}

	/**
	 * Consume the scan data. The received raw block is added to the end of list of raw blocks. If the list size exceeds
	 * job.average() the first raw block is removed from the list. Then the average over all stored raw blocks is calculated.
	 * 
	 * The average of the raw blocks is then used for calculation of the calibrated data
	 * 
	 * @see krause.vna.data.IVNADataConsumer#consumeDataBlock(java.util.List)
	 **/
	public void consumeDataBlock(List<VNABackgroundJob> jobs) {
		final String method = "consumeDataBlock";
		TraceHelper.entry(this, method);

		// jobs in call data?
		if (jobs != null) {
			// yes
			// only first job is relevant
			VNABackgroundJob job = jobs.get(0);

			// first job set?=
			if (job != null) {
				// yes

				// all previously received raw blocks
				List<VNASampleBlock> rawBlocks = datapool.getRawDataBlocks();
				TraceHelper.text(this, method, "previously %d raw block(s) in datapool", rawBlocks.size());

				// get result of job
				final VNASampleBlock result = job.getResult();
				final VNADeviceInfoBlock dib = datapool.getDriver().getDeviceInfoBlock();

				// data in job?
				if (result == null) {
					// no
					ErrorLogHelper.text(this, method, "result in job is empty");
					TraceHelper.exit(this, method);
					return;
				}

				// check if we must remove switch points
				if (dib.isPeakSuppression()) {
					// yes remove the spikes
					VNASampleBlockHelper.removeSwitchPoints(result, dib.getSwitchPoints());
				}

				// do we already have data collected?
				// has the new block a different size?
				if (!rawBlocks.isEmpty() && (rawBlocks.get(0).getNumberOfSteps() != result.getNumberOfSteps())) {
					// yes
					// remove all currently stored blocks.
					rawBlocks.clear();
				}

				// add new scan to list of raw scans
				rawBlocks.add(result);

				// more blocks than average specifies?
				while (rawBlocks.size() > job.getAverage()) {
					// yes
					// reduce by one
					rawBlocks.remove(0);
					TraceHelper.text(this, method, "removed first element from buffer to match average size");
				}

				// less blocks than average specifies?
				if (rawBlocks.size() < job.getAverage()) {
					while (rawBlocks.size() < job.getAverage()) {
						// yes
						// reduce by one
						rawBlocks.add(result);
						TraceHelper.text(this, method, "added block to end of buffer to fill up average buffer");
					}
				}

				TraceHelper.text(this, method, "now %d raw block(s) in datapool", rawBlocks.size());

				// calculate the average of all previous scans
				VNASampleBlock data = VNASampleBlockHelper.calculateAverageSampleBlock(rawBlocks);

				// store in global pool
				datapool.setRawData(data);

				// now process the whole bunch of data
				processRawData();
			} else {
				ErrorLogHelper.text(this, method, "job is empty");
			}
		} else

		{
			ErrorLogHelper.text(this, method, "no jobs returned");
		}

		// repaint diagram
		repaint();

		TraceHelper.exit(this, method);

	}

	/**
	 * process the received raw data and display the stuff
	 * 
	 **/
	public void processRawData() {
		TraceHelper.entry(this, "processRawData");

		final VNASampleBlock data = datapool.getRawData();

		// data present?
		if (data != null) {
			// yes
			if (config.isExportRawData()) {
				XLSRawExporter.export(data, "RawData");
			}

			final IVNADriverMathHelper mathHelper = data.getMathHelper();
			// math helper set?
			if (mathHelper != null) {
				// yes
				// apply filtering to raw IQ data
				mathHelper.applyFilter(data.getSamples());

				// also a calibration block available?
				final VNACalibrationBlock calBlock = datapool.getResizedCalibrationBlock();
				if (calBlock != null) {
					// yes
					final VNACalibrationContext context = mathHelper.createCalibrationContextForCalibratedSamples(calBlock);
					context.setConversionTemperature(data.getDeviceTemperature());

					final VNACalibratedSampleBlock calSamples = mathHelper.createCalibratedSamples(context, data);
					datapool.setCalibratedData(calSamples);

					if (config.isExportRawData()) {
						XLSRawExporter.export(calBlock.getCalibrationPoints(), "CalibrationData");
						XLSRawExporter.export(calSamples.getCalibratedSamples(), "calibratedSamples");
					}

					//
					updateMarkerPanel();

					// rescale the vertical scales
					if (config.isAutoscaleEnabled()) {
						rescaleScalesToData();
					}
					//
					handleAutoExport();

					// show memory usage
					showMemoryUsage();

					//
					if (scaleSelectPanel != null && scaleSelectPanel.getSmithDialog() != null) {
						scaleSelectPanel.getSmithDialog().consumeCalibratedData(datapool.getCalibratedData());
					}
				}
			}
		}

		// repaint diagram
		repaint();

		TraceHelper.exit(this, "processRawData");
	}

	private void showMemoryUsage() {

		final long total = Runtime.getRuntime().totalMemory();
		final long free = Runtime.getRuntime().freeMemory();

		final String msg = VNAFormatFactory.formatMemoryInMiB(free) + "MiB/" + VNAFormatFactory.formatMemoryInMiB(total) + "MiB";
		scaleSelectPanel.getLabelDebug().setText(msg);
	}

	/**
	 * 
	 */
	private void handleAutoExport() {
		TraceHelper.entry(this, "handleAutoExport");
		String filename = null;
		try {
			if (config.getAutoExportFormat() == 1) {
				filename = internalAutoExport(new CSVExporter(mainFrame));
			}
			if (config.getAutoExportFormat() == 2) {
				filename = internalAutoExport(new JpegExporter(mainFrame));
			}
			if (config.getAutoExportFormat() == 3) {
				filename = internalAutoExport(new PDFExporter(mainFrame));
			}
			if (config.getAutoExportFormat() == 4) {
				filename = internalAutoExport(new SnPExporter(mainFrame));
			}
			if (config.getAutoExportFormat() == 5) {
				filename = internalAutoExport(new XLSExporter(mainFrame));
			}
			if (config.getAutoExportFormat() == 6) {
				filename = internalAutoExport(new XMLExporter(mainFrame));
			}
			if (config.getAutoExportFormat() == 7) {
				filename = internalAutoExport(new ZPlotsExporter(mainFrame));
			}
		} catch (ProcessingException e) {
			JOptionPane.showMessageDialog(mainFrame.getJFrame(), MessageFormat.format(VNAMessages.getString("Message.Export.5"), e.getMessage()), VNAMessages.getString("Message.Export.6"), JOptionPane.ERROR_MESSAGE);
		}
		TraceHelper.exitWithRC(this, "handleAutoExport", filename);

	}

	/**
	 * @param csvExporter
	 * @return
	 * @throws ProcessingException
	 */
	private String internalAutoExport(VNAExporter exporter) throws ProcessingException {
		TraceHelper.entry(this, "internalAutoExport");
		String fnp = config.getAutoExportDirectory() + System.getProperty("file.separator") + config.getAutoExportFilename();
		String filename = exporter.export(fnp, true);
		TraceHelper.exit(this, "internalAutoExport");
		return filename;
	}

	private void updateMarkerPanel() {
		TraceHelper.entry(this, "updateMarkerPanel");
		VNACalibratedSampleBlock cd = datapool.getCalibratedData();

		for (VNAMarker mark : mainFrame.getMarkerPanel().getMarkers()) {
			if (mark.isVisible()) {
				mark.moveMarkerToData(cd);
			}
		}
		TraceHelper.exit(this, "updateMarkerPanel");
	}

	/**
	 * 
	 */
	public void rescaleScalesToData() {
		TraceHelper.entry(this, "rescaleScales");

		VNACalibratedSampleBlock currentData = datapool.getCalibratedData();

		if (currentData != null) {

			HashMap<SCALE_TYPE, VNAGenericScale> mst = VNAScaleSymbols.MAP_SCALE_TYPES;

			for (VNAGenericScale aScale : mst.values()) {
				if (aScale.getType() != SCALE_TYPE.SCALE_NONE) {
					aScale.setCurrentMinMaxValue(currentData.getMinMaxPair(aScale.getType()));
					aScale.rescale();
				}
			}
		}
		TraceHelper.exit(this, "rescaleScales");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.state.IVNAApplicationStateListener#stateChanged(krause.vna .state.VNAApplicationState.INNERSTATE,
	 * krause.vna.state.VNAApplicationState.INNERSTATE)
	 */
	public void changeState(INNERSTATE oldState, INNERSTATE newState) {
		if (newState == INNERSTATE.DRIVERLOADED) {
			if ((oldState == INNERSTATE.DRIVERLOADED) || (oldState == INNERSTATE.GUIINITIALIZED)) {
				VNADeviceInfoBlock dib = datapool.getDriver().getDeviceInfoBlock();

				// recalculate the values of all scales
				for (VNAGenericScale scale : VNAScaleSymbols.MAP_SCALE_TYPES.values()) {
					scale.initScaleFromConfigOrDib(dib, config);
				}
				repaint();
			}
			if (oldState == INNERSTATE.CALIBRATED) {
				// clear the calibrated data
				datapool.setCalibratedData(new VNACalibratedSampleBlock(0));

				// force repaint of diagramm
				repaint();
			}
		} else if (newState == INNERSTATE.CALIBRATED) {
			// noop
		} else if (newState == INNERSTATE.RUNNING) {
			// noop
		} else {
			// noop
		}

		scaleSelectPanel.changeState(oldState, newState);
		imagePanel.changeState(oldState, newState);
	}

	public VNAScaleSelectPanel getScaleSelectPanel() {
		return scaleSelectPanel;
	}

	public void setScaleSelectPanel(VNAScaleSelectPanel scaleSelectPanel) {
		this.scaleSelectPanel = scaleSelectPanel;
	}

	/**
	 * clear all diagram data and repaint the diagram area
	 */
	public void clearScanData() {
		datapool.setRawData(null);
		datapool.setCalibratedData(null);
		repaint();
	}
}
