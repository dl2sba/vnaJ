/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.gui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import com.apple.eawt.AppEventListener;
import com.l2fprod.common.swing.StatusBar;

import krause.common.exception.ProcessingException;
import krause.util.PropertiesHelper;
import krause.util.ResourceLoader;
import krause.util.ras.logging.ApplicationLogHelper;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAApplicationState;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNADriverFactory;
import krause.vna.gui.mac.MacApplicationHandler;
import krause.vna.gui.menu.VNAMenuBar;
import krause.vna.gui.panels.VNADiagramPanel;
import krause.vna.gui.panels.data.VNADataPanel;
import krause.vna.gui.panels.marker.VNAMarkerPanel;
import krause.vna.gui.toolbar.VNAToolbar;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

/**
 * Main class of the application. It is not an Swing or AWT component itself.
 */
public class VNAMainFrame implements ClipboardOwner, AppEventListener {
	private VNAConfig config = VNAConfig.getSingleton();
	private VNADataPool datapool = VNADataPool.getSingleton();

	private VNAApplicationState applicationState = new VNAApplicationState(this);
	private VNADataPanel dataPanel = null;
	private JFrame mainFrame = null;
	private VNAMenuAndToolbarHandler menuAndToolbarHandler = null;
	private VNADiagramPanel diagramPanel = null;
	private VNAMarkerPanel markerPanel = null;
	private StatusBar statusBar = null;
	private VNAToolbar toolbar;
	private VNAMenuBar menubar;

