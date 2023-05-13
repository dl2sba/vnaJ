package krause.vna.gui.update;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.config.VNASystemConfig;
import krause.vna.gui.HelpButton;
import krause.vna.gui.OptionDialogHelper;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import krause.vna.update.DownloadFile;
import krause.vna.update.FileDownloadJob;
import krause.vna.update.FileDownloadStatusListener;
import krause.vna.update.FileDownloadTask;
import krause.vna.update.UpdateChecker;
import krause.vna.update.UpdateChecker.FILE_TYPE;
import krause.vna.update.UpdateInfoBlock;
import net.miginfocom.swing.MigLayout;

public class VNAUpdateDialog extends KrauseDialog implements ActionListener, FileDownloadStatusListener {
	private VNAConfig config = VNAConfig.getSingleton();
	private JTextField txtCurrentVersion;
	private JTextField txtNewVersion;
	private UpdateChecker updateChecker = new UpdateChecker();
	private UpdateInfoBlock infoBlock;
	private JButton btInstall;
	private VNAUpdateFileTable lstFiles;
	private JTextField txtInstallDir;
	private JTextField txtComment;
	private JButton btClose;
	private JButton btAbort;
	FileDownloadTask backgroundTask = null;
	private JButton btCheck;
	private JButton btSearch;
	private JTextField txtUpdateSite;
	private JButton btPropose;
	private JCheckBox rbAllPlattforms;
	private JButton btReadme;

	public VNAUpdateDialog(Frame owner) {
		super(owner, true);
		TraceHelper.exit(this, "VNAUpdateDialog");
		//
		setResizable(true);
		setPreferredSize(new Dimension(600, 400));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		//
		setConfigurationPrefix("VNAUpdateDialog");
		setProperties(config);

		setTitle(VNAMessages.getString("VNAUpdateDialog.title"));
		setLayout(new MigLayout("", "[][grow][][]", ""));

		add(new JLabel(VNAMessages.getString("VNAUpdateDialog.updateSite")), "");
		add(txtUpdateSite = new JTextField(), "span 3,grow,wrap");
		txtUpdateSite.setColumns(20);
		txtUpdateSite.setEditable(false);

		add(new JLabel(VNAMessages.getString("VNAUpdateDialog.currentVersion")), "");
		add(txtCurrentVersion = new JTextField(), "span 3,grow,wrap");
		txtCurrentVersion.setColumns(20);
		txtCurrentVersion.setEditable(false);

		add(new JLabel(VNAMessages.getString("VNAUpdateDialog.newVersion")), "");
		add(txtNewVersion = new JTextField(VNAMessages.getString("VNAUpdateDialog.unknownVersion")), "span 2,grow");
		txtNewVersion.setColumns(20);
		txtNewVersion.setEditable(false);
		add(btCheck = SwingUtil.createJButton("Button.Check", this), "right,grow,wrap");

		add(new JLabel(VNAMessages.getString("VNAUpdateDialog.comment")), "");
		add(txtComment = new JTextField(), "span 2,grow");
		txtComment.setColumns(20);
		txtComment.setEditable(false);
		add(btReadme = SwingUtil.createJButton("Button.Readme", this), "right,wrap");
		btReadme.setEnabled(false);

		add(new JLabel(VNAMessages.getString("VNAUpdateDialog.filelist")), "");

		lstFiles = new VNAUpdateFileTable();
		JScrollPane listScroller = new JScrollPane(lstFiles);
		listScroller.setPreferredSize(new Dimension(400, 400));
		add(listScroller, "span 3,grow,wrap");

		add(new JLabel(VNAMessages.getString("VNAUpdateDialog.installDir")), "");
		add(txtInstallDir = new JTextField(), "span 3,grow,wrap");
		txtInstallDir.setEditable(false);
		txtInstallDir.setColumns(128);

		//
		add(new JLabel(), "");
		add(btPropose = SwingUtil.createJButton("Button.Propose", this), "left");
		add(rbAllPlattforms = SwingUtil.createJCheckbox("VNAUpdateDialog.allOS", this), "left");
		add(btSearch = SwingUtil.createJButton("Button.Search", this), "right,wrap");

		add(btClose = SwingUtil.createJButton("Button.Close", this), "");
		add(new HelpButton(this, "VNAUpdateDialog"), "");
		add(btAbort = SwingUtil.createJButton("Button.Abort", this), "");
		add(btInstall = SwingUtil.createJButton("Button.Install", this), "right,wrap");

		doDialogInit();
		TraceHelper.exit(this, "VNAUpdateDialog");
	}

	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		//
		txtUpdateSite.setText(VNASystemConfig.getVNA_UPDATEURL());
		txtCurrentVersion.setText(VNAMessages.getString("Application.version"));
		btInstall.setEnabled(false);
		btAbort.setEnabled(false);
		btPropose.setEnabled(false);
		txtInstallDir.setText(config.getInstallationDirectory());

