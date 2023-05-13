package krause.vna.gui.scheduler;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

import it.sauronsoftware.cron4j.Scheduler;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.VNABackgroundJob;
import krause.vna.config.VNAConfig;
import krause.vna.data.IVNADataConsumer;
import krause.vna.export.CSVExporter;
import krause.vna.export.JpegExporter;
import krause.vna.export.PDFExporter;
import krause.vna.export.SnPExporter;
import krause.vna.export.VNAExporter;
import krause.vna.export.XLSExporter;
import krause.vna.export.XMLExporter;
import krause.vna.export.ZPlotsExporter;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;

public class VNASchedulerDialog extends KrauseDialog implements IVNADataConsumer {
	private static VNAConfig config = VNAConfig.getSingleton();
	private JTextField txtCron;
	private Scheduler scheduler = new Scheduler();
	private String taskID;
	private VNAMainFrame mainFrame;
	private JButton btnStart;
	private JButton btnStop;
	private JButton btnOK;
	private JCheckBox rdbtnXls;
	private JCheckBox rdbtnCsv;
	private JCheckBox rdbtnPdf;
	private JCheckBox rdbtnJpg;
	private JCheckBox rdbtnXml;
	private JList lstTasks;
	private JCheckBox rdbtnZPlot;
	private JCheckBox rdbtnSParm;

	@SuppressWarnings("unchecked")
	public VNASchedulerDialog(Frame aFrame, VNAMainFrame pMainFrame) {
		super(aFrame, true);
		mainFrame = pMainFrame;
		setTitle(VNAMessages.getString("VNASchedulerDialog.title"));
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, BorderLayout.SOUTH);
		pnlButton.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		pnlButton.add(panel, BorderLayout.EAST);

