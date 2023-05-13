package krause.vna.gui.menu;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;

import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAApplicationState.INNERSTATE;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.gui.VNAMenuAndToolbarHandler;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;

@SuppressWarnings("unused")
public class VNAMenuBar extends JMenuBar implements VNAApplicationStateObserver {
	private transient VNAMenuAndToolbarHandler handler = null;
	private JLabel statusBar = null;
	private VNAConfig config = VNAConfig.getSingleton();

	private JMenu menuAnalyser = null;
	private JMenu menuExport = null;
	private JMenu menuTools = null;
	private JMenu menuCalibrate = null;
	private JMenu menuFile = null;
	private JMenu menuHelp = null;
	private JMenu menuPreset = null;

	public VNAMenuBar(VNAMenuAndToolbarHandler pHandler, JLabel pStatusBar) {

		handler = pHandler;
		statusBar = pStatusBar;
		// register myself
		handler.setMenubar(this);
		// create entries
		add(menuFile = createFileMenu());
		add(menuTools = createToolsMenu());
		add(menuCalibrate = createCalibrationMenu());
		add(menuExport = createExportMenu());
		add(menuAnalyser = createAnalyserMenu());
		add(menuPreset = createPresetsMenu());
		// add(menuExperimental = createExperimentalMenu())
		add(Box.createHorizontalGlue());
		add(menuHelp = createHelpMenu());
	}

	/**
	 * @return
	 */
	private JMenu createPresetsMenu() {
		JMenu rc;
		TraceHelper.entry(this, "createPresetsMenu");
		rc = SwingUtil.createJMenu("Menu.Presets", statusBar); //$NON-NLS-1$
		rc.add(SwingUtil.createJMenuItem("Menu.Presets.Load", handler, statusBar)); //$NON-NLS-1$
		rc.add(SwingUtil.createJMenuItem("Menu.Presets.Save", handler, statusBar)); //$NON-NLS-1$
		TraceHelper.exit(this, "createPresetsMenu");
		return rc;
	}

