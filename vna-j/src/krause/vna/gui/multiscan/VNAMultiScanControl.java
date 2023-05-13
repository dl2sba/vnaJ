package krause.vna.gui.multiscan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.VNABackgroundJob;
import krause.vna.background.VnaBackgroundTask;
import krause.vna.config.VNAConfig;
import krause.vna.data.IVNADataConsumer;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.device.VNAScanRange;
import krause.vna.gui.input.FrequencyInputField;
import krause.vna.gui.panels.data.table.VNAEditableFrequencyPairTable;
import krause.vna.gui.util.SwingUtil;
import krause.vna.gui.util.VNAFrequencyPair;
import krause.vna.resources.VNAMessages;

public class VNAMultiScanControl extends JInternalFrame implements IVNADataConsumer, ActionListener {

	private VNAConfig config = VNAConfig.getSingleton();
	private VNADataPool datapool = VNADataPool.getSingleton();

	private VNAMultiScanWindow mainWindow;
	private List<VNAMultiScanResult> results = new ArrayList<VNAMultiScanResult>();
	private JLabel lblStatus;
	private FrequencyInputField txtStart;
	private FrequencyInputField txtStop;
	private VNAEditableFrequencyPairTable tblFrequencies = null;
	private JCheckBox cbFreeRun;
	private JButton btnScan;

	public VNAMultiScanControl(VNAMultiScanWindow pMainWindow) {
		super("Control", true, false, false, true);

		mainWindow = pMainWindow;

		//
		if (datapool.getResizedCalibrationBlock() == null) {
			if (datapool.getMainCalibrationBlock() != null) {
				VNACalibrationBlock oldBlock = datapool.getResizedCalibrationBlock();
				if (oldBlock != null) {
					TraceHelper.text(this, "recalcCalibrationBlock", "OLD id=" + oldBlock.hashCode());
					TraceHelper.text(this, "recalcCalibrationBlock", " start  =" + oldBlock.getStartFrequency());
					TraceHelper.text(this, "recalcCalibrationBlock", " end    =" + oldBlock.getStopFrequency());
					TraceHelper.text(this, "recalcCalibrationBlock", " samples=" + oldBlock.getNumberOfSteps());
				}

				//
				VNACalibrationBlock newBlock = VNACalibrationBlockHelper.createResizedCalibrationBlock(datapool.getMainCalibrationBlock(), datapool.getFrequencyRange().getStart(), datapool.getFrequencyRange().getStop(), config.getNumberOfSamples());
				TraceHelper.text(this, "recalcCalibrationBlock", "NEW id=" + newBlock.hashCode());
				TraceHelper.text(this, "recalcCalibrationBlock", " start  =" + newBlock.getStartFrequency());
				TraceHelper.text(this, "recalcCalibrationBlock", " end    =" + newBlock.getStopFrequency());
				TraceHelper.text(this, "recalcCalibrationBlock", " samples=" + newBlock.getNumberOfSteps());

				datapool.setResizedCalibrationBlock(newBlock);
			}
		}

		long minFrq = datapool.getResizedCalibrationBlock().getStartFrequency();
		long maxFrq = datapool.getResizedCalibrationBlock().getStopFrequency();

		setLocation(0, 0);
		setSize(350, 300);
		setBackground(Color.GREEN);

		lblStatus = new JLabel("");
		getContentPane().add(lblStatus, BorderLayout.SOUTH);

		JPanel pnlAction = new JPanel();
		getContentPane().add(pnlAction, BorderLayout.NORTH);

		btnScan = SwingUtil.createJButton("Panel.Data.ButtonScan", this);
		pnlAction.add(btnScan);

		cbFreeRun = SwingUtil.createJCheckbox("Panel.Data.ButtonFree", this);
		pnlAction.add(cbFreeRun);

		JPanel pnlRESULT = new JPanel();
		getContentPane().add(pnlRESULT, BorderLayout.CENTER);
		pnlRESULT.setLayout(new BorderLayout(0, 0));

		JPanel pnlBTRES = new JPanel();
		pnlRESULT.add(pnlBTRES, BorderLayout.SOUTH);

		JPanel pnlADD = new JPanel();
		pnlBTRES.add(pnlADD);

		JLabel lblStart = new JLabel("Start");
		pnlADD.add(lblStart);

		txtStart = new FrequencyInputField("start", datapool.getDriver().getDeviceInfoBlock().getMinFrequency(), minFrq, maxFrq);
		pnlADD.add(txtStart);
		txtStart.setColumns(10);

		JLabel lblStop = new JLabel("Stop");
		pnlADD.add(lblStop);

		txtStop = new FrequencyInputField("stop", datapool.getDriver().getDeviceInfoBlock().getMaxFrequency(), minFrq, maxFrq);
		pnlADD.add(txtStop);
		txtStop.setColumns(10);

		pnlRESULT.add(createListbox(), BorderLayout.CENTER);

		loadListbox();

		moveToFront();
		setVisible(true);

		doInit();
	}

