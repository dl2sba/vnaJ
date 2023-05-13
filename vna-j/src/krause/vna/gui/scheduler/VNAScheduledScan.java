package krause.vna.gui.scheduler;

import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.VNABackgroundJob;
import krause.vna.background.VnaBackgroundTask;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.gui.VNAMainFrame;

public class VNAScheduledScan extends Task {
	private VNAMainFrame mainFrame;
	private VNAConfig config = VNAConfig.getSingleton();
	private VNADataPool datapool = VNADataPool.getSingleton();

	private VNASchedulerDialog dialog;

	public VNAScheduledScan(VNAMainFrame pMainFrame, VNASchedulerDialog pDialog) {
		mainFrame = pMainFrame;
		dialog = pDialog;
	}

	@Override
	public void execute(TaskExecutionContext arg0) throws RuntimeException {
		TraceHelper.entry(this, "execute");
		//
		if (datapool.getResizedCalibrationBlock() == null) {
			if (datapool.getMainCalibrationBlock() != null) {
				VNACalibrationBlock newBlock = VNACalibrationBlockHelper.createResizedCalibrationBlock(datapool.getMainCalibrationBlock(), datapool.getFrequencyRange().getStart(), datapool.getFrequencyRange().getStop(), config.getNumberOfSamples());
				TraceHelper.text(this, "execute", "Created new resized calibration block id=" + newBlock.hashCode());
				TraceHelper.text(this, "execute", " start  =" + newBlock.getStartFrequency());
				TraceHelper.text(this, "execute", " end    =" + newBlock.getStopFrequency());
				TraceHelper.text(this, "execute", " samples=" + newBlock.getNumberOfSteps());

				datapool.setResizedCalibrationBlock(newBlock);
			}
		}
		//
		mainFrame.getApplicationState().evtMeasureStarted();

		// create one instance
		VNABackgroundJob job = new VNABackgroundJob();
		job.setNumberOfSamples(config.getNumberOfSamples());
		job.setFrequencyRange(datapool.getFrequencyRange());
		job.setScanMode(datapool.getScanMode());
		job.setSpeedup(1);

		VnaBackgroundTask task = new VnaBackgroundTask(datapool.getDriver());
		task.addJob(job);
		task.setStatusLabel(mainFrame.getStatusBarStatus());
		task.addDataConsumer(dialog);
		task.execute();

		TraceHelper.exit(this, "execute");
	}
}