	/**
	 * Protected to avoid instantiation outside the singleton pattern
	 * 
	 * @throws ProcessingException
	 * 
	 */
	protected VNAMainFrame() {
		ApplicationLogHelper.text(this, "VNAMainFrame", "Setting up instance..."); //$NON-NLS-1$ //$NON-NLS-2$
		createAndShowGUI();
		applicationState.evtGUIInitialzed();
		ApplicationLogHelper.text(this, "VNAMainFrame", "Instance setup done!"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void addHotKey(String key, Action action) {
		JRootPane rp = getJFrame().getRootPane();
		InputMap inputMap = rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(key), key);
		rp.getActionMap().put(key, action);
	}

	private void addHotKeys() {
		TraceHelper.entry(this, "addHotKeys");
		addHotKey("F2", new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				getDataPanel().getTxtStartFreq().requestFocus();
			}
		});
		addHotKey("F3", new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				getDataPanel().getTxtStopFreq().requestFocus();
			}
		});
		addHotKey("F4", new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				getDataPanel().getCbMode().requestFocus();
			}
		});

		TraceHelper.exit(this, "addHotKeys");
	}

	/*
	 * 
	 */
	public void changedMode() {
		TraceHelper.entry(this, "changedMode");
		// scan mode set?
		if (datapool.getScanMode() != null) {
			// yes
			// try to get the matching block
			datapool.setMainCalibrationBlock(datapool.getMainCalibrationBlockForMode(datapool.getScanMode()));

			// we have no calibration with data?
			if (datapool.getMainCalibrationBlock() == null) {
				// yes no block
				// show info
				JOptionPane.showMessageDialog(getJFrame(), VNAMessages.getString("Message.ModeChange.1"), VNAMessages //$NON-NLS-1$
						.getString("Message.ModeChange.2"), JOptionPane.INFORMATION_MESSAGE);

				// inform the gang
				applicationState.evtCalibrationUnloaded();
			} else {
				// clear the resized calibration block
				datapool.clearResizedCalibrationBlock();

				// data is available
				applicationState.evtCalibrationLoaded();
			}
		}
		TraceHelper.exit(this, "changedMode");
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
	 */
	private void createAndShowGUI() {
		//
		menuAndToolbarHandler = new VNAMenuAndToolbarHandler(this);

		// create the gui components
		createGUIComponents();

		// verify expiration date
		verifyTTL();

		// add the special keys
		addHotKeys();

		// Display the window.
		getJFrame().pack();

		// restore initial window positions
		config.restoreWindowPosition("MainWindow", getJFrame(), new Point(10, 10));
		config.restoreWindowSize("MainWindow", getJFrame(), new Dimension(1000, 600));

		// show the stuff
		getJFrame().setVisible(true);

		// show version info
		getStatusBarStatus().setText(MessageFormat.format(VNAMessages.getString("Application.welcome"), VNAMessages.getString("Application.version"), VNAMessages.getString("Application.date"), VNAMessages.getString("Application.copyright")));
	}

	/**
	 * check if it is a pre version and if is used too long
	 * 
	 * If so, terminate application after info
	 * 
	 */
	private void verifyTTL() {
		if (VNAMessages.getString("Application.version").endsWith("pre")) {
			GregorianCalendar current = new GregorianCalendar();
			GregorianCalendar expiration = new GregorianCalendar(2021, 5, 30);

			if (current.after(expiration)) {
				Object[] options = {
						VNAMessages.getString("Button.Terminate"), //$NON-NLS-1$

				};
				JOptionPane.showOptionDialog(getJFrame(), "Please use an official version", "vna/J - License for preview version expired", JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
				System.exit(1);
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	private VNADataPanel createDataPanel() {
		VNADataPanel rc;
		TraceHelper.entry(this, "createDataPanel"); //$NON-NLS-1$
		rc = new VNADataPanel(this);
		rc.load();
		applicationState.addStateListener(rc);
		TraceHelper.exit(this, "createDataPanel"); //$NON-NLS-1$
		return rc;
	}

	/**
	 * 
	 * @return
	 */
	private VNADiagramPanel createDiagramPanel() {
		VNADiagramPanel rc;
		TraceHelper.entry(this, "createDiagramPanel"); //$NON-NLS-1$

		rc = new VNADiagramPanel(this);
		applicationState.addStateListener(rc);

		TraceHelper.exit(this, "createDiagramPanel"); //$NON-NLS-1$
		return rc;
	}

	/**
	 * 
	 */
	private void createGUIComponents() {
		final String methodName = "createGUIComponents";
		TraceHelper.entry(this, methodName);
		// Create and set up the window.
		String title = MessageFormat.format(VNAMessages.getString("Application.header"), VNAMessages.getString("Application.version"), System.getProperty("java.version"));
		JFrame frame = new JFrame(title);
		//
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				doShutdown();
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// not relevant
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// not relevant
			}

		});

		// check if we run on MAC OS X
		if (config.isMac()) {
			TraceHelper.text(this, methodName, "running on OS X");
			//
			// https://alvinalexander.com/apple/mac/java-mac-native-look/Putting_your_application_na.shtml
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", title);

			new MacApplicationHandler(this);

			// try {
			// Class util = Class.forName("com.apple.eawt.FullScreenUtilities");
			// Class[] params = new Class[] {
			// Window.class,
			// Boolean.TYPE
			// };
			// Method method = util.getMethod("setWindowCanFullScreen", params);
			// method.invoke(util, frame, true);
			// } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException |
			// InvocationTargetException e) {
			// ErrorLogHelper.exception(this, methodName, e);
			// }

		}
		// resizable if not locked per config
		frame.setResizable(!config.isResizeLocked());

		//
		try {
			byte[] iconBytes = ResourceLoader.getResourceAsByteArray("images/logo.gif");
			Image img = Toolkit.getDefaultToolkit().createImage(iconBytes);
			frame.setIconImage(img);

			// if (config.isMac()) {
			// Application.getApplication().setDockIconImage(img);
			// }
		} catch (IOException ex) {
			ErrorLogHelper.exception(SwingUtil.class, methodName, ex);
		}
		// remember in container
		setJFrame(frame);

		// set the layout
		if (config.isMac()) {
			frame.setLayout(new MigLayout("", "[grow,fill][300px]", "[][grow,fill][][]"));
		} else {
			frame.setLayout(new MigLayout("", "[grow,fill][250px]", "[][grow,fill][][]"));
		}

		// create the toolbar
		toolbar = new VNAToolbar(menuAndToolbarHandler);
		applicationState.addStateListener(toolbar);
		frame.add(toolbar, "span 2,wrap,grow");

		// create the components
		diagramPanel = createDiagramPanel();
		dataPanel = createDataPanel();
		markerPanel = createMarkerPanel();
		statusBar = createStatusPanel();

		// Set the menu bar and add the label to the content pane.
		menubar = new VNAMenuBar(menuAndToolbarHandler, getStatusBarStatus());
		frame.setJMenuBar(menubar);
		applicationState.addStateListener(menubar);

		// create the image in the center
		frame.add(diagramPanel, "");
		frame.add(dataPanel, "span 1 2,top,wrap,growy");
		frame.add(markerPanel, "wrap");
		frame.add(statusBar, "span 2,grow");

		TraceHelper.exit(this, methodName);

	}

	/**
	 * 
	 */
	private VNAMarkerPanel createMarkerPanel() {
		VNAMarkerPanel rc;
		TraceHelper.entry(this, "createMarkerPanel");

		rc = new VNAMarkerPanel(this);
		applicationState.addStateListener(rc);
		rc.setVisible(true);

		TraceHelper.exit(this, "createMarkerPanel");
		return rc;
	}

	/**
	 * 
	 * @return
	 */
	private static StatusBar createStatusPanel() {
		final StatusBar rc = new StatusBar();

		rc.addZone("status", new StatusBarLabel(VNAMessages.getString("Message.Ready"), 50), "30%");
		rc.addZone("driver", new StatusBarLabel("???", 20), "15%");
		rc.addZone("calibStatus", new StatusBarLabel("???", 20), "10%");
		rc.addZone("calibFile", new StatusBarLabel("???", 80), "*");

		return rc;
	}

	/**
	 * Shutdown the application
	 */
	public void doShutdown() {
		TraceHelper.entry(this, "doShutdown");

		config.storeWindowPosition("MainWindow", getJFrame());
		config.storeWindowSize("MainWindow", getJFrame());

		if (config.isAskOnExit()) {
			//
			Object[] options = {
					VNAMessages.getString("Button.Terminate"), //$NON-NLS-1$
					VNAMessages.getString("Button.Cancel") //$NON-NLS-1$
			};
			int n = JOptionPane.showOptionDialog(getJFrame(), VNAMessages.getString("Message.Exit.1"), VNAMessages //$NON-NLS-1$
					.getString("Message.Exit.2"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, //$NON-NLS-1$
					options[0]);
			if (n != 0) {
				return;
			}
		}
		// save individual data of the panels
		getDataPanel().save();

		// now save the datapool
		datapool.save(config);

		// now save the configuration
		config.save();

		// is still a driver loaded?
		if (datapool.getDriver() != null) {
			// yes
			// kill the driver first
			datapool.getDriver().destroy();
		}

		TraceHelper.exit(this, "doShutdown");

		// end everything
		System.exit(0);
	}

	public VNAApplicationState getApplicationState() {
		return applicationState;
	}

	/**
	 * @return the dataPanel
	 */
	public VNADataPanel getDataPanel() {
		return dataPanel;
	}

	/**
	 * @return the diagramPanel
	 */
	public VNADiagramPanel getDiagramPanel() {
		return diagramPanel;
	}

	/**
	 * @return Returns the mainFrame.
	 */
	public JFrame getJFrame() {
		return mainFrame;
	}

	/**
	 * @return the markerPanel
	 */
	public VNAMarkerPanel getMarkerPanel() {
		return markerPanel;
	}

	public VNAMenuAndToolbarHandler getMenuAndToolbarHandler() {
		return menuAndToolbarHandler;
	}

	public VNAMenuBar getMenubar() {
		return menubar;
	}

	public JLabel getStatusBarCalibrationFilename() {
		return (JLabel) statusBar.getZone("calibFile");
	}

	public JLabel getStatusBarCalibrationStatus() {
		return (JLabel) statusBar.getZone("calibStatus");
	}

	public JLabel getStatusBarDriverType() {
		return (JLabel) statusBar.getZone("driver");
	}

	/**
	 * @return Returns the lblStatusBar.
	 */
	public JLabel getStatusBarStatus() {
		return (JLabel) statusBar.getZone("status");
	}

	public VNAToolbar getToolbar() {
		return toolbar;
	}

	/**
	 * 
	 */
	public boolean loadDriver() {
		TraceHelper.entry(this, "loadDriver");
		boolean rc = false;

		try {
			IVNADriver drv = VNADriverFactory.getSingleton().getDriverForType(datapool.getDeviceType());
			datapool.setDriver(drv);
			datapool.setScanMode(drv.getDefaultMode());
			drv.init();
			applicationState.evtDriverLoaded();
			rc = true;
		} catch (ProcessingException e) {
			ErrorLogHelper.exception(this, "setDeviceType", e); //$NON-NLS-1$
			OptionDialogHelper.showExceptionDialog(getJFrame(), "VNAMainFrame.Error.loadDriver.1", "VNAMainFrame.Error.loadDriver.2", e);
		}

		TraceHelper.exit(this, "loadDriver");
		return rc;
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// not used
	}

	/**
	 * Try to load all calibration blocks defined in configuration
	 */
	public void preloadCalibrationBlocks() {
		final String methodName = "preloadCalibrationBlocks";
		TraceHelper.entry(this, methodName);
		try {
			final VNADeviceInfoBlock dib = datapool.getDriver().getDeviceInfoBlock();
			final VNACalibrationKit kit = datapool.getCalibrationKit();
			final String configPath = "CalibrationBlocks." + datapool.getDriver().getDeviceInfoBlock().getShortName() + ".";
			final Properties props = PropertiesHelper.createProperties(config, configPath, true);
			final Enumeration enu = props.keys();

			while (enu.hasMoreElements()) {
				String key = (String) enu.nextElement();
				String filename = props.getProperty(key);
				String pathname = config.getVNACalibrationDirectory() + System.getProperty("file.separator") + filename;
				TraceHelper.text(this, methodName, "Try to load[" + pathname + "]");

				File file = new File(pathname);
				VNACalibrationBlock block = VNACalibrationBlockHelper.load(file, datapool.getDriver(), kit);

				// do we have a match?
				if (block.blockMatches(dib)) {
					// yes
					// store in datapool
					datapool.setMainCalibrationBlockForMode(block);
				}
			}
		} catch (ProcessingException e) {
			ErrorLogHelper.exception(this, methodName, e);
		}
		TraceHelper.exit(this, methodName);
	}

	/**
	 * @param mainFrame
	 *            The mainFrame to set.
	 */
	public void setJFrame(JFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	/**
	 * 
	 * @param pMainCalibrationBlock
	 */
	public void setMainCalibrationBlock(VNACalibrationBlock pMainCalibrationBlock) {
		TraceHelper.entry(this, "setMainCalibrationBlock");
		// exists and old block?
		if (datapool.getMainCalibrationBlock() != null) {
			// yes
			// nullit
			datapool.clearResizedCalibrationBlock();
			// inform the gang
			applicationState.evtCalibrationUnloaded();
		}
		//
		datapool.setMainCalibrationBlock(pMainCalibrationBlock);
		// is there a new one?
		if (datapool.getMainCalibrationBlock() != null) {
			// yes
			//
			datapool.setMainCalibrationBlockForMode(pMainCalibrationBlock);
			datapool.clearResizedCalibrationBlock();

			//
			storePreloadCalibrationBlocks();

			// inform the gang
			applicationState.evtCalibrationLoaded();
		}
		TraceHelper.exit(this, "setMainCalibrationBlock");
	}

	private void storePreloadCalibrationBlocks() {
		TraceHelper.entry(this, "storePreloadCalibrationBlocks");

		Map<String, VNACalibrationBlock> calBlocks = datapool.getMainCalibrationBlocks();
		String configPath = "CalibrationBlocks." + datapool.getDriver().getDeviceInfoBlock().getShortName() + ".";

		for (Map.Entry<String, VNACalibrationBlock> blockEntry : calBlocks.entrySet()) {
			final VNACalibrationBlock block = blockEntry.getValue();

			String configKey = configPath + block.getScanMode().key();
			// is the block stored?
			if (block.getFile() != null) {
				// yes
				// then we remember
				config.put(configKey, block.getFile().getName());
			} else {
				// else we remove the existing key/value
				config.remove(configKey);
			}
		}
		TraceHelper.exit(this, "storePreloadCalibrationBlocks");
	}

	/**
	 * 
	 */
	public void unloadDriver() {
		TraceHelper.entry(this, "unloadDriver");
		//
		if (datapool.getMainCalibrationBlock() != null) {
			// delete all blocks
			datapool.clearCalibrationBlocks();
		}
		if (datapool.getDriver() != null) {
			datapool.getDriver().destroy();
			datapool.setDriver(null);
		}

		//
		applicationState.evtDriverUnloaded();
		//
		TraceHelper.exit(this, "unloadDriver");
	}
}
