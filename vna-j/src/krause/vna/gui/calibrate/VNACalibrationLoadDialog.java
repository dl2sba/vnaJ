package krause.vna.gui.calibrate;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.io.FilenameFilter;
import java.text.MessageFormat;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.HelpButton;
import krause.vna.gui.OptionDialogHelper;
import krause.vna.gui.calibrate.file.VNACalibrationFileTable;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNACalibrationLoadDialog extends KrauseDialog implements IVNACalibrationSelectionListener {
	private final VNAConfig config = VNAConfig.getSingleton();
	private final transient VNADataPool datapool = VNADataPool.getSingleton();
	private final VNADeviceInfoBlock dib = datapool.getDriver().getDeviceInfoBlock();

	private VNACalibrationFileTable lstFiles;
	private JButton btCancel;
	private JButton btOK;
	private JCheckBox cbShowAll;
	private JPanel mainPanel;

	private VNACalibrationBlock selectedCalBlock = null;

	public VNACalibrationLoadDialog(Window pOwner) {
		super(pOwner, true);
		setResizable(true);
		final String methodName = "VNACalibrationLoadDialog";
		TraceHelper.entry(this, methodName);

		setConfigurationPrefix(methodName);
		setProperties(config);

		String tit = VNAMessages.getString("VNACalibrationLoadDialog.title");
		setTitle(MessageFormat.format(tit, datapool.getScanMode().toString()));
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(800, 200));

		mainPanel = new JPanel(new MigLayout("", "[grow,fill][][][]", "[grow,fill][][]"));
		mainPanel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(mainPanel);

		lstFiles = new VNACalibrationFileTable(this);
		JScrollPane scrollPane = new JScrollPane(lstFiles);
		scrollPane.setAlignmentX(LEFT_ALIGNMENT);
		scrollPane.setViewportBorder(null);
		mainPanel.add(scrollPane, "grow, span 4,wrap");

		cbShowAll = new JCheckBox(VNAMessages.getString("VNACalibrationLoadDialog.cbShowAll"));
		mainPanel.add(cbShowAll);

		mainPanel.add(new HelpButton(this, methodName), "wmin 100px"); //NOSONAR

		btCancel = new JButton(VNAMessages.getString("Button.Cancel"));
		mainPanel.add(btCancel, "wmin 100px");

		btOK = new JButton(VNAMessages.getString("Button.OK"));
		mainPanel.add(btOK, "wmin 100px");
		btOK.addActionListener(e -> doOK());
		btCancel.addActionListener(e -> doDialogCancel());
		cbShowAll.addActionListener(e -> loadDirectory(cbShowAll.isSelected()));

		//
		getRootPane().setDefaultButton(btOK);
		//
		doDialogInit();
		TraceHelper.exit(this, methodName);
	}

	public VNACalibrationBlock getSelectedCalibrationBlock() {
		return selectedCalBlock;
	}

	protected void doOK() {
		TraceHelper.entry(this, "doOK");
		setVisible(false);
		TraceHelper.exit(this, "doOK");
	}

	@Override
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		setVisible(false);
		TraceHelper.exit(this, "doCANCEL");
	}

	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		addEscapeKey();
		loadDirectory(false);

		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	/**
	 * 
	 * @param showAllFiles
	 */
	private void loadDirectory(final boolean showAllFiles) {
		final String methodName = "loadDirectory";
		TraceHelper.entry(this, methodName);

		lstFiles.getModel().clear();

		File file = new File(config.getVNACalibrationDirectory());
		FilenameFilter fnf = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".cal");
			}
		};

		File[] files = file.listFiles(fnf);
		for (int i = 0; i < files.length; i++) {
			File currFile = files[i];
			VNACalibrationBlock blk;
			try {
				blk = VNACalibrationBlockHelper.loadHeader(currFile);
				// now filter
				if (showAllFiles) {
					blk.setFile(currFile);
					lstFiles.addCalibrationBlock(blk);
				} else {
					boolean matches = blk.blockMatches(dib, datapool.getScanMode());
					TraceHelper.text(this, methodName, currFile.getName() + ((matches) ? " matches" : " not matching"));
					if (matches) {
						blk.setFile(currFile);
						lstFiles.addCalibrationBlock(blk);
					}
				}
			} catch (ProcessingException e) {
				ErrorLogHelper.exception(this, methodName, e);
			}
		}
		btOK.setEnabled(false);
		//
		Collections.sort(lstFiles.getModel().getData(), new VNACalibrationBlockComparator());
		//
		lstFiles.updateUI();
		TraceHelper.exit(this, methodName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.gui.calibrate.IVNACalibrationSelectionListener#valueChanged(krause.vna.data.calibrated.VNACalibrationBlock,
	 * boolean)
	 */
	public void valueChanged(VNACalibrationBlock blk, boolean doubleClick) {
		final String methodName = "valueChanged";
		TraceHelper.entry(this, methodName, "dbal=%b", doubleClick);

		final boolean matches = blk.blockMatches(dib, datapool.getScanMode());

		// block matches the analyzer and mode?
		if (matches) {
			// yes
			// now load the complete block from file
			try {
				selectedCalBlock = VNACalibrationBlockHelper.load(blk.getFile(), datapool.getDriver(), datapool.getCalibrationKit());
				if (selectedCalBlock != null) {
					btOK.setEnabled(true);
					//
					if (doubleClick) {
						doOK();
					}
				}
			} catch (ProcessingException e) {
				ErrorLogHelper.exception(this, methodName, e);
				OptionDialogHelper.showExceptionDialog(null, "Serializer.Error.1", "Serializer.Error.2", e);
			}
		} else {
			Toolkit.getDefaultToolkit().beep();
			btOK.setEnabled(false);
		}
		TraceHelper.exit(this, methodName);
	}
}
