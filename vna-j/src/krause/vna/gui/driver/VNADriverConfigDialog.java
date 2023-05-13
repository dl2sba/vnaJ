package krause.vna.gui.driver;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.IVNABackgroundTaskStatusListener;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADriverFactory;
import krause.vna.device.VNADriverNameComparator;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNADriverConfigDialog extends KrauseDialog implements IVNABackgroundTaskStatusListener {
	private static VNAConfig config = VNAConfig.getSingleton();
	private static VNADataPool datapool = VNADataPool.getSingleton();

	private JButton btOK;
	private JButton btTest;
	private JList driverList;
	private JList portList;

	private IVNADriver selectedDriver = null;
	private String selectedPort = null;

	private JLabel statusBar;

	private VNAMainFrame mainFrame;
	private IVNADriver currentlyLoadedDriver = null;
	private JCheckBox cbNoFilter;
	private JCheckBox cbWireless;

	public VNADriverConfigDialog(VNAMainFrame pMainFrame) {
		super(pMainFrame.getJFrame(), true);

		mainFrame = pMainFrame;
		TraceHelper.entry(this, "VNADriverConfigDialog");

		setResizable(true);
		setConfigurationPrefix("VNADriverConfigDialog");
		setProperties(config);
		setTitle(VNAMessages.getString("VNADriverConfigDialog.title"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLayout(new MigLayout("", "[][grow,fill]", "[grow,fill][][]"));
		setPreferredSize(new Dimension(720, 370));

		add(createDriverPanel(), "");
		add(createPortPanel(), "wrap");
		add(createStatusPanel(), "span 2,grow,wrap");
		add(createButtonPanel(), "span 2,grow,wrap");

		//
		getRootPane().setDefaultButton(btOK);
		//
		doDialogInit();

		TraceHelper.exit(this, "VNADriverConfigDialog");
	}

	private Component createStatusPanel() {
		JPanel rc = new JPanel(new MigLayout("", "[grow,fill]", "[grow,fill]"));
		rc.setBorder(new TitledBorder(null, VNAMessages.getString("VNADriverConfigDialog.status"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		statusBar = new JLabel();

		rc.add(statusBar, "");
		return rc;
	}

	private Component createButtonPanel() {
		JPanel rc = new JPanel(new MigLayout("", "[grow][grow][grow][grow][grow]", "[]"));

		// create the buttons
		JButton btCancel = SwingUtil.createJButton("Button.Cancel", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doDialogCancel();
			}
		});
		rc.add(btCancel, "left");

		cbNoFilter = SwingUtil.createJCheckbox("Button.Filter", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doFilter();
			}
		});
		rc.add(cbNoFilter, "left");

		cbWireless = SwingUtil.createJCheckbox("Button.Wireless", null);
		rc.add(cbWireless, "left");

		btTest = SwingUtil.createJButton("Button.Test", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doTest();
			}
		});
		btTest.setEnabled(false);
		rc.add(btTest, "center");

		rc.add(new HelpButton(this, "VNADriverConfigDialog"), "center");

		btOK = SwingUtil.createJButton("Button.Update", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doOK();
			}
		});
		btOK.setEnabled(false);
		rc.add(btOK, "right");

		return rc;
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void fillDriverList() {
		TraceHelper.entry(this, "fillDriverList");

		List<IVNADriver> availableDrivers = VNADriverFactory.getSingleton().getDriverList();

		// sort it
		Collections.sort(availableDrivers, new VNADriverNameComparator());

		Vector<String> drvVector = new Vector<>(availableDrivers.size());

		for (IVNADriver driver : availableDrivers) {
			drvVector.add(driver.getDeviceInfoBlock().getShortName());
		}

		driverList.setListData(drvVector);

		TraceHelper.exit(this, "fillDriverList");
	}

	private Component createDriverPanel() {
		JPanel rc = new JPanel(new MigLayout("", "[grow,fill]", "[grow,fill][]"));
		rc.setBorder(new TitledBorder(null, VNAMessages.getString("VNADriverConfigDialog.drvList"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		driverList = new JList();
		driverList.setVisibleRowCount(6);
		driverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		driverList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				handleDriverListSelection(e);
			}
		});

		JScrollPane sp = new JScrollPane(driverList);
		rc.add(sp, "wrap");

		rc.add(new JLabel(VNAMessages.getString("VNADriverConfigDialog.drvUsage")), "");
		return rc;
	}

	private Component createPortPanel() {
		JPanel rc = new JPanel(new MigLayout("", "[grow,fill]", "[grow,fill][]"));
		rc.setBorder(new TitledBorder(null, VNAMessages.getString("VNADriverConfigDialog.portLIst"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		portList = new JList();
		portList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				handlePortListSelection(e);
			}
		});

		JScrollPane sp = new JScrollPane(portList);
		rc.add(sp, "wrap");

		rc.add(new JLabel(VNAMessages.getString("VNADriverConfigDialog.portUsage")), "");

		return rc;
	}

	@Override
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");

		// was initialy a driver loaded?
		if (currentlyLoadedDriver != null) {
			// yes
			// reload it
			mainFrame.loadDriver();
		}
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doCANCEL");
	}

	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");

		// create the driver list
		fillDriverList();

		//
		currentlyLoadedDriver = selectedDriver = datapool.getDriver();
		// is there a driver already loaded ?
		if (currentlyLoadedDriver != null) {
			// yes
			// first of all unload current driver
			mainFrame.unloadDriver();

			// select the driver in the list
			driverList.setSelectedValue(currentlyLoadedDriver.getDeviceInfoBlock().getShortName(), true);
		}

		// do ui stuff
		addEscapeKey();
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	protected void doOK() {
		TraceHelper.entry(this, "doOK");

		// write the selected port to the config
		config.setPortName(selectedDriver, selectedPort);

		// write to data storage
		datapool.setDeviceType(selectedDriver.getDeviceInfoBlock().getType());

		// load the new driver
		mainFrame.loadDriver();

		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doOK");
	}

	protected void doFilter() {
		handleDriverListSelection(null);
	}

	@SuppressWarnings("unchecked")
	private void fillPortList() throws ProcessingException {
		TraceHelper.entry(this, "fillPortList");

		List<String> ports = selectedDriver.getPortList();

		Vector<String> portVector = new Vector<>(ports.size());

		boolean filter = !cbNoFilter.isSelected();
		if (filter) {
			TraceHelper.text(this, "fillPortList", "Filtering serial ports ...");
		}

		for (String port : ports) {
			if (filter) {
				boolean useIt = false;
				// on mac ?
				if (config.isMac()) {
					// yes
					if (port.startsWith("cu")) {
						useIt = true;
					}
					// on Windows
				} else if (config.isWindows()) {
					// yes
					// add everything
					useIt = true;
				} else {
					// on all other platforms
					// tty interface
					if ("tty".equals(port)) {
						// yes
						// ignore
					} else if (port.startsWith("tty")) {
						// ignore all tty<nn> interfaces
						String sName = port.substring(3);
						try {
							Integer.parseInt(sName);
						} catch (NumberFormatException e) {
							useIt = true;
						}
					} else {
						// simply add
						useIt = true;
					}
				}
				if (useIt) {
					portVector.add(port);
				} else {
					TraceHelper.text(this, "fillPortList", "port [" + port + " filtered");
				}
			} else {
				portVector.add(port);
			}
		}

		Collections.sort(portVector);

		portList.setListData(portVector);
		TraceHelper.exit(this, "fillPortList");
	}

	protected void handleDriverListSelection(ListSelectionEvent e) {
		TraceHelper.entry(this, "handleDriverListSelection");
		if ((e != null) && (e.getValueIsAdjusting())) {
			return;
		}

		String selDrv = (String) driverList.getSelectedValue();
		TraceHelper.text(this, "handleDriverListSelection", "drv=" + selDrv);

		try {
			btOK.setEnabled(false);
			selectedDriver = VNADriverFactory.getSingleton().getDriverForShortName(selDrv);
			if (selectedDriver != null) {
				fillPortList();

				selectedPort = config.getPortName(selectedDriver);
				if (selectedPort != null) {
					portList.setSelectedValue(selectedPort, true);
				}
			}
		} catch (ProcessingException e1) {
			// ignore it here
		}

		setStatusbar(Color.RED, "");
		TraceHelper.exit(this, "handleDriverListSelection");
	}

	protected void handlePortListSelection(ListSelectionEvent e) {
		if (e.getValueIsAdjusting())
			return;
		TraceHelper.entry(this, "handlePortListSelection");
		selectedPort = (String) portList.getSelectedValue();
		TraceHelper.text(this, "handlePortListSelection", "drv=" + selectedPort);

		btTest.setEnabled(selectedPort != null);
		setStatusbar(Color.RED, "");

		TraceHelper.exit(this, "handlePortListSelection");
	}

	private void setStatusbar(Color col, String text) {
		statusBar.setBackground(col);
		statusBar.setOpaque(true);
		statusBar.setText(text);
	}

	/**
	 * test the selected driver
	 */
	private void doTest() {
		TraceHelper.entry(this, "doTest");
		String oldPortName = null;

		btOK.setEnabled(false);
		btTest.setEnabled(false);

		// save old stuff
		oldPortName = config.getPortName(selectedDriver);

		// set new stuff
		config.setPortName(selectedDriver, selectedPort);

		// let the driver check
		if (selectedDriver.checkForDevicePresence(cbWireless.isSelected())) {
			setStatusbar(Color.GREEN, VNAMessages.getString("VNADriverConfigDialog.statusOK"));
			btOK.setEnabled(true);
		} else {
			setStatusbar(Color.RED, VNAMessages.getString("VNADriverConfigDialog.statusFAIL1"));
		}
		btTest.setEnabled(true);
		if (oldPortName != null) {
			config.setPortName(selectedDriver, oldPortName);
		}
		TraceHelper.exit(this, "doTest");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * krause.vna.background.IVNABackgroundTaskStatusListener#publishProgress(
	 * int)
	 */
	@Override
	public void publishProgress(int percentage) {
		// nothing happens here in this dialogs
	}
}