		btnOK = new JButton(VNAMessages.getString("Button.Close"));
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doDialogCancel();
			}
		});
		panel.add(btnOK);

		JPanel pnlControl = new JPanel();
		pnlButton.add(pnlControl, BorderLayout.WEST);

		btnStart = new JButton(VNAMessages.getString("Button.START"));
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doStart();
			}
		});
		pnlControl.add(btnStart);

		btnStop = new JButton(VNAMessages.getString("Button.STOP"));
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doStop();
			}
		});
		pnlControl.add(btnStop);

		JPanel pnlMain = new JPanel();
		getContentPane().add(pnlMain, BorderLayout.CENTER);
		pnlMain.setLayout(new BorderLayout(0, 0));

		JPanel pnlOutput = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) pnlOutput.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		pnlOutput.setBorder(new TitledBorder(null, VNAMessages.getString("VNASchedulerDialog.format.1"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlMain.add(pnlOutput, BorderLayout.NORTH);

		pnlOutput.add(new JLabel(VNAMessages.getString("VNASchedulerDialog.format.2")));

		rdbtnXls = SwingUtil.createJCheckbox("Menu.Export.XLS", null);
		pnlOutput.add(rdbtnXls);

		rdbtnCsv = SwingUtil.createJCheckbox("Menu.Export.CSV", null);
		pnlOutput.add(rdbtnCsv);

		rdbtnPdf = SwingUtil.createJCheckbox("Menu.Export.PDF", null);
		pnlOutput.add(rdbtnPdf);

		rdbtnJpg = SwingUtil.createJCheckbox("Menu.Export.JPG", null);
		pnlOutput.add(rdbtnJpg);

		rdbtnXml = SwingUtil.createJCheckbox("Menu.Export.XML", null);
		pnlOutput.add(rdbtnXml);

		rdbtnZPlot = SwingUtil.createJCheckbox("Menu.Export.ZPlot", null);
		pnlOutput.add(rdbtnZPlot);

		rdbtnSParm = SwingUtil.createJCheckbox("Menu.Export.S2P", null);
		pnlOutput.add(rdbtnSParm);

		JPanel pnlSchedule = new JPanel();
		pnlSchedule.setBorder(new TitledBorder(null, VNAMessages.getString("VNASchedulerDialog.title"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlMain.add(pnlSchedule, BorderLayout.CENTER);
		pnlSchedule.setLayout(new BorderLayout(0, 0));

		JPanel pnlList = new JPanel();
		pnlList.setBorder(new TitledBorder(null, VNAMessages.getString("VNASchedulerDialog.actions"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlSchedule.add(pnlList, BorderLayout.CENTER);
		pnlList.setLayout(new BorderLayout(0, 0));

		lstTasks = new JList(new DefaultListModel());
		lstTasks.setVisibleRowCount(-1);
		lstTasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane listScroller = new JScrollPane(lstTasks);
		listScroller.setPreferredSize(new Dimension(600, 300));

		pnlList.add(listScroller);

		JPanel pnlCron = new JPanel();
		FlowLayout flowLayout = (FlowLayout) pnlCron.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		pnlSchedule.add(pnlCron, BorderLayout.NORTH);

		JLabel lblCronstring = new JLabel(VNAMessages.getString("VNASchedulerDialog.cron"));
		pnlCron.add(lblCronstring);
		//
		txtCron = new JTextField();
		txtCron.setText("* * * * *");
		pnlCron.add(txtCron);
		txtCron.setColumns(20);
		//
		getRootPane().setDefaultButton(btnOK);
		//
		doDialogInit();
	}

	protected void doStop() {
		TraceHelper.entry(this, "doStop");
		if (taskID != null) {
			scheduler.deschedule(taskID);
			taskID = null;
		}
		btnStart.setEnabled(true);
		btnStop.setEnabled(false);
		btnOK.setEnabled(true);
		txtCron.setEnabled(true);
		TraceHelper.exit(this, "doStop");
	}

	protected void doStart() {
		TraceHelper.entry(this, "doStart");
		btnStart.setEnabled(false);
		btnStop.setEnabled(true);
		btnOK.setEnabled(false);
		txtCron.setEnabled(false);
		taskID = scheduler.schedule(txtCron.getText(), new VNAScheduledScan(mainFrame, this));
		TraceHelper.exit(this, "doStart");
	}

	@Override
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		if (taskID != null) {
			scheduler.deschedule(taskID);
			taskID = null;
		}
		if (scheduler.isStarted()) {
			scheduler.stop();
		}
		dispose();
		TraceHelper.exit(this, "doCANCEL");
	}

	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		scheduler.start();
		btnStop.setEnabled(false);
		addEscapeKey();
		showCentered(getOwner());
		TraceHelper.exit(this, "doInit");
	}

	@SuppressWarnings("unchecked")
	public void consumeDataBlock(List<VNABackgroundJob> jobs) {
		TraceHelper.entry(this, "consumeDataBlock");
		String filename = "";
		DefaultListModel model = (DefaultListModel) lstTasks.getModel();

		mainFrame.getDataPanel().consumeDataBlock(jobs);
		mainFrame.getDiagramPanel().consumeDataBlock(jobs);
		try {
			VNAExporter exp = null;
			String fnp = config.getAutoExportDirectory() + System.getProperty("file.separator") + config.getAutoExportFilename();

			Date now = new Date(System.currentTimeMillis());
			String nowString = DateFormat.getDateTimeInstance().format(now);
			if (rdbtnXml.isSelected()) {
				exp = new XMLExporter(mainFrame);
				filename = exp.export(fnp, config.isExportOverwrite());
				model.add(0, nowString + " " + filename);
			}
			if (rdbtnJpg.isSelected()) {
				exp = new JpegExporter(mainFrame);
				filename = exp.export(fnp, config.isExportOverwrite());
				model.add(0, nowString + " " + filename);
			}
			if (rdbtnCsv.isSelected()) {
				exp = new CSVExporter(mainFrame);
				filename = exp.export(fnp, config.isExportOverwrite());
				model.add(0, nowString + " " + filename);
			}
			if (rdbtnPdf.isSelected()) {
				exp = new PDFExporter(mainFrame);
				filename = exp.export(fnp, config.isExportOverwrite());
				model.add(0, nowString + " " + filename);
			}
			if (rdbtnXls.isSelected()) {
				exp = new XLSExporter(mainFrame);
				filename = exp.export(fnp, config.isExportOverwrite());
				model.add(0, nowString + " " + filename);
			}
			if (rdbtnSParm.isSelected()) {
				exp = new SnPExporter(mainFrame);
				filename = exp.export(fnp, config.isExportOverwrite());
				model.add(0, nowString + " " + filename);
			}
			if (rdbtnZPlot.isSelected()) {
				exp = new ZPlotsExporter(mainFrame);
				filename = exp.export(fnp, config.isExportOverwrite());
				model.add(0, nowString + " " + filename);
			}
			lstTasks.ensureIndexIsVisible(0);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(mainFrame.getJFrame(), MessageFormat.format(VNAMessages.getString("Message.Export.5"), e.getMessage()), VNAMessages.getString("Message.Export.6"), JOptionPane.ERROR_MESSAGE);
			doStop();
		}
		TraceHelper.exit(this, "consumeDataBlock");
	}
}
