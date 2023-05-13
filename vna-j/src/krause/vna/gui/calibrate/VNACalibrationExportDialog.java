package krause.vna.gui.calibrate;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.text.MessageFormat;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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

public class VNACalibrationExportDialog extends KrauseDialog implements IVNACalibrationSelectionListener {
	private static VNAConfig config = VNAConfig.getSingleton();
	private VNADataPool datapool = VNADataPool.getSingleton();
	private VNADeviceInfoBlock dib = datapool.getDriver().getDeviceInfoBlock();

	private VNACalibrationFileTable lstFiles;
	private JButton btCancel;
	private JButton btOK;

	private VNACalibrationBlock selectedCalBlock = null;
	private JPanel pnlButtons;
	private JCheckBox cbShowAll;
	private JPanel pnlFiles;

	public VNACalibrationExportDialog(Window pOwner) {
		super(pOwner, true);
		setResizable(false);
		TraceHelper.entry(this, "VNACalibrationExportDialog");

		String tit = VNAMessages.getString("VNACalibrationExportDialog.title");
		setTitle(MessageFormat.format(tit, datapool.getScanMode().toString()));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 800, 333);

		pnlButtons = new JPanel();
		pnlButtons.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(pnlButtons, BorderLayout.SOUTH);

		cbShowAll = new JCheckBox(VNAMessages.getString("VNACalibrationExportDialog.cbShowAll"));
		pnlButtons.add(cbShowAll);

		btCancel = new JButton(VNAMessages.getString("Button.Cancel"));
		pnlButtons.add(btCancel);

		pnlButtons.add(new HelpButton(this, "VNACalibrationExportDialog"));

		btOK = new JButton(VNAMessages.getString("Button.OK"));
		pnlButtons.add(btOK);
		btOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doOK();
			}
		});
		btCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doDialogCancel();
			}
		});
		cbShowAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadDirectory(cbShowAll.isSelected());
			}
		});

		pnlFiles = new JPanel();
		pnlFiles.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(pnlFiles, BorderLayout.CENTER);

		lstFiles = new VNACalibrationFileTable(this);
		// lstFiles.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(lstFiles);
		pnlFiles.add(scrollPane);
		scrollPane.setViewportBorder(null);

		scrollPane.setPreferredSize(new Dimension(800, 245));
		scrollPane.setMinimumSize(new Dimension(800, 245));
		scrollPane.setAlignmentX(LEFT_ALIGNMENT);
		//
		getRootPane().setDefaultButton(btOK);
		//
		doDialogInit();
		TraceHelper.exit(this, "VNACalibrationExportDialog");
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
		showCentered(getWidth(), getHeight());
		TraceHelper.exit(this, "doInit");
	}

	/**
	 * 
	 * @param showAllFiles
	 */
	private void loadDirectory(final boolean showAllFiles) {
		TraceHelper.entry(this, "loadDirectory");

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
					if (matches) {
						blk.setFile(currFile);
						lstFiles.addCalibrationBlock(blk);
					}
				}
			} catch (ProcessingException e) {
				ErrorLogHelper.exception(this, "loadDirectory", e);
			}
		}
		btOK.setEnabled(false);
		//
		Collections.sort(lstFiles.getModel().getData(), new VNACalibrationBlockComparator());
		//
		lstFiles.updateUI();
		TraceHelper.exit(this, "loadDirectory");
	}


	/*
	 * (non-Javadoc)
	 * @see krause.vna.gui.calibrate.IVNACalibrationSelectionListener#valueChanged(krause.vna.data.calibrated.VNACalibrationBlock, boolean)
	 */
	public void valueChanged(VNACalibrationBlock blk, boolean doubleClick) {
		TraceHelper.entry(this, "valueChanged", "dbl=" + doubleClick);

		final VNADeviceInfoBlock dib = datapool.getDriver().getDeviceInfoBlock();
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
				ErrorLogHelper.exception(this, "valueChanged", e);
				OptionDialogHelper.showExceptionDialog(null, "Serializer.Error.1", "Serializer.Error.2", e);
			}
		} else {
			Toolkit.getDefaultToolkit().beep();
			btOK.setEnabled(false);
		}
		TraceHelper.exit(this, "valueChanged");

	}
}
