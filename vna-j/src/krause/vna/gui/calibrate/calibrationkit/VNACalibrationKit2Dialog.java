package krause.vna.gui.calibrate.calibrationkit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;

import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.common.validation.DoubleValidator;
import krause.common.validation.StringValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrationkit.VNACalSetHelper;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNACalibrationKit2Dialog extends KrauseDialog {

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
	private transient VNACalibrationKit currentSelectedCalSet;
	private NumberFormat formatCx;
	private NumberFormat formatLength;
	private transient VNAMainFrame mainFrame;

	public VNACalibrationKit2Dialog(final VNAMainFrame pMainFrame) {
		super(pMainFrame.getJFrame(), true);
		this.mainFrame = pMainFrame;

		TraceHelper.entry(this, "VNACalSetDialog");

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

		buttonPanel.add(new HelpButton(this, "VNACalSetDialog"), "wmin 100px");
		btOK = SwingUtil.createJButton("Button.OK", e -> doDialogOK());
		buttonPanel.add(new JLabel(VNAMessages.getString("VNACalSetDialog.selectedCalSet")), "");
		buttonPanel.add(btOK, "wmin 100px");

		initFormatters();

		//
		getRootPane().setDefaultButton(btOK);
		//
		doDialogInit();
		TraceHelper.exit(this, "VNACalSetDialog");
	}

	private Component createCalSetDetail() {
		final JPanel rc = new JPanel(new MigLayout("", "[50%][]", "[][][]"));
		rc.setBorder(new TitledBorder(null, VNAMessages.getString("VNACalSetDialog.calSetDetail"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.name")), "");
		txtName = new JTextField();
		txtName.setColumns(30);
		rc.add(txtName, "wrap");

		rc.add(createKitParameters(), "span 2,wrap");
		rc.add(createOpenCapacitanceCoefficients(), "span 2,wrap");
		rc.add(createShortInductance(), "grow");
		rc.add(createThruLength(), "wrap");

		btCalSetSave = SwingUtil.createJButton("Button.Save", e -> doSaveCalSet());
		btCalSetSave.setEnabled(false);
		rc.add(btCalSetSave, "wmin 100px");

		btCalSetAbort = SwingUtil.createJButton("Button.Abort", e -> doCalSetAbortEditOrAdd());
		btCalSetAbort.setEnabled(false);
		rc.add(btCalSetAbort, "wmin 100px,right");

		return rc;
	}

	private Component createCalSetList() {
		JPanel rc = new JPanel(new MigLayout("", "[grow,fill][][]", "[grow,fill][]"));
		rc.setBorder(new TitledBorder(null, VNAMessages.getString("VNACalSetDialog.calSetList"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		lbCalibrationSets = new VNACalibrationKitTable();
		lbCalibrationSets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lbCalibrationSets.addListSelectionListener(e -> handleCalSetListSelection(e));
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
				JList list = (JList) evt.getSource();
				if (evt.getClickCount() == 2) {
					int index = list.locationToIndex(evt.getPoint());
					System.out.println("index: " + index);
				}

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

		final JPanel pnlKitParms = new JPanel(new MigLayout("", "[][][]", "[][][]"));
		pnlKitParms.setBorder(new TitledBorder(null, VNAMessages.getString("VNACalSetDialog.kitParms"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		pnlKitParms.add(new JLabel(VNAMessages.getString("VNACalSetDialog.kpOffset")), "right, span 2");
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

		pnlKitParms.add(new JLabel(VNAMessages.getString("VNACalSetDialog.kpShort")), "");
		txtKpShortOffset = new JTextField();
		txtKpShortOffset.setHorizontalAlignment(SwingConstants.RIGHT);
		txtKpShortOffset.setColumns(10);
		pnlKitParms.add(txtKpShortOffset, "");
		txtKpShortLoss = new JTextField();
		txtKpShortLoss.setHorizontalAlignment(SwingConstants.RIGHT);
		txtKpShortLoss.setColumns(10);
		pnlKitParms.add(txtKpShortLoss, "wrap");
		return pnlKitParms;
	}

	private Component createOpenCapacitanceCoefficients() {

		final JPanel rc = new JPanel(new MigLayout("", "[][][]", "[][][]"));
		rc.setBorder(new TitledBorder(null, VNAMessages.getString("VNACalSetDialog.openCapCoeff"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.lblC0")), "right");
		txtC0 = new JTextField();
		txtC0.setHorizontalAlignment(SwingConstants.RIGHT);
		txtC0.setColumns(10);
		rc.add(txtC0, "");

		rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.lblC1")), "right");
		txtC1 = new JTextField();
		txtC1.setHorizontalAlignment(SwingConstants.RIGHT);
		txtC1.setColumns(10);
		rc.add(txtC1, "wrap");

		rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.lblC2")), "right");
		txtC2 = new JTextField();
		txtC2.setHorizontalAlignment(SwingConstants.RIGHT);
		txtC2.setColumns(10);
		rc.add(txtC2, "");

		rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.lblC3")), "right");
		txtC3 = new JTextField();
		txtC3.setHorizontalAlignment(SwingConstants.RIGHT);
		txtC3.setColumns(10);
		rc.add(txtC3, "wrap");

		return rc;
	}

	private Component createShortInductance() {
		final JPanel rc = new JPanel(new MigLayout("", "[][]", "[]"));
		rc.setBorder(new TitledBorder(null, VNAMessages.getString("VNACalSetDialog.shortInductance"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.lblL")), "right");
		txtShortInd = new JTextField();
		txtShortInd.setHorizontalAlignment(SwingConstants.RIGHT);
		txtShortInd.setColumns(10);
		rc.add(txtShortInd, "");
		return rc;
	}

	private Component createThruLength() {
		final JPanel rc = new JPanel(new MigLayout("", "[][]", "[]"));
		rc.setBorder(new TitledBorder(null, VNAMessages.getString("VNACalSetDialog.thruLength"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.lblLen")), "right");
		txtThruLen = new JTextField();
		txtThruLen.setHorizontalAlignment(SwingConstants.RIGHT);
		txtThruLen.setColumns(10);
		rc.add(txtThruLen, "");
		return rc;
	}

	protected void doCalSetAbortEditOrAdd() {
		TraceHelper.entry(this, "doCalSetAbortEditOrAdd");
		enableEditFields(false);

		btCalSetAdd.setEnabled(true);
		btCalSetDelete.setEnabled(false);
		btCalSetAbort.setEnabled(false);
		btCalSetSave.setEnabled(false);

		currentEditMode = EDIT_MODES.NONE;

		TraceHelper.exit(this, "doCalSetAbortEditOrAdd");
	}

	protected void doCalSetAdd() {
		TraceHelper.entry(this, "doCalSetAdd");

		btCalSetAdd.setEnabled(false);
		btCalSetDelete.setEnabled(false);
		btCalSetEdit.setEnabled(false);
		btCalSetAbort.setEnabled(true);
		btCalSetSave.setEnabled(true);

		this.currentSelectedCalSet = new VNACalibrationKit("");
		transferDataToFields(this.currentSelectedCalSet);
		currentEditMode = EDIT_MODES.ADD;
		enableEditFields(true);

		TraceHelper.exit(this, "doCalSetAdd");
	}

	protected void doCalSetEdit() {
		TraceHelper.entry(this, "doCalSetEdit");

		btCalSetAdd.setEnabled(false);
		btCalSetDelete.setEnabled(false);
		btCalSetEdit.setEnabled(false);
		btCalSetAbort.setEnabled(true);
		btCalSetSave.setEnabled(true);

		currentEditMode = EDIT_MODES.EDIT;
		this.currentSelectedCalSet = lbCalibrationSets.getSelectedValue();
		transferDataToFields(this.currentSelectedCalSet);
		enableEditFields(true);

		TraceHelper.exit(this, "doCalSetEdit");
	}

	protected void doDialogCancel() {
		TraceHelper.entry(this, "doDialogCancel");
		setVisible(false);
		dispose();
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
			this.currentSelectedCalSet = null;
			final VNACalibrationKitTableListModel model = (VNACalibrationKitTableListModel) lbCalibrationSets.getModel();
			model.removeElement(lbCalibrationSets.getSelectedValue());
		}
		TraceHelper.exit(this, "doCalSetDelete");
	}

	protected void doDialogInit() {
		TraceHelper.entry(this, "doDialogInit");
		enableEditFields(false);

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
		TraceHelper.entry(this, "doDialogOK");

		final VNACalibrationKitTableListModel model = (VNACalibrationKitTableListModel) lbCalibrationSets.getModel();
		new VNACalSetHelper().save(model.getData(), config.getCalibrationKitFilename());

		if (this.currentSelectedCalSet != null) {
			final VNADataPool dataPool = VNADataPool.getSingleton();

			TraceHelper.text(this, "doDialogOK", "Setting calset [" + this.currentSelectedCalSet + "] into datapool");
			dataPool.setCalibrationKit(this.currentSelectedCalSet);

			// force rebuild of calibration data
			TraceHelper.text(this, "doDialogOK", "Clearing pre-calculated calibrationblocks in datapool");
			VNACalibrationBlock reloadedBlock;
			try {
				reloadedBlock = VNACalibrationBlockHelper.load(dataPool.getMainCalibrationBlock().getFile(), dataPool.getDriver(), this.currentSelectedCalSet);
				this.mainFrame.setMainCalibrationBlock(reloadedBlock);
			} catch (ProcessingException e) {
				ErrorLogHelper.exception(this, "doDialogOK", e);
			}

			TraceHelper.text(this, "doDialogOK", "Setting calset [" + this.currentSelectedCalSet + "] as default calset");
			this.config.setCurrentCalSetID(this.currentSelectedCalSet.getId());
		}

		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doDialogOK");
	}

	protected void doSaveCalSet() {
		TraceHelper.entry(this, "doSaveCalSet");

		if (transferFieldsToData(currentSelectedCalSet)) {
			enableEditFields(false);
			btCalSetAdd.setEnabled(true);
			btCalSetDelete.setEnabled(true);
			btCalSetAbort.setEnabled(false);
			btCalSetSave.setEnabled(false);

			if (currentEditMode == EDIT_MODES.EDIT) {
				lbCalibrationSets.updateCalSet(currentSelectedCalSet);
			} else {
				lbCalibrationSets.addCalSet(currentSelectedCalSet);
			}
			currentEditMode = EDIT_MODES.NONE;
		}
		TraceHelper.exit(this, "doSaveCalSet");
	}

	protected void handleCalSetListSelection(ListSelectionEvent e) {
		TraceHelper.entry(this, "handleCalSetListSelection");
		if (!e.getValueIsAdjusting()) {

			final int selIdx = lbCalibrationSets.getSelectedIndex();

			if (selIdx == -1) {
				this.currentSelectedCalSet = null;
				btCalSetDelete.setEnabled(false);
				btCalSetEdit.setEnabled(false);
				btCalSetAdd.setEnabled(true);

			} else {
				this.currentSelectedCalSet = lbCalibrationSets.getSelectedValue();
				enableEditFields(false);
				btCalSetDelete.setEnabled(true);
				btCalSetEdit.setEnabled(true);
				btCalSetAdd.setEnabled(true);
			}
		}
		transferDataToFields(this.currentSelectedCalSet);
		TraceHelper.exit(this, "handleCalSetListSelection");
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
		}

		TraceHelper.exit(this, "transferDataToFields");
	}

	private boolean transferFieldsToData(VNACalibrationKit calSetToUpdate) {
		TraceHelper.entry(this, "transferFieldsToData");
		boolean rc = false;
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
			calSetToUpdate.setName(name);
			calSetToUpdate.setOpenCapCoeffC0(c0);
			calSetToUpdate.setOpenCapCoeffC1(c1);
			calSetToUpdate.setOpenCapCoeffC2(c2);
			calSetToUpdate.setOpenCapCoeffC3(c3);

			calSetToUpdate.setOpenOffset(kpOpenOffset);
			calSetToUpdate.setOpenLoss(kpOpenLoss);

			calSetToUpdate.setShortOffset(kpShortOffset);
			calSetToUpdate.setShortLoss(kpShortLoss);

			calSetToUpdate.setThruLength(thruLength);
			calSetToUpdate.setShortInductance(shortInductance);
			rc = true;
		} else {
			new ValidationResultsDialog(getOwner(), results, getTitle());
		}
		TraceHelper.exit(this, "transferFieldsToData");
		return rc;
	}

	private void enableEditFields(final boolean enabled) {
		txtC0.setEnabled(enabled);
		txtC1.setEnabled(enabled);
		txtC2.setEnabled(enabled);
		txtC3.setEnabled(enabled);
		txtKpOpenLoss.setEnabled(enabled);
		txtKpShortLoss.setEnabled(enabled);
		txtKpOpenOffset.setEnabled(enabled);
		txtKpShortOffset.setEnabled(enabled);
		txtName.setEnabled(enabled);
		txtShortInd.setEnabled(enabled);
		txtThruLen.setEnabled(enabled);
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
}