		//
		addEscapeKey();
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doCANCEL");
	}

	public void actionPerformed(ActionEvent e) {
		TraceHelper.entry(this, "actionPerformed");
		String cmd = e.getActionCommand();

		if (VNAMessages.getString("Button.Close.Command").equals(cmd)) {
			doDialogCancel();
		} else if (VNAMessages.getString("Button.Install.Command").equals(cmd)) {
			doINSTALL();
		} else if (VNAMessages.getString("Button.Check.Command").equals(cmd)) {
			doCheck();
		} else if (VNAMessages.getString("Button.Abort.Command").equals(cmd)) {
			doABORT();
		} else if (VNAMessages.getString("Button.Search.Command").equals(cmd)) {
			doUPDATE();
		} else if (VNAMessages.getString("Button.Propose.Command").equals(cmd)) {
			doPROPOSE();
		} else if (VNAMessages.getString("Button.Readme.Command").equals(cmd)) {
			doShowReadme();
		}

		TraceHelper.exit(this, "actionPerformed");
	}

	/**
	 * 
	 */
	private void doShowReadme() {
		TraceHelper.entry(this, "doShowReadme");
		List<DownloadFile> readmeFiles = infoBlock.getFilesForType(FILE_TYPE.README);

		if (readmeFiles != null && readmeFiles.size() > 0) {
			DownloadFile readmeFile = readmeFiles.get(0);
			String url = readmeFile.getRemoteFileName();
			try {
				java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
			} catch (IOException e1) {
				ErrorLogHelper.exception(this, "mouseClicked", e1);
			}
		}
		TraceHelper.exit(this, "doShowReadme");
	}

	/**
	 * 
	 */
	private void doPROPOSE() {
		TraceHelper.entry(this, "doPROPOSE");
		String userDir = System.getProperty("user.dir");
		File f = new File(userDir);
		TraceHelper.text(this, "doPROPOSE", "f=" + f.getAbsolutePath());
		File p = f.getParentFile();
		TraceHelper.text(this, "doPROPOSE", "p=" + p.getAbsolutePath());
		String n = p.getAbsolutePath() + System.getProperty("file.separator") + txtNewVersion.getText();
		txtInstallDir.setText(n);
		TraceHelper.exit(this, "doPROPOSE");
	}

	/**
	 * 
	 */
	private void doABORT() {
		TraceHelper.entry(this, "doABORT");
		if (backgroundTask != null) {
			backgroundTask.abort();
		}
		//
		btAbort.setEnabled(false);
		btClose.setEnabled(true);
		btInstall.setEnabled(true);
		btCheck.setEnabled(true);
		btSearch.setEnabled(true);
		TraceHelper.exit(this, "doABORT");
	}

	private void doINSTALL() {
		TraceHelper.entry(this, "doINSTALL");
		String localDirectory = txtInstallDir.getText();

		// check that the selected directory is not the directory the current
		// application is running
		if (!validateDownloadDirectory(localDirectory)) {
			JOptionPane.showMessageDialog(this, VNAMessages.getString("VNAUpdateDialog.dirErr.1"), VNAMessages.getString("VNAUpdateDialog.dirErr.2"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		// create one instance
		backgroundTask = new FileDownloadTask(this);

		for (FileDownloadJob job : lstFiles.getModel().getJobs()) {
			job.setLocalDirectory(localDirectory);
			backgroundTask.addJob(job);
		}
		backgroundTask.execute();
		//
		btAbort.setEnabled(true);
		btClose.setEnabled(false);
		btInstall.setEnabled(false);
		btCheck.setEnabled(false);
		btSearch.setEnabled(false);
		btPropose.setEnabled(false);
		TraceHelper.exit(this, "doINSTALL");
	}

	/**
	 * @param localDirectory
	 * @return
	 */
	private boolean validateDownloadDirectory(String localDirectory) {
		boolean rc = false;
		TraceHelper.entry(this, "validateDownloadDirectory");

		String userDir = System.getProperty("user.dir");
		File f = new File(userDir);
		TraceHelper.text(this, "validateDownloadDirectory", "f=" + f.getAbsolutePath());

		rc = !f.getAbsolutePath().equalsIgnoreCase(localDirectory);

		TraceHelper.exit(this, "validateDownloadDirectory");
		return rc;
	}

	private void doCheck() {
		TraceHelper.entry(this, "doCheck");
		lstFiles.getModel().clear();
		try {
			infoBlock = updateChecker.readUpdateInfoFile(VNASystemConfig.getVNA_UPDATEURL(), rbAllPlattforms.isSelected());
			if (infoBlock != null) {
				if (infoBlock.getFiles() != null) {
					for (DownloadFile oneFile : infoBlock.getFiles()) {
						FileDownloadJob job = new FileDownloadJob();
						job.setFile(oneFile);
						lstFiles.getModel().addElement(job);
					}

					String remoteVersion = infoBlock.getVersion();
					if (remoteVersion != null) {
						txtNewVersion.setText(remoteVersion);
						txtComment.setText(infoBlock.getComment());
						btPropose.setEnabled(true);
						boolean ok = (txtCurrentVersion.getText().length() > 0) && (txtInstallDir.getText().length() > 0);
						btInstall.setEnabled(ok);
					}

					List<DownloadFile> readme = infoBlock.getFilesForType(FILE_TYPE.README);
					btReadme.setEnabled(readme != null && readme.size() > 0);
				}
			}
		} catch (ProcessingException e) {
			OptionDialogHelper.showExceptionDialog(getOwner(), "VNAUpdateDialog.versionCheckError.title", "VNAUpdateDialog.versionCheckError.message", e);
		}

		TraceHelper.exit(this, "doCheck");
	}

	private void doUPDATE() {
		TraceHelper.entry(this, "doUPDATE");

		File currFile = new File(txtInstallDir.getText());
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(VNAMessages.getString("VNAUpdateDialog.chooseDirectory"));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setCurrentDirectory(currFile);
		int returnVal = fc.showOpenDialog(getOwner());

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String currentDirectory = fc.getSelectedFile().getAbsolutePath();
			txtInstallDir.setText(currentDirectory);
			config.setInstallationDirectory(currentDirectory);
		}
		TraceHelper.exit(this, "doUPDATE");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * krause.vna.update.FileDownloadStatusListener#publishState(krause.vna.
	 * update.FileDownloadJob)
	 */
	public void publishState(FileDownloadJob job) {
		TraceHelper.entry(this, "publishState");
		lstFiles.getModel().updateElement(job);
		TraceHelper.exit(this, "publishState");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.update.FileDownloadStatusListener#done()
	 */
	public void done() {
		TraceHelper.entry(this, "done");
		//
		btAbort.setEnabled(false);
		btClose.setEnabled(true);
		btInstall.setEnabled(true);
		btCheck.setEnabled(true);
		btSearch.setEnabled(true);
		btPropose.setEnabled(true);

		//
		int opt = JOptionPane.showOptionDialog(this, VNAMessages.getString("VNAUpdateDialog.done.1"), VNAMessages.getString("VNAUpdateDialog.done.2"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, //$NON-NLS-1$
				null);
		if (opt == 0) {
			File file = new File(txtInstallDir.getText());
			try {
				java.awt.Desktop.getDesktop().open(file);
			} catch (IOException e1) {
				ErrorLogHelper.exception(this, "actionPerformed", e1);
			}
		}
		TraceHelper.exit(this, "done");
	}
}
