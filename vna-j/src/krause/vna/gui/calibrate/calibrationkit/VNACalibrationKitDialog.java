package krause.vna.gui.calibrate.calibrationkit;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.common.validation.DoubleValidator;
import krause.common.validation.StringValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.IVNABackgroundTaskStatusListener;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.calibrationkit.VNACalSetHelper;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNACalibrationKitDialog extends KrauseDialog implements AdjustmentListener, ListSelectionListener, ActionListener, IVNABackgroundTaskStatusListener {

	private enum EDIT_MODES {
		NONE, EDIT, ADD
	}

	private final VNAConfig config = VNAConfig.getSingleton();

	private JButton btOK;
	private VNACalibrationKitTable lbCalibrationSets;
	private JTextField txtKpOpenOffset;
	private JTextField txtKpOpenLoss;
	private JTextField txtKpShortOffset;
	private JTextField txtKpShortLoss;
	private JTextField txtShortInd;
	private JTextField txtC0;
	private JTextField txtC1;
	private JTextField txtC2;
	private JTextField txtC3;
	private JTextField txtThruLen;
	private JTextField txtName;

	private JButton btCalSetAdd;
	private JButton btCalSetEdit;
	private JButton btCalSetDelete;
	private JButton btCalSetSave;
	private JButton btCalSetAbort;

	private EDIT_MODES currentEditMode = EDIT_MODES.NONE;
	private transient VNACalibrationKit selectedCalibrationKit;
	private NumberFormat formatCx;
	private NumberFormat formatLength;
	private transient VNAMainFrame mainFrame;

	private JScrollBar sbOpenOffset;

	private JScrollBar sbShortOffset;

	private JToggleButton cbSmith;

	private VNACalibrationKitSmithDiagramDialog smithDialog;
	private static final int NUM_SAMPLES = 200;

	public VNACalibrationKitDialog(final VNAMainFrame pMainFrame) {
		super(pMainFrame.getJFrame(), true);
		this.mainFrame = pMainFrame;

		TraceHelper.entry(this, "VNACalSetDialog"); // NOSONAR

		setTitle(VNAMessages.getString("VNACalSetDialog.title"));
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setProperties(config);
		setConfigurationPrefix("VNACalSetDialog");
		setPreferredSize(new Dimension(580, 450));

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[][grow,fill]", "[grow,fill]"));

		panel.add(createCalSetList(), "");
		panel.add(createCalSetDetail(), "wrap");

		final JPanel buttonPanel = new JPanel(new MigLayout("", "[left][grow,fill][right]", ""));
		panel.add(buttonPanel, "span 2,grow");

		buttonPanel.add(new HelpButton(this, "VNACalSetDialog"), "wmin 100px");// NOSONAR
		btOK = SwingUtil.createJButton("Button.OK", e -> doDialogOK());
		buttonPanel.add(new JLabel(VNAMessages.getString("VNACalSetDialog.selectedCalSet")), "");
		buttonPanel.add(btOK, "wmin 100px");// NOSONAR

		initFormatters();

		//
		getRootPane().setDefaultButton(btOK);
		//
		doDialogInit();
		TraceHelper.exit(this, "VNACalSetDialog");
	}

	private Component createCalSetDetail() {
		final JPanel rc = new JPanel(new MigLayout("", "[grow,fill][][]", "[]")); // NOSONAR
		rc.setBorder(new TitledBorder(null, VNAMessages.getString("VNACalSetDialog.calSetDetail"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.name")), "");
		txtName = new JTextField();
		txtName.setColumns(30);
		rc.add(txtName, "wrap");

		rc.add(createKitParameters(), "span 3, wrap");// NOSONAR
		rc.add(createOpenCapacitanceCoefficients(), "span 3,wrap");// NOSONAR
		rc.add(createShortInductance(), "span 2,grow");
		rc.add(createThruLength(), "wrap");

		btCalSetAbort = SwingUtil.createJButton("Button.Abort", e -> doCalSetAbortEditOrAdd());
		btCalSetAbort.setEnabled(false);
		rc.add(btCalSetAbort, "wmin 100px");

		this.cbSmith = SwingUtil.createToggleButton("Panel.Scale.Smith", this);
		cbSmith.setEnabled(false);

		rc.add(this.cbSmith, "center");

		btCalSetSave = SwingUtil.createJButton("Button.Save", e -> doSaveCalSet());
		btCalSetSave.setEnabled(false);
		rc.add(btCalSetSave, "wmin 100px,right");

		return rc;
	}

	private Component createCalSetList() {
		JPanel rc = new JPanel(new MigLayout("", "[grow,fill][][]", "[grow,fill][]"));
		rc.setBorder(new TitledBorder(null, VNAMessages.getString("VNACalSetDialog.calSetList"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		lbCalibrationSets = new VNACalibrationKitTable();
		lbCalibrationSets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lbCalibrationSets.addListSelectionListener(this);
		lbCalibrationSets.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// not used
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// not used
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// not used
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// not used
			}

			@Override
			public void mouseClicked(MouseEvent evt) {
				// not used
			}
		});

		JScrollPane sp = new JScrollPane(lbCalibrationSets);
		rc.add(sp, "span 3,wrap");

		btCalSetAdd = SwingUtil.createJButton("Button.Add", e -> doCalSetAdd());
		rc.add(btCalSetAdd, "");

		btCalSetEdit = SwingUtil.createJButton("Button.Edit", e -> doCalSetEdit());
		btCalSetEdit.setEnabled(false);
		rc.add(btCalSetEdit, "");

		btCalSetDelete = SwingUtil.createJButton("Button.Delete", e -> doCalSetDelete());
		btCalSetDelete.setEnabled(false);
		rc.add(btCalSetDelete, "");

		return rc;
	}

	private Component createKitParameters() {

		final JPanel pnlKitParms = new JPanel(new MigLayout("", "[20%][][]", "[][][]"));
		pnlKitParms.setBorder(new TitledBorder(null, VNAMessages.getString("VNACalSetDialog.kitParms"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		pnlKitParms.add(new JLabel());
		pnlKitParms.add(new JLabel(VNAMessages.getString("VNACalSetDialog.kpOffset")), "right"); // NOSONAR
		pnlKitParms.add(new JLabel(VNAMessages.getString("VNACalSetDialog.kpLoss")), "right, wrap");

		pnlKitParms.add(new JLabel(VNAMessages.getString("VNACalSetDialog.kpOpen")), "");
		txtKpOpenOffset = new JTextField();
		txtKpOpenOffset.setHorizontalAlignment(SwingConstants.RIGHT);
		txtKpOpenOffset.setColumns(10);
		pnlKitParms.add(txtKpOpenOffset, "");

		txtKpOpenLoss = new JTextField();
		txtKpOpenLoss.setHorizontalAlignment(SwingConstants.RIGHT);
		txtKpOpenLoss.setColumns(10);
		pnlKitParms.add(txtKpOpenLoss, "wrap");

		pnlKitParms.add(new JLabel());
		sbOpenOffset = new JScrollBar(Adjustable.HORIZONTAL, 0, 1, -300, 300);
		sbOpenOffset.addAdjustmentListener(this);
		pnlKitParms.add(sbOpenOffset, "grow,wrap");

		pnlKitParms.add(new JLabel(VNAMessages.getString("VNACalSetDialog.kpShort")), "");
		txtKpShortOffset = new JTextField();
		txtKpShortOffset.setHorizontalAlignment(SwingConstants.RIGHT);
		txtKpShortOffset.setColumns(10);
		pnlKitParms.add(txtKpShortOffset, "");

		txtKpShortLoss = new JTextField();
		txtKpShortLoss.setHorizontalAlignment(SwingConstants.RIGHT);
		txtKpShortLoss.setColumns(10);
		pnlKitParms.add(txtKpShortLoss, "wrap");

		pnlKitParms.add(new JLabel());
		sbShortOffset = new JScrollBar(Adjustable.HORIZONTAL, 0, 1, -300, 300);
		sbShortOffset.addAdjustmentListener(this);
		pnlKitParms.add(sbShortOffset, "grow,wrap");

		return pnlKitParms;
	}

	private Component createOpenCapacitanceCoefficients() {

		final JPanel rc = new JPanel(new MigLayout("", "[][][]", "[][][]"));
		rc.setBorder(new TitledBorder(null, VNAMessages.getString("VNACalSetDialog.openCapCoeff"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.lblC0")), "right"); // NOSONAR
		txtC0 = new JTextField();
		txtC0.setHorizontalAlignment(SwingConstants.RIGHT);
		txtC0.setColumns(10);
		rc.add(txtC0, "");

		rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.lblC1")), "right");// NOSONAR
		txtC1 = new JTextField();
		txtC1.setHorizontalAlignment(SwingConstants.RIGHT);
		txtC1.setColumns(10);
		rc.add(txtC1, "wrap");

		rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.lblC2")), "right");// NOSONAR
		txtC2 = new JTextField();
		txtC2.setHorizontalAlignment(SwingConstants.RIGHT);
		txtC2.setColumns(10);
		rc.add(txtC2, "");

		rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.lblC3")), "right");// NOSONAR
		txtC3 = new JTextField();
		txtC3.setHorizontalAlignment(SwingConstants.RIGHT);
		txtC3.setColumns(10);
		rc.add(txtC3, "wrap");

		return rc;
	}

	private Component createShortInductance() {
		final JPanel rc = new JPanel(new MigLayout("", "[][]", "[]"));
		rc.setBorder(new TitledBorder(null, VNAMessages.getString("VNACalSetDialog.shortInductance"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.lblL")), "right");// NOSONAR
		txtShortInd = new JTextField();
		txtShortInd.setHorizontalAlignment(SwingConstants.RIGHT);
		txtShortInd.setColumns(10);
		rc.add(txtShortInd, "");
		return rc;
	}

	private Component createThruLength() {
		final JPanel rc = new JPanel(new MigLayout("", "[][]", "[]"));
		rc.setBorder(new TitledBorder(null, VNAMessages.getString("VNACalSetDialog.thruLength"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.lblLen")), "right");// NOSONAR
		txtThruLen = new JTextField();
		txtThruLen.setHorizontalAlignment(SwingConstants.RIGHT);
		txtThruLen.setColumns(10);
		rc.add(txtThruLen, "");
		return rc;
	}

	protected void doCalSetAbortEditOrAdd() {
		TraceHelper.entry(this, "doCalSetAbortEditOrAdd");
		enableFieldsAndButtons();

		valueChanged(new ListSelectionEvent(lbCalibrationSets, 0, 0, false));

		currentEditMode = EDIT_MODES.NONE;

		if (this.smithDialog != null) {
			removeSmithDialog();
			this.cbSmith.setSelected(false);
		}

		TraceHelper.exit(this, "doCalSetAbortEditOrAdd");
	}

	protected void doCalSetAdd() {
		TraceHelper.entry(this, "doCalSetAdd");

		this.selectedCalibrationKit = new VNACalibrationKit("");
		transferDataToFields(this.selectedCalibrationKit);
		currentEditMode = EDIT_MODES.ADD;
		enableFieldsAndButtons();

		TraceHelper.exit(this, "doCalSetAdd");
	}

	protected void doCalSetEdit() {
		TraceHelper.entry(this, "doCalSetEdit");

		currentEditMode = EDIT_MODES.EDIT;
		this.selectedCalibrationKit = lbCalibrationSets.getSelectedValue();
		transferDataToFields(this.selectedCalibrationKit);
		enableFieldsAndButtons();

		TraceHelper.exit(this, "doCalSetEdit");
	}

	protected void doDialogCancel() {
		TraceHelper.entry(this, "doDialogCancel");
		TraceHelper.exit(this, "doDialogCancel");
	}

	protected void doCalSetDelete() {
		TraceHelper.entry(this, "doCalSetDelete");

		Object[] options = {
				VNAMessages.getString("Button.Delete"), //$NON-NLS-1$
				VNAMessages.getString("Button.Cancel") //$NON-NLS-1$
		};
		int n = JOptionPane.showOptionDialog(this.mainFrame.getJFrame(), VNAMessages.getString("VNACalSetDialog.Delete.1"), VNAMessages //$NON-NLS-1$
				.getString("VNACalSetDialog.Delete.2"), //$NON-NLS-1$
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (n == 0) {
			this.selectedCalibrationKit = null;
			final VNACalibrationKitTableListModel model = (VNACalibrationKitTableListModel) lbCalibrationSets.getModel();
			model.removeElement(lbCalibrationSets.getSelectedValue());
		}
		TraceHelper.exit(this, "doCalSetDelete");
	}

	protected void doDialogInit() {
		TraceHelper.entry(this, "doDialogInit");
		this.currentEditMode = EDIT_MODES.NONE;
		enableFieldsAndButtons();

		final VNACalibrationKitTableListModel model = (VNACalibrationKitTableListModel) lbCalibrationSets.getModel();
		List<VNACalibrationKit> calSets = new VNACalSetHelper().load(config.getCalibrationKitFilename());
		for (VNACalibrationKit aCalSet : calSets) {
			model.addElement(aCalSet);
		}

		for (VNACalibrationKit calSet : calSets) {
			if (calSet.getId().equals(config.getCurrentCalSetID())) {
				lbCalibrationSets.setSelectedValue(calSet, true);
			}
		}

		doDialogShow();
		TraceHelper.exit(this, "doDialogInit");
	}

	protected void doDialogOK() {
		final String methodName = "doDialogOK";
		TraceHelper.entry(this, methodName);

		final VNACalibrationKitTableListModel model = (VNACalibrationKitTableListModel) lbCalibrationSets.getModel();
		new VNACalSetHelper().save(model.getData(), config.getCalibrationKitFilename());

		if (this.selectedCalibrationKit != null) {
			final VNADataPool dataPool = VNADataPool.getSingleton();

			TraceHelper.text(this, methodName, "Setting calset [" + this.selectedCalibrationKit + "] into datapool");
			dataPool.setCalibrationKit(this.selectedCalibrationKit);

			// force rebuild of calibration data
			VNACalibrationBlock reloadedBlock;
			try {
				reloadedBlock = VNACalibrationBlockHelper.load(dataPool.getMainCalibrationBlock().getFile(), dataPool.getDriver(), this.selectedCalibrationKit);
				this.mainFrame.setMainCalibrationBlock(reloadedBlock);
			} catch (ProcessingException e) {
				ErrorLogHelper.exception(this, methodName, e);
			}

			TraceHelper.text(this, methodName, "Setting calset [" + this.selectedCalibrationKit + "] as default calset");
			this.config.setCurrentCalSetID(this.selectedCalibrationKit.getId());
		}

		setVisible(false);
		dispose();
		TraceHelper.entry(this, methodName);
	}

	protected void doSaveCalSet() {
		TraceHelper.entry(this, "doSaveCalSet");

		this.selectedCalibrationKit = transferFieldsToData(this.selectedCalibrationKit.getId());

		if (this.selectedCalibrationKit != null) {
			if (currentEditMode == EDIT_MODES.EDIT) {
				lbCalibrationSets.updateCalSet(this.selectedCalibrationKit);
			} else {
				lbCalibrationSets.addCalSet(this.selectedCalibrationKit);
			}
			currentEditMode = EDIT_MODES.NONE;
			enableFieldsAndButtons();
		}
		TraceHelper.exit(this, "doSaveCalSet");
	}

	private void transferDataToFields(final VNACalibrationKit calSet) {
		TraceHelper.entry(this, "transferDataToFields");

		if (calSet == null) {
			txtC0.setText("");
			txtC1.setText("");
			txtC2.setText("");
			txtC3.setText("");
			txtKpOpenOffset.setText("");
			txtKpShortOffset.setText("");
			txtThruLen.setText("");
			txtName.setText("");
			txtKpOpenLoss.setText("");
			txtKpShortLoss.setText("");
			txtThruLen.setText("");
		} else {
			txtC0.setText(formatCx.format(calSet.getOpenCapCoeffC0()));
			txtC1.setText(formatCx.format(calSet.getOpenCapCoeffC1()));
			txtC2.setText(formatCx.format(calSet.getOpenCapCoeffC2()));
			txtC3.setText(formatCx.format(calSet.getOpenCapCoeffC3()));
			txtKpOpenOffset.setText(formatLength.format(calSet.getOpenOffset()));
			txtKpOpenLoss.setText(formatLength.format(calSet.getOpenLoss()));
			txtKpShortOffset.setText(formatLength.format(calSet.getShortOffset()));
			txtKpShortLoss.setText(formatLength.format(calSet.getShortLoss()));
			txtName.setText(calSet.getName());
			txtThruLen.setText(formatLength.format(calSet.getThruLength()));
			txtShortInd.setText(formatLength.format(calSet.getShortInductance()));
			sbOpenOffset.setValue((int) (calSet.getOpenOffset() * 10));
			sbShortOffset.setValue((int) (calSet.getShortOffset() * 10));
		}

		TraceHelper.exit(this, "transferDataToFields");
	}

	private VNACalibrationKit transferFieldsToData(final String id) {
		TraceHelper.entry(this, "transferFieldsToData");
		VNACalibrationKit rc = null;
		ValidationResults results = new ValidationResults();

		String name = StringValidator.parse(txtName.getText(), 1, 20, VNAMessages.getString("VNACalSetDialog.name"), results);
		double c0 = DoubleValidator.parse(txtC0.getText(), 0.0, 10000.0, VNAMessages.getString("VNACalSetDialog.lblC0"), results);
		double c1 = DoubleValidator.parse(txtC1.getText(), 0.0, 10000.0, VNAMessages.getString("VNACalSetDialog.lblC1"), results);
		double c2 = DoubleValidator.parse(txtC2.getText(), 0.0, 10000.0, VNAMessages.getString("VNACalSetDialog.lblC2"), results);
		double c3 = DoubleValidator.parse(txtC3.getText(), 0.0, 10000.0, VNAMessages.getString("VNACalSetDialog.lblC3"), results);

		double kpOpenLoss = DoubleValidator.parse(txtKpOpenLoss.getText(), 0.0, 10000.0, VNAMessages.getString("VNACalSetDialog.kpOpen"), results);
		double kpShortLoss = DoubleValidator.parse(txtKpShortLoss.getText(), 0.0, 10000.0, VNAMessages.getString("VNACalSetDialog.kpLoss"), results);
		double shortInductance = DoubleValidator.parse(txtShortInd.getText(), 0.0, 10000.0, VNAMessages.getString("VNACalSetDialog.shortInductance"), results);

		double kpShortOffset = DoubleValidator.parse(txtKpShortOffset.getText(), -100.0, 100.0, VNAMessages.getString("VNACalSetDialog.kpShortOffset"), results);
		double kpOpenOffset = DoubleValidator.parse(txtKpOpenOffset.getText(), -100.0, 100.0, VNAMessages.getString("VNACalSetDialog.kpOpenOffset"), results);
		double thruLength = DoubleValidator.parse(txtThruLen.getText(), -100.0, 100.0, VNAMessages.getString("VNACalSetDialog.thruLength"), results);

		if (results.isEmpty()) {
			rc = new VNACalibrationKit(name);
			rc.setId(id);
			rc.setOpenCapCoeffC0(c0);
			rc.setOpenCapCoeffC1(c1);
			rc.setOpenCapCoeffC2(c2);
			rc.setOpenCapCoeffC3(c3);

			rc.setOpenOffset(kpOpenOffset);
			rc.setOpenLoss(kpOpenLoss);

			rc.setShortOffset(kpShortOffset);
			rc.setShortLoss(kpShortLoss);
			rc.setThruLength(thruLength);
			rc.setShortInductance(shortInductance);
		} else {
			new ValidationResultsDialog(getOwner(), results, getTitle());
		}
		TraceHelper.exit(this, "transferFieldsToData");
		return rc;
	}

	// NOSONAR
	private void enableFieldsAndButtons() {
		switch (currentEditMode) {
		case ADD:
		case EDIT:
			txtC0.setEnabled(true);
			txtC1.setEnabled(true);
			txtC2.setEnabled(true);
			txtC3.setEnabled(true);
			txtKpOpenLoss.setEnabled(true);
			txtKpShortLoss.setEnabled(true);
			txtKpOpenOffset.setEnabled(true);
			txtKpShortOffset.setEnabled(true);
			txtName.setEnabled(true);
			txtShortInd.setEnabled(true);
			txtThruLen.setEnabled(true);
			sbOpenOffset.setEnabled(true);
			sbShortOffset.setEnabled(true);

			lbCalibrationSets.setEnabled(false);
			btCalSetAdd.setEnabled(false);
			btCalSetDelete.setEnabled(false);
			btCalSetEdit.setEnabled(false);

			btCalSetAbort.setEnabled(true);
			btCalSetSave.setEnabled(true);

			cbSmith.setEnabled(true);
			btOK.setEnabled(false);

			break;

		case NONE:
			txtC0.setEnabled(false);
			txtC1.setEnabled(false);
			txtC2.setEnabled(false);
			txtC3.setEnabled(false);
			txtKpOpenLoss.setEnabled(false);
			txtKpShortLoss.setEnabled(false);
			txtKpOpenOffset.setEnabled(false);
			txtKpShortOffset.setEnabled(false);
			txtName.setEnabled(false);
			txtShortInd.setEnabled(false);
			txtThruLen.setEnabled(false);
			sbOpenOffset.setEnabled(false);
			sbShortOffset.setEnabled(false);

			lbCalibrationSets.setEnabled(true);
			btCalSetAdd.setEnabled(true);
			btCalSetDelete.setEnabled(false);
			btCalSetAbort.setEnabled(false);
			btCalSetSave.setEnabled(false);

			cbSmith.setEnabled(false);
			btOK.setEnabled(true);

			break;
		}
	}

	private void initFormatters() {
		formatCx = NumberFormat.getNumberInstance();
		formatCx.setGroupingUsed(false);
		formatCx.setMaximumFractionDigits(2);
		formatCx.setMinimumFractionDigits(2);
		formatCx.setMaximumIntegerDigits(4);
		formatCx.setMinimumIntegerDigits(1);

		formatLength = NumberFormat.getNumberInstance();
		formatLength.setGroupingUsed(false);
		formatLength.setMaximumFractionDigits(1);
		formatLength.setMinimumFractionDigits(1);
		formatLength.setMaximumIntegerDigits(4);
		formatLength.setMinimumIntegerDigits(1);

	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		boolean updateDiagram = false;

		// dynamically transfer data to text fields
		if (e.getSource() == sbOpenOffset) {
			txtKpOpenOffset.setText(formatLength.format(sbOpenOffset.getValue() / 10.0));
			updateDiagram = true;
		} else if (e.getSource() == sbShortOffset) {
			txtKpShortOffset.setText(formatLength.format(sbShortOffset.getValue() / 10.0));
			updateDiagram = true;
		}

		// if slider released then optionally update smit chart
		if (!e.getValueIsAdjusting() && updateDiagram && smithDialog != null) {
			this.selectedCalibrationKit = transferFieldsToData(this.selectedCalibrationKit.getId());
			if (this.selectedCalibrationKit != null) {
				final VNACalibratedSampleBlock calibratedSamples = doExecuteOneScan();
				smithDialog.consumeCalibratedData(calibratedSamples);
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		final boolean isAdjusting = e.getValueIsAdjusting();
		final String methodName = "valueChanged";
		TraceHelper.entry(this, methodName, "adj=%b", isAdjusting);

		if (!isAdjusting) {
			enableFieldsAndButtons();

			final int selIdx = lbCalibrationSets.getSelectedIndex();

			if (selIdx == -1) {
				this.selectedCalibrationKit = null;
				btCalSetDelete.setEnabled(false);
				btCalSetEdit.setEnabled(false);
				btCalSetAdd.setEnabled(true);

			} else {
				this.selectedCalibrationKit = lbCalibrationSets.getSelectedValue();
				btCalSetDelete.setEnabled(true);
				btCalSetEdit.setEnabled(true);
				btCalSetAdd.setEnabled(true);
			}
			transferDataToFields(this.selectedCalibrationKit);
		}
		TraceHelper.exit(this, methodName);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final String methodName = "actionPerformed";
		TraceHelper.entry(this, methodName, "enabled=%b", this.cbSmith.isSelected());

		if (this.cbSmith.isSelected()) {
			if (this.smithDialog == null) {
				setupSmithDialog();
			}
		} else {
			if (this.smithDialog != null) {
				removeSmithDialog();
				this.cbSmith.setSelected(false);
			}
		}
		TraceHelper.exit(this, methodName);
	}

	private void removeSmithDialog() {
		final String methodName = "removeSmithDialog";
		TraceHelper.entry(this, methodName);
		smithDialog.setVisible(false);
		smithDialog.dispose();
		smithDialog = null;

		TraceHelper.exit(this, methodName);
	}

	private void setupSmithDialog() {
		final String methodName = "setupSmithDialog";
		TraceHelper.entry(this, methodName);
		this.smithDialog = new VNACalibrationKitSmithDiagramDialog(this);
		this.smithDialog.setLocation(this.getX() + this.getWidth(), this.getY());
		this.smithDialog.setVisible(true);

		final VNACalibratedSampleBlock calibratedSamples = doExecuteOneScan();
		smithDialog.consumeCalibratedData(calibratedSamples);
		TraceHelper.exit(this, methodName);
	}

	private VNACalibratedSampleBlock doExecuteOneScan() {
		final String methodName = "doExecuteOneScan";
		VNACalibratedSampleBlock calibratedSamples = null;

		TraceHelper.entry(this, methodName);
		final VNADataPool pool = VNADataPool.getSingleton();
		final IVNADriver driver = pool.getDriver();
		final IVNADriverMathHelper mathHelper = driver.getMathHelper();
		final VNADeviceInfoBlock dib = driver.getDeviceInfoBlock();
		final long startFreq = 1600000000;
		final long stopFreq = dib.getMaxFrequency();
		final File file = pool.getMainCalibrationBlock().getFile();

		try {
			final VNACalibrationBlock mcb = VNACalibrationBlockHelper.load(file, driver, this.selectedCalibrationKit);

			// create a resized one matching the frequency range
			final VNACalibrationBlock resizedCalibrationBlock = VNACalibrationBlockHelper.createResizedCalibrationBlock(mcb, startFreq, stopFreq, NUM_SAMPLES);
			//
			VNASampleBlock rawData = driver.scan(VNAScanMode.MODE_REFLECTION, startFreq, stopFreq, NUM_SAMPLES, this);
			// calibrate them
			final VNACalibrationContext context = mathHelper.createCalibrationContextForCalibratedSamples(resizedCalibrationBlock);
			context.setConversionTemperature(rawData.getDeviceTemperature());
			calibratedSamples = mathHelper.createCalibratedSamples(context, rawData);
		} catch (ProcessingException e) {
			ErrorLogHelper.exception(this, methodName, e);
		}

		TraceHelper.exit(this, methodName);
		return calibratedSamples;
	}

	@Override
	public void publishProgress(int percentage) {
		// not running in background thread so no update possible
	}
}