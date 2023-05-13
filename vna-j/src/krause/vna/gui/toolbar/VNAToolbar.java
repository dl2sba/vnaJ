package krause.vna.gui.toolbar;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JToolBar;

import krause.vna.data.VNAApplicationState.INNERSTATE;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.gui.VNAMenuAndToolbarHandler;
import krause.vna.gui.util.SwingUtil;

public class VNAToolbar extends JToolBar implements VNAApplicationStateObserver {
	private transient VNAMenuAndToolbarHandler handler = null;
	private JButton tbLEN;
	private JButton tbGEN;
	private JButton tbXLS;
	private JButton tbCSV;
	private JButton tbPDF;
	private JButton tbJPG;
	private JButton tbCal;
	private JButton tbCalLoad;
	private JButton tbDriverInfo;
	private JButton tbScheduler;
	private JButton tbAnalysis;
	private JButton tbS2P;
	private JButton tbZPlots;
	private JButton tbMultitune;
	private JButton tbPadcalc;
	private JButton tbXML;

	/**
	 * 
	 * @param pHandler
	 */
	public VNAToolbar(VNAMenuAndToolbarHandler pHandler) {
		setBorder(null);
		setOpaque(false);
		setRollover(true);
		setFloatable(false);
		// store references
		handler = pHandler;
		// register myself
		handler.setToolbar(this);
		// create entries
		add(tbLEN = SwingUtil.createToolbarButton("Menu.Tools.Cablelength", handler));
		add(tbGEN = SwingUtil.createToolbarButton("Menu.Tools.Generator", handler));
		add(tbScheduler = SwingUtil.createToolbarButton("Menu.Schedule.Execute", handler));
		add(tbAnalysis = SwingUtil.createToolbarButton("Menu.Analysis", handler));
		add(tbMultitune = SwingUtil.createToolbarButton("Menu.Multitune", handler));
		add(tbPadcalc = SwingUtil.createToolbarButton("Menu.Tools.Padcalc", handler));
		addSeparator();
		add(tbCal = SwingUtil.createToolbarButton("Menu.Calibration.Calibrate", handler));
		add(tbCalLoad = SwingUtil.createToolbarButton("Menu.Calibration.Load", handler));
		addSeparator();
		add(tbCSV = SwingUtil.createToolbarButton("Menu.Export.CSV", handler));
		add(tbJPG = SwingUtil.createToolbarButton("Menu.Export.JPG", handler));
		add(tbPDF = SwingUtil.createToolbarButton("Menu.Export.PDF", handler));
		add(tbS2P = SwingUtil.createToolbarButton("Menu.Export.S2P", handler));
		add(tbXLS = SwingUtil.createToolbarButton("Menu.Export.XLS", handler));
		add(tbXML = SwingUtil.createToolbarButton("Menu.Export.XML", handler));
		add(tbZPlots = SwingUtil.createToolbarButton("Menu.Export.ZPlot", handler));
		addSeparator();
		add(tbDriverInfo = SwingUtil.createToolbarButton("Menu.Analyser.Info", handler));
		add(Box.createHorizontalGlue());
		add(SwingUtil.createToolbarButton("Menu.File.SettingsScales", handler));
		add(SwingUtil.createToolbarButton("Menu.File.Settings", handler));
		add(SwingUtil.createToolbarButton("Menu.File.Color", handler));
	}

	public void changeState(INNERSTATE oldState, INNERSTATE newState) {

		// things we can always do
		tbPadcalc.setEnabled(true);
		tbAnalysis.setEnabled(true);

		if (newState == INNERSTATE.DRIVERLOADED) {
			tbCSV.setEnabled(false);
			tbJPG.setEnabled(false);
			tbPDF.setEnabled(false);
			tbXLS.setEnabled(false);
			tbXML.setEnabled(false);
			tbS2P.setEnabled(false);
			tbZPlots.setEnabled(false);
			tbLEN.setEnabled(false);
			tbGEN.setEnabled(false);
			tbMultitune.setEnabled(false);
			tbScheduler.setEnabled(false);
			tbCal.setEnabled(true);
			tbCalLoad.setEnabled(true);
			tbDriverInfo.setEnabled(true);
		} else if (newState == INNERSTATE.CALIBRATED) {
			tbCSV.setEnabled(true);
			tbJPG.setEnabled(true);
			tbPDF.setEnabled(true);
			tbXLS.setEnabled(true);
			tbXML.setEnabled(true);
			tbS2P.setEnabled(true);
			tbZPlots.setEnabled(true);
			tbLEN.setEnabled(true);
			tbGEN.setEnabled(true);
			tbMultitune.setEnabled(true);
			tbScheduler.setEnabled(true);
			tbCal.setEnabled(true);
			tbCalLoad.setEnabled(true);
			tbDriverInfo.setEnabled(true);
		} else {
			tbCSV.setEnabled(false);
			tbJPG.setEnabled(false);
			tbPDF.setEnabled(false);
			tbXLS.setEnabled(false);
			tbXML.setEnabled(false);
			tbS2P.setEnabled(false);
			tbZPlots.setEnabled(false);
			tbLEN.setEnabled(false);
			tbGEN.setEnabled(false);
			tbMultitune.setEnabled(false);
			tbCal.setEnabled(false);
			tbCalLoad.setEnabled(false);
			tbScheduler.setEnabled(false);
			tbDriverInfo.setEnabled(false);
		}
	}
}
