package krause.vna.gui.calibrate;

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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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
import krause.vna.gui.calibrate.file.VNACalibrationFileTable;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNACalibrationSaveDialog extends KrauseDialog implements IVNACalibrationSelectionListener {
	private static VNAConfig config = VNAConfig.getSingleton();
	private VNADataPool datapool = VNADataPool.getSingleton();

	private VNACalibrationFileTable lstFiles;
	private JButton btCancel;
	private JButton btSAVE;
	private JTextField txtFilename;
	private JTextField txtComment;
	private VNACalibrationBlock calibration = null;
	private JPanel panel;

	public VNACalibrationSaveDialog(Window pOwner, VNACalibrationBlock block2save) {
		super(pOwner, true);
		setResizable(true);
		TraceHelper.entry(this, "VNACalibrationSaveDialog");

		setConfigurationPrefix("VNACalibrationSaveDialog");
		setProperties(config);

		calibration = block2save;

		String tit = VNAMessages.getString("VNACalibrationSaveDialog.title");
		setTitle(MessageFormat.format(tit, datapool.getScanMode().toString()));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(800, 200));

		//
		// files
		//
		panel = new JPanel(new MigLayout("", "[][grow,fill][][][]", "[grow,fill][][]"));
		panel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(panel);

		lstFiles = new VNACalibrationFileTable(this);
		JScrollPane scrollPane = new JScrollPane(lstFiles);
		scrollPane.setAlignmentX(LEFT_ALIGNMENT);
		panel.add(scrollPane, "grow, span 6,wrap");

		//
		// filename and button panel
		//
		panel.add(new JLabel(VNAMessages.getString("VNACalibrationSaveDialog.filename")), "");
		txtFilename = new JTextField();
		txtFilename.setColumns(30);
		panel.add(txtFilename, "");

		panel.add(new HelpButton(this, "VNACalibrationSaveDialog"), "wmin 100px");
		btCancel = new JButton(VNAMessages.getString("Button.Cancel"));
		btCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doDialogCancel();
			}
		});
		panel.add(btCancel, "wmin 100px");

		btSAVE = new JButton(VNAMessages.getString("Button.Save"));
		btSAVE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doSAVE();
			}
		});
		panel.add(btSAVE, "wmin 100px, wrap");

		//
		panel.add(new JLabel(VNAMessages.getString("VNACalibrationSaveDialog.comment")), "");
		txtComment = new JTextField();
		txtComment.setColumns(30);
		panel.add(txtComment, "");

		//
		getRootPane().setDefaultButton(btSAVE);
		//
		doDialogInit();
		TraceHelper.exit(this, "VNACalibrationSaveDialog");
	}

	protected void doSAVE() {
		TraceHelper.entry(this, "doSaveCalibration");
		
		//
		calibration.setComment(txtComment.getText());
		
		//
		File newFile = new File(config.getVNACalibrationDirectory(), txtFilename.getText());
		if (newFile.exists()) {
			String msg = MessageFormat.format(VNAMessages.getString("VNACalibrationSaveDialog.fileExists.2"), config.getVNACalibrationDirectory(), txtFilename.getText());
			Object[] options = {
					VNAMessages.getString("Button.Overwrite"),
					VNAMessages.getString("Button.Cancel"),
			};
			int n = JOptionPane.showOptionDialog(getOwner(), msg, VNAMessages.getString("VNACalibrationSaveDialog.fileExists.1"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			if (n == 0) {
				VNACalibrationBlockHelper.save(calibration, newFile.getAbsolutePath());
				setVisible(false);
			}
		} else {
			VNACalibrationBlockHelper.save(calibration, newFile.getAbsolutePath());
			setVisible(false);
		}
		TraceHelper.exit(this, "doSaveCalibration");
	}

	@Override
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doExit");
		setVisible(false);
		TraceHelper.exit(this, "doExit");
	}

	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");

		final StringBuffer sb = new StringBuffer();
		sb.append(calibration.getScanMode().shortText());
		sb.append("_");
		sb.append(datapool.getDriver().getDeviceInfoBlock().getShortName());
		sb.append(".cal");
		txtFilename.setText(sb.toString());

		addEscapeKey();
		loadDirectory(true);

		//
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	private void loadDirectory(final boolean showAllFiles) {
		TraceHelper.entry(this, "loadDirectory");

		VNADeviceInfoBlock dib = datapool.getDriver().getDeviceInfoBlock();

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
					if (blk.blockMatches(dib, datapool.getScanMode())) {
						blk.setFile(currFile);
						lstFiles.addCalibrationBlock(blk);
					}
				}
			} catch (ProcessingException e) {
				ErrorLogHelper.exception(this, "loadDirectory", e);
			}
		}
		//
		Collections.sort(lstFiles.getModel().getData(), new VNACalibrationBlockComparator());
		//
		lstFiles.updateUI();
		TraceHelper.exit(this, "loadDirectory");
	}

	public void valueChanged(VNACalibrationBlock blk, boolean doubleClick) {
		TraceHelper.entry(this, "valueChanged", "dbl=" + doubleClick);
		VNADeviceInfoBlock dib = datapool.getDriver().getDeviceInfoBlock();
		boolean blkMatches = blk.blockMatches(dib, datapool.getScanMode());
		if (blkMatches) {
			txtFilename.setText(blk.getFile().getName());
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
		TraceHelper.exit(this, "valueChanged");

	}
}