	public JMenu createExperimentalMenu() {
		final String methodName = "createExperimentalMenu";
		TraceHelper.entry(this, methodName);
		JMenu menu1;
		menu1 = SwingUtil.createJMenu("Menu.Experimental", statusBar); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Experimental.A", handler, statusBar)); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Experimental.B", handler, statusBar)); //$NON-NLS-1$
		TraceHelper.exit(this, methodName);
		return menu1;
	}

	private JMenu createExportMenu() {
		JMenu rc;
		//
		rc = SwingUtil.createJMenu("Menu.Export", statusBar); //$NON-NLS-1$
		rc.add(SwingUtil.createJMenuItem("Menu.Export.CSV", handler, statusBar)); //$NON-NLS-1$
		rc.add(SwingUtil.createJMenuItem("Menu.Export.JPG", handler, statusBar, KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0)));
		rc.add(SwingUtil.createJMenuItem("Menu.Export.PDF", handler, statusBar, KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0)));
		rc.add(SwingUtil.createJMenuItem("Menu.Export.S2P", handler, statusBar)); //$NON-NLS-1$
		rc.add(SwingUtil.createJMenuItem("Menu.Export.S2PCollector", handler, statusBar)); //$NON-NLS-1$
		rc.add(SwingUtil.createJMenuItem("Menu.Export.XLS", handler, statusBar));
		rc.add(SwingUtil.createJMenuItem("Menu.Export.XML", handler, statusBar)); //$NON-NLS-1$
		rc.add(SwingUtil.createJMenuItem("Menu.Export.ZPlot", handler, statusBar)); //$NON-NLS-1$
		rc.addSeparator();
		rc.add(SwingUtil.createJMenuItem("Menu.Export.Setting", handler, statusBar));
		rc.add(SwingUtil.createJMenuItem("Menu.Export.AutoSetting", handler, statusBar)); //$NON-NLS-1$
		return rc;
	}

	private JMenu createToolsMenu() {
		JMenu menu1;
		//
		menu1 = SwingUtil.createJMenu("Menu.Tools", statusBar); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Tools.Beacon", handler, statusBar)); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Tools.Cablelength", handler, statusBar)); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Tools.CableLoss", handler, statusBar)); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Tools.Gaussian", handler, statusBar)); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Tools.Generator", handler, statusBar)); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Schedule.Execute", handler, statusBar)); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Multitune", handler, statusBar)); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Tools.Padcalc", handler, statusBar)); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Tools.FFT", handler, statusBar)); //$NON-NLS-1$

		return menu1;
	}

	private JMenu createCalibrationMenu() {
		JMenu menu1;
		//
		menu1 = SwingUtil.createJMenu("Menu.Calibration", statusBar); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Calibration.Frequency", handler, statusBar)); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Calibration.Calibrate", handler, statusBar)); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Calibration.Load", handler, statusBar)); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Calibration.Import", handler, statusBar)); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Calibration.Export", handler, statusBar)); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Calibration.CalibrationSet", handler, statusBar)); //$NON-NLS-1$
		return menu1;
	}

	private JMenu createHelpMenu() {
		JMenu menu1;
		//
		menu1 = SwingUtil.createJMenu("Menu.Help", statusBar); //$NON-NLS-1$
		menu1.setMnemonic(VNAMessages.getString("MMenu.Help.Key").charAt(0)); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Help.Readme", handler, statusBar)); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Help.Support", handler, statusBar)); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Help.License", handler, statusBar)); //$NON-NLS-1$
		// menu1.addSeparator()
		// menu1.add(SwingUtil.createJMenuItem("Menu.Update", handler, statusBar))
		if (!config.isMac()) {
			menu1.addSeparator();
			menu1.add(SwingUtil.createJMenuItem("Menu.Help.About", handler, statusBar)); //$NON-NLS-1$
		}
		return menu1;
	}

	private JMenu createFileMenu() {
		JMenu menu1;
		//
		menu1 = SwingUtil.createJMenu("Menu.File", statusBar); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.Analysis", handler, statusBar)); //$NON-NLS-1$
		if (!config.isMac()) {
			menu1.add(SwingUtil.createJMenuItem("Menu.File.Settings", handler, statusBar)); //$NON-NLS-1$
		}
		menu1.add(SwingUtil.createJMenuItem("Menu.File.SettingsScales", handler, statusBar)); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.File.Color", handler, statusBar)); //$NON-NLS-1$
		menu1.add(SwingUtil.createJMenuItem("Menu.File.Language", handler, statusBar)); //$NON-NLS-1$
		if (!config.isMac()) {
			menu1.addSeparator();
			menu1.add(SwingUtil.createJMenuItem("Menu.File.Exit", handler, statusBar)); //$NON-NLS-1$
		}
		return menu1;
	}

	/**
	 * 
	 * @return
	 */
	private JMenu createAnalyserMenu() {
		TraceHelper.entry(this, "createAnalyserMenu"); //$NON-NLS-1$

		JMenu rc = SwingUtil.createJMenu("Menu.Analyser", statusBar); //$NON-NLS-1$
		rc.add(SwingUtil.createJMenuItem("Menu.Analyser.Setup", handler, statusBar)); //$NON-NLS-1$
		rc.add(SwingUtil.createJMenuItem("Menu.Analyser.Info", handler, statusBar)); //$NON-NLS-1$
		rc.add(SwingUtil.createJMenuItem("Menu.Analyser.Reconnect", handler, statusBar, KeyStroke.getKeyStroke(KeyEvent.VK_F5, InputEvent.SHIFT_DOWN_MASK)));
		rc.addSeparator();
		rc.add(SwingUtil.createJMenuItem("Menu.Tools.Firmware", handler, statusBar)); //$NON-NLS-1$
		rc.addSeparator();
		if (config.isMac()) {
			rc.add(SwingUtil.createJMenuItem("Menu.Analyser.Single", handler, statusBar, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)));
			rc.add(SwingUtil.createJMenuItem("Menu.Analyser.Free", handler, statusBar, KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0)));
		} else {
			rc.add(SwingUtil.createJMenuItem("Menu.Analyser.Single", handler, statusBar, KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0)));
			rc.add(SwingUtil.createJMenuItem("Menu.Analyser.Free", handler, statusBar, KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0)));
		}

		TraceHelper.exit(this, "createAnalyserMenu"); //$NON-NLS-1$
		return rc;
	}

	public void changeState(INNERSTATE oldState, INNERSTATE newState) {
		if (newState == INNERSTATE.DRIVERLOADED) {
			menuExport.setEnabled(false);
			menuTools.setEnabled(false);
			menuCalibrate.setEnabled(true);
			menuAnalyser.setEnabled(true);
			menuPreset.setEnabled(false);
		} else if (newState == INNERSTATE.CALIBRATED) {
			menuExport.setEnabled(true);
			menuTools.setEnabled(true);
			menuCalibrate.setEnabled(true);
			menuAnalyser.setEnabled(true);
			menuPreset.setEnabled(true);
		} else if (newState == INNERSTATE.RUNNING) {
			menuExport.setEnabled(false);
			menuTools.setEnabled(false);
			menuCalibrate.setEnabled(false);
			menuAnalyser.setEnabled(false);
			menuPreset.setEnabled(false);
		} else {
			menuExport.setEnabled(false);
			menuTools.setEnabled(false);
			menuCalibrate.setEnabled(false);
			menuAnalyser.setEnabled(true);
			menuPreset.setEnabled(false);
		}
	}
}
