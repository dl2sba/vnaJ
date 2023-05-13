package krause.vna.firmware;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.device.IVNAFlashableDevice;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.HelpButton;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAFirmwareUpdateDialog extends KrauseDialog implements ActionListener, IVNABackgroundFlashBurnerConsumer {
	private VNAConfig config = VNAConfig.getSingleton();
	private JTextField txtFilename;
	private JButton btFlash;
	private JButton btClose;
	private JButton btSearch;

	private FirmwareFileParser flashFileParser = null;
	private SimpleStringListbox messageList;
	private VnaBackgroundFlashBurner backgroundBurner;
	private JCheckBox cbAutoReset;

	private final IVNAFlashableDevice currentDriver = (IVNAFlashableDevice) VNADataPool.getSingleton().getDriver();
	private long startTime;

	public VNAFirmwareUpdateDialog(Frame owner) {
		super(owner, true);
		TraceHelper.exit(this, "VNAFirmwareUpdateDialog");
		//
		setResizable(true);
		setPreferredSize(new Dimension(600, 400));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		//
		setConfigurationPrefix("VNAFirmwareUpdateDialog");
		setProperties(config);

		setTitle(VNAMessages.getString("VNAFirmwareUpdateDialog.title"));
		setLayout(new MigLayout("", "[grow][][][]", ""));

		add(new JLabel(VNAMessages.getString("VNAFirmwareUpdateDialog.filename")), "left, span 4, wrap");

		add(txtFilename = new JTextField(128), "span 3");
		txtFilename.setEditable(false);
		add(btSearch = SwingUtil.createJButton("Button.Search", this), "right, wrap");

		//
		messageList = new SimpleStringListbox(VNAMessages.getString("VNAFirmwareUpdateDialog.messages"));
		JScrollPane listScroller = new JScrollPane(messageList);
		listScroller.setPreferredSize(new Dimension(600, 800));
		add(listScroller, "span 4, wrap");

		add(btClose = SwingUtil.createJButton("Button.Close", this), "");
		add(cbAutoReset = SwingUtil.createJCheckbox("AutoReset", this), "left");

		add(new HelpButton(this, "VNAFirmwareUpdateDialog"), "");
		add(btFlash = SwingUtil.createJButton("Button.Install", this), "right,wrap");

		doDialogInit();
		TraceHelper.exit(this, "VNAFirmwareUpdateDialog");
	}

	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		//
		btFlash.setEnabled(false);
		//
		cbAutoReset.setEnabled(currentDriver.hasResetButton());
		cbAutoReset.setSelected(currentDriver.supportsAutoReset());
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
			doFlash();
		} else if (VNAMessages.getString("Button.Search.Command").equals(cmd)) {
			doSearch();
		}

		TraceHelper.exit(this, "actionPerformed");
	}

	/**
	 * 
	 */
	private void doFlash() {
		TraceHelper.entry(this, "doFlash");

		int rc = JOptionPane.YES_OPTION;
		if (cbAutoReset.isSelected()) {
			rc = JOptionPane.showConfirmDialog(this, VNAMessages.getString("VNAFirmwareUpdateDialog.warningNoReset"), VNAMessages.getString("VNAFirmwareUpdateDialog.title"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		} else {
			if (currentDriver.hasResetButton()) {
				rc = JOptionPane.showConfirmDialog(this, VNAMessages.getString("VNAFirmwareUpdateDialog.warning"), VNAMessages.getString("VNAFirmwareUpdateDialog.title"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			} else {
				rc = JOptionPane.showConfirmDialog(this, VNAMessages.getString("VNAFirmwareUpdateDialog.warningNoReset"), VNAMessages.getString("VNAFirmwareUpdateDialog.title"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			}
		}
		if (rc == JOptionPane.YES_OPTION) {
			btClose.setEnabled(false);
			btFlash.setEnabled(false);
			btSearch.setEnabled(false);

			startTime = System.currentTimeMillis();

			backgroundBurner = new VnaBackgroundFlashBurner();
			backgroundBurner.setDataConsumer(this);
			backgroundBurner.setListbox(messageList);
			backgroundBurner.setFlashFile(flashFileParser);
			backgroundBurner.setAutoReset(cbAutoReset.isSelected());
			backgroundBurner.setDriver(VNADataPool.getSingleton().getDriver());
			backgroundBurner.execute();
		} else {
			messageList.addMessage(VNAMessages.getString("VNAFirmwareUpdateDialog.UploadCanceled"));
		}

		TraceHelper.exit(this, "doFlash");
	}

	@Override
	public void consumeReturnCode(Integer rc) {
		TraceHelper.entry(this, "consumeReturnCode");
		//
		btClose.setEnabled(true);
		btFlash.setEnabled(true);
		btSearch.setEnabled(true);
		if (rc == 0) {
			long duration = (System.currentTimeMillis() - startTime) / 1000;
			messageList.addMessage(VNAMessages.getString("VNAFirmwareUpdateDialog.UploadOK") + duration + "s");
		} else {
			messageList.addMessage(VNAMessages.getString("VNAFirmwareUpdateDialog.UploadFailed"));
		}
		//
		TraceHelper.exit(this, "consumeReturnCode");
	}

	private void doSearch() {
		TraceHelper.entry(this, "doSearch");

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				VNADeviceInfoBlock dib = VNADataPool.getSingleton().getDriver().getDeviceInfoBlock();
				return "Firmware (" + dib.getFirmwareFileFilter() + ")";
			}

			@Override
			public boolean accept(File aFile) {
				if (aFile.isDirectory()) {
					return true;
				} else {
					VNADeviceInfoBlock dib = VNADataPool.getSingleton().getDriver().getDeviceInfoBlock();
					WildcardFileFilter wcff = new WildcardFileFilter(dib.getFirmwareFileFilter(), IOCase.SYSTEM);
					return wcff.accept(aFile);
				}
			}
		});
		fc.setCurrentDirectory(new File(config.getFlashFilename()));
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fc.getSelectedFile();
			String sf = selectedFile.getAbsolutePath();
			config.setFlashFilename(sf);
			txtFilename.setText(sf);
			messageList.clear();
			// messageList.addMessage(VNAMessages.getString("VNAFirmwareUpdateDialog.TryToLoad"));
			try {
				flashFileParser = new FirmwareFileParser(selectedFile);
				flashFileParser.parseFile();
				messageList.addMessage(VNAMessages.getString("VNAFirmwareUpdateDialog.FileLoaded"));
				if (flashFileParser.isIntelHexFile()) {
					messageList.addMessage("Flash start address 0x" + Integer.toHexString(flashFileParser.getFlashMin()));
					messageList.addMessage("Flash end   address 0x" + Integer.toHexString(flashFileParser.getFlashMax()));
					messageList.addMessage("Flash memory offset 0x" + Integer.toHexString(flashFileParser.getMemOffset()));
					messageList.addMessage("Flash memory size   0x" + Integer.toHexString(flashFileParser.getMemUsage()));
				} else {
					messageList.addMessage(flashFileParser.getFlash().length + " bytes read from file");
				}
				btFlash.setEnabled(true);
			} catch (ProcessingException e) {
				messageList.addMessage(e.getMessage());
				btFlash.setEnabled(false);
			}
		}
		TraceHelper.exit(this, "doSearch");
	}
}
