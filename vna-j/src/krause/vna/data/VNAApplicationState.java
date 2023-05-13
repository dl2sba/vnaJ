package krause.vna.data;

import java.util.ArrayList;
import java.util.List;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.resources.VNAMessages;

public class VNAApplicationState {
	public enum INNERSTATE {
		NEW, UNINITIALIZED, GUIINITIALIZED, DRIVERLOADED, CALIBRATED, READY, RUNNING, TERMINATING
	}

	private INNERSTATE innerState;

	private List<VNAApplicationStateObserver> listeners = new ArrayList<>();

	private VNAMainFrame mainframe;
	private VNADataPool datapool = VNADataPool.getSingleton();

	public VNAApplicationState(VNAMainFrame pMF) {
		mainframe = pMF;
		innerState = INNERSTATE.UNINITIALIZED;
	}

	public void addStateListener(VNAApplicationStateObserver pListener) {
		TraceHelper.entry(this, "addStateListener", pListener.getClass().getName());
		listeners.add(pListener);
		TraceHelper.exit(this, "addStateListener");
	}

	/**
	 * 
	 */
	public void datapoolLoaded() {
		TraceHelper.entry(this, "datapoolLoaded");
		setState(INNERSTATE.NEW);
		TraceHelper.exit(this, "datapoolLoaded");
	}

	public void evtCalibrationLoaded() {
		TraceHelper.entry(this, "evtCalibrationLoaded", "state=" + innerState);

		String drvString = datapool.getDriver().getDeviceInfoBlock().getShortName() + "/" + datapool.getDriver().getPortname();
		mainframe.getStatusBarDriverType().setText(drvString);

		VNACalibrationBlock mcb = datapool.getMainCalibrationBlock();
		if (mcb != null) {
			// update calibration status field
			mainframe.getStatusBarCalibrationStatus().setText(mcb.getNumberOfSteps() + "/" + mcb.getNumberOfOverscans());

			//
			String calFilename = "";

			// is data loaded from file?
			if (mcb.getFile() != null) {
				if (mcb.getComment() != null && !"".equals(mcb.getComment())) {
					calFilename += mcb.getFile().getName() + " (" + mcb.getComment() + ")";
				} else {
					calFilename += mcb.getFile().getName();
				}
			}
			final Double calTemp = mcb.getTemperature();
			if (calTemp != null) {
				calFilename += "   CalTemp: [" + VNAFormatFactory.getTemperatureFormat().format(calTemp) + "°C]";
			}

			if (datapool.getCalibrationKit() != null) {
				calFilename += "   CalKit: [" + datapool.getCalibrationKit().getName() + "]";
			}
			mainframe.getStatusBarCalibrationFilename().setText(calFilename);
		}
		datapool.setCalibratedData(new VNACalibratedSampleBlock(0));

		setState(INNERSTATE.CALIBRATED);
		TraceHelper.exit(this, "evtCalibrationLoaded");
	}

	public void evtCalibrationUnloaded() {
		TraceHelper.entry(this, "evtCalibrationUnloaded", "state=" + innerState);
		setState(INNERSTATE.DRIVERLOADED);

		mainframe.getStatusBarDriverType().setText(datapool.getDriver().getDeviceInfoBlock().getShortName());
		mainframe.getStatusBarCalibrationStatus().setText(VNAMessages.getString("VNAMainFrame.Cal.UNCAL"));
		mainframe.getStatusBarCalibrationFilename().setText(VNAMessages.getString("VNAMainFrame.Cal.NOFILE"));
		TraceHelper.exit(this, "evtCalibrationUnloaded");
	}

	public void evtDriverLoaded() {
		TraceHelper.entry(this, "evtDriverLoaded", "state=" + innerState);
		setState(INNERSTATE.DRIVERLOADED);

		String drvString = datapool.getDriver().getDeviceInfoBlock().getShortName() + "/" + datapool.getDriver().getPortname();
		mainframe.getStatusBarDriverType().setText(drvString);
		mainframe.getStatusBarCalibrationStatus().setText(VNAMessages.getString("VNAMainFrame.Cal.UNCAL"));
		mainframe.getStatusBarCalibrationFilename().setText(VNAMessages.getString("VNAMainFrame.Cal.NOFILE"));

		mainframe.preloadCalibrationBlocks();
		mainframe.changedMode();

		// is already a resized calibration block available?
		if (datapool.getMainCalibrationBlock() != null) {
			// yes
			// switch state
			evtCalibrationLoaded();
		}

		TraceHelper.exit(this, "evtDriverLoaded");
	}

	public void evtDriverUnloaded() {
		TraceHelper.entry(this, "evtDriverUnloaded", "state=" + innerState);
		setState(INNERSTATE.GUIINITIALIZED);
		TraceHelper.exit(this, "evtDriverUnloaded");
	}

	public void evtGUIInitialzed() {
		TraceHelper.entry(this, "evtGUIInitialzed");
		mainframe.getStatusBarDriverType().setText("---");
		mainframe.getStatusBarCalibrationStatus().setText(VNAMessages.getString("VNAMainFrame.Cal.UNCAL"));
		mainframe.getStatusBarCalibrationFilename().setText(VNAMessages.getString("VNAMainFrame.Cal.NOFILE"));

		//
		setState(INNERSTATE.GUIINITIALIZED);

		//
		mainframe.loadDriver();

		TraceHelper.exit(this, "evtGUIInitialzed");
	}

	public void evtMeasureEnded() {
		TraceHelper.entry(this, "evtMeasureEnded", "state=" + innerState);
		setState(INNERSTATE.CALIBRATED);
		TraceHelper.exit(this, "evtMeasureEnded");
	}

	public void evtMeasureStarted() {
		TraceHelper.entry(this, "evtMeasureStarted", "state=" + innerState);
		setState(INNERSTATE.RUNNING);
		TraceHelper.exit(this, "evtMeasureStarted");
	}

	/**
	 * 
	 */
	public void evtScanModeChanged() {
		TraceHelper.entry(this, "evtScanModeChanged");
		mainframe.changedMode();
		TraceHelper.exit(this, "evtScanModeChanged");
	}

	public INNERSTATE getState() {
		return innerState;
	}

	private void publishState(INNERSTATE pOld, INNERSTATE pNew) {
		TraceHelper.entry(this, "publishState", "state=" + innerState);
		for (VNAApplicationStateObserver listener : listeners) {
			listener.changeState(pOld, pNew);
		}
		TraceHelper.exit(this, "publishState");
	}

	public void republishState() {
		for (VNAApplicationStateObserver listener : listeners) {
			listener.changeState(innerState, innerState);
		}
	}

	protected void setState(INNERSTATE pNew) {
		INNERSTATE old = innerState;
		innerState = pNew;
		publishState(old, pNew);
	}
}