	private void doInit() {
		TraceHelper.entry(this, "doInit");
		txtStart.setFrequency(config.getInteger("MultiTune.Control.Start", 1000000));
		txtStop.setFrequency(config.getInteger("MultiTune.Control.Stop", 30000000));
		config.restoreWindowPosition("MultiTune.Control", this, new Point(0, 0));
		config.restoreWindowSize("MultiTune.Control", this, new Dimension(300, 300));
		TraceHelper.exit(this, "doInit");
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		TraceHelper.entry(this, "actionPerformed", cmd);
		if (e.getSource() == cbFreeRun) {
			if (cbFreeRun.isSelected()) {
				btnScan.setEnabled(false);
				// trigger a single shot
				doSingleScan();
			} else {
				cbFreeRun.setSelected(false);
				btnScan.setEnabled(true);
			}
		} else if (e.getSource() == btnScan) {
			doSingleScan();
		} else if (e.getSource() == tblFrequencies) {
			if (cmd.equals("ADD")) {
				doAddRange();
			} else if (cmd.startsWith("DEL")) {
				int i1 = cmd.indexOf(';');
				int i2 = cmd.indexOf(';', i1 + 1);
				long start = Long.parseLong(cmd.substring(i1 + 1, i2));
				long stop = Long.parseLong(cmd.substring(i2 + 1));
				doRemoveRange(start, stop);
			}
		}
		TraceHelper.exit(this, "actionPerformed");
	}

	/**
	 * 
	 */
	public void consumeDataBlock(List<VNABackgroundJob> jobs) {
		TraceHelper.entry(this, "consumeDataBlock");
		// for each job == window
		for (VNABackgroundJob job : jobs) {
			// right job type?
			if (job instanceof VNAMultiScanBackgroundJob) {
				// yes
				// get the data
				final VNAMultiScanBackgroundJob msJob = (VNAMultiScanBackgroundJob) job;

				// and pump it to the correct window
				msJob.getResultWindow().consumeSampleBlock(job.getResult());
			}
		}
		// running in free run mode?
		if (cbFreeRun.isSelected()) {
			// yes
			// than trigger the next scan
			doSingleScan();
		}
		TraceHelper.exit(this, "consumeDataBlock");
	}

	/**
	 * @return
	 */
	private JComponent createListbox() {
		JScrollPane rc = null;
		TraceHelper.entry(this, "createListbox");
		//
		tblFrequencies = new VNAEditableFrequencyPairTable();
		tblFrequencies.getButtonUse().setVisible(false);
		tblFrequencies.addActionListener(this);
		//
		rc = new JScrollPane(tblFrequencies);
		rc.setPreferredSize(new Dimension(200, 300));
		rc.setMinimumSize(rc.getPreferredSize());
		rc.setAlignmentX(LEFT_ALIGNMENT);
		//
		TraceHelper.exit(this, "createListbox");
		return rc;
	}

	@Override
	public void dispose() {
		TraceHelper.entry(this, "dispose");
		cbFreeRun.setSelected(false);

		config.storeWindowPosition("MultiTune.Control", this);
		config.storeWindowSize("MultiTune.Control", this);

		saveListbox();

		for (VNAMultiScanResult result : results) {
			result.dispose();
		}
		super.dispose();
		TraceHelper.exit(this, "dispose");
	}

	protected void doAddRange() {
		TraceHelper.entry(this, "doAddRange");

		long start = txtStart.getFrequency();
		long stop = txtStop.getFrequency();
		VNAScanRange range = new VNAScanRange(start, stop, config.getNumberOfSamples());

		ValidationResults valRes = datapool.getDriver().validateScanRange(range);

		if (valRes.isEmpty()) {
			VNAMultiScanResult result = new VNAMultiScanResult(this, txtStart.getFrequency(), txtStop.getFrequency(), mainWindow.getScale());

			mainWindow.getDesktop().add(result);
			results.add(result);

			tblFrequencies.addFrequency(new VNAFrequencyPair(result.getStartFrequency(), result.getStopFrequency()));
		} else {
			new ValidationResultsDialog(null, valRes, VNAMessages.getString("VNAMultiScanControl.Value.1"));
		}
		TraceHelper.exit(this, "doAddRange");
	}

	private void doRemoveRange(long start, long stop) {
		TraceHelper.entry(this, "doRemoveRange");
		for (VNAMultiScanResult result : results) {
			if ((result.getStartFrequency() == start) && (result.getStopFrequency() == stop)) {
				results.remove(result);
				result.dispose();
				break;
			}
		}
		TraceHelper.exit(this, "doRemoveRange");

	}

	private void doSingleScan() {
		TraceHelper.entry(this, "doSingleScan");

		VnaBackgroundTask backgroundTask = new VnaBackgroundTask(datapool.getDriver());

		for (VNAMultiScanResult result : results) {
			VNABackgroundJob job = new VNAMultiScanBackgroundJob(result);
			backgroundTask.addJob(job);
		}

		backgroundTask.setStatusLabel(lblStatus);
		backgroundTask.addDataConsumer(this);
		backgroundTask.execute();

		TraceHelper.exit(this, "doSingleScan");
	}

	public VNAMultiScanWindow getMainWindow() {
		return mainWindow;
	}

	private void loadListbox() {
		TraceHelper.entry(this, "loadListbox");
		tblFrequencies.load(config.getVNAConfigDirectory() + "/Multiscan.xml");

		int x = 20;
		int y = 20;
		for (VNAFrequencyPair pair : tblFrequencies.getFrequencyPairs()) {
			VNAMultiScanResult result = new VNAMultiScanResult(this, pair.getStartFrequency(), pair.getStopFrequency(), mainWindow.getScale());
			mainWindow.getDesktop().add(result);
			results.add(result);

			result.setLocation(x, y);
			result.moveToFront();
			x += 30;
			y += 30;
		}
		TraceHelper.exit(this, "loadListbox");
	}

	private void saveListbox() {
		TraceHelper.entry(this, "saveListbox");
		tblFrequencies.save(config.getVNAConfigDirectory() + "/Multiscan.xml");
		TraceHelper.exit(this, "saveListbox");
	}
}
