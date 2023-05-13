package krause.vna.gui.reference;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.reference.VNAReferenceDataBlock;
import krause.vna.data.reference.VNAReferenceDataComparator;
import krause.vna.gui.raw.VNARawHandler;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAReferenceDataLoadDialog extends KrauseDialog implements IVNAReferenceDataSelectionListener {
	private static VNAConfig config = VNAConfig.getSingleton();
	private VNADataPool datapool = VNADataPool.getSingleton();

	private VNAReferenceDataTable lstFiles;
	private File currentDirectoy = new File(config.getReferenceDirectory());
	private JButton btCancel;
	private JButton btOK;

	private JLabel lblDirectory;
	private JTextField txtDirectory;
	private JButton btnSearch;
	private JButton btnRefresh;
	private VNAReferenceDataBlock selectedBlock = null;
	private JButton btClear;

	public VNAReferenceDataLoadDialog(Frame pOwner) {
		super(pOwner, true);
		TraceHelper.entry(this, "VNAReferenceDataLoadDialog");

		setConfigurationPrefix("VNAReferenceDataLoadDialog");
		setProperties(config);

		setTitle(VNAMessages.getString("VNAReferenceDataLoadDialog.title"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 800, 333);
		setLayout(new MigLayout("", "[][grow,fill][][]", "[][grow,fill][]"));

		//
		lblDirectory = new JLabel(VNAMessages.getString("VNAReferenceDataLoadDialog.lblDirectory.text"));
		add(lblDirectory, "");
		//
		txtDirectory = new JTextField();
		txtDirectory.setEditable(false);
		txtDirectory.setColumns(30);
		add(txtDirectory, "");
		//
		btnRefresh = new JButton(VNAMessages.getString("VNAReferenceDataLoadDialog.btnRefresh.text"));
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadDirectory();
			}
		});
		add(btnRefresh, "wmin 100px");
		//
		btnSearch = new JButton(VNAMessages.getString("VNAReferenceDataLoadDialog.btnSearch.text"));
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doChangeDirectory();
			}
		});
		add(btnSearch, "wmin 100px, wrap");

		//
		lstFiles = new VNAReferenceDataTable(this);
		JScrollPane scrollPane = new JScrollPane(lstFiles);
		scrollPane.setViewportBorder(null);
		add(scrollPane, "span 4,grow,wrap");

		//
		btClear = SwingUtil.createJButton("Button.Clear", null);
		btClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doClearReference();
			}
		});
		add(btClear, "wmin 100px");

		//
		add(new JLabel(), "");

		// 
		btCancel = SwingUtil.createJButton("Button.Cancel", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doDialogCancel();
			}
		});
		add(btCancel, "wmin 100px");
		//
		btOK = SwingUtil.createJButton("Button.Load", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doOK();
			}
		});
		add(btOK, "wmin 100px, right");

		//
		getRootPane().setDefaultButton(btOK);
		//
		doDialogInit();
		TraceHelper.exit(this, "VNAReferenceDataLoadDialog");
	}

	/**
	 * 
	 */
	protected void doClearReference() {
		TraceHelper.entry(this, "doClearReference");
		selectedBlock = null;
		doOK();
		TraceHelper.exit(this, "doClearReference");
	}

	/**
	 * 
	 */
	protected void doChangeDirectory() {
		TraceHelper.entry(this, "doChangeDirectory");
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(VNAMessages.getString("VNAReferenceDataLoadDialog.directoryChooser"));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setCurrentDirectory(currentDirectoy);
		int returnVal = fc.showOpenDialog(getOwner());

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			currentDirectoy = fc.getSelectedFile();
			loadDirectory();
		}
		TraceHelper.exit(this, "doChangeDirectory");

	}

	protected void doOK() {
		TraceHelper.entry(this, "doOK");
		// use selected reference data
		datapool.setReferenceData(selectedBlock);

		// store selected directory
		config.setReferenceDirectory(currentDirectoy.getAbsolutePath());

		// hide me
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doOK");
	}

	@Override
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doCANCEL");
	}

	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		addEscapeKey();
		loadDirectory();
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	/**
	 * 
	 */
	private void loadDirectory() {
		TraceHelper.entry(this, "loadDirectory");

		txtDirectory.setText(currentDirectoy.getAbsolutePath());

		lstFiles.getModel().clear();

		FilenameFilter fnf = new FilenameFilter() {
			public boolean accept(File dir, String name) {

				return (name.toUpperCase().endsWith(VNARawHandler.RAW_EXTENSION_V2));
			}
		};

		File[] files = currentDirectoy.listFiles(fnf);
		for (int i = 0; i < files.length; i++) {
			File currFile = files[i];
			try {
				VNACalibratedSampleBlock calSampleBlock = null;
				calSampleBlock = new VNARawHandler(this).readFile(currFile);
				if (calSampleBlock != null) {
					VNAReferenceDataBlock blk;
					blk = new VNAReferenceDataBlock(calSampleBlock);
					blk.setFile(currFile);
					lstFiles.addReferenceData(blk);
				}
			} catch (ProcessingException e) {
				ErrorLogHelper.exception(this, "loadDirectory", e);
			}
		}
		btOK.setEnabled(false);
		//
		Collections.sort(lstFiles.getModel().getData(), new VNAReferenceDataComparator());
		//
		lstFiles.updateUI();
		TraceHelper.exit(this, "loadDirectory");
	}

	public void valueChanged(VNAReferenceDataBlock blk, boolean doubleClick) {
		TraceHelper.entry(this, "valueChanged", "dbl=" + doubleClick);

		if (blk != null) {
			selectedBlock = blk;
			btOK.setEnabled(true);

			if (doubleClick) {
				doOK();
			}
		} else {
			btOK.setEnabled(false);
		}
		TraceHelper.exit(this, "valueChanged");
	}

	public VNAReferenceDataBlock getSelectedBlock() {
		return selectedBlock;
	}
}
