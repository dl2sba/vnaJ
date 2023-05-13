package krause.vna.gui.multiscan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;

import krause.common.TypedProperties;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.scale.VNAMeasurementScale;

public class VNAMultiScanWindow extends KrauseDialog {

	private VNAMainFrame mainFrame;
	private TypedProperties config = VNAConfig.getSingleton();
	private VNAMultiScanControl control;
	private JDesktopPane desktop;
	private VNAMeasurementScale scale;

	public VNAMultiScanWindow(JFrame jFrame, VNAMainFrame pMainFrame, VNAMeasurementScale pScale) {
		super(jFrame, true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("MultiTune ["+pScale.getScale().getName()+"]");
		mainFrame = pMainFrame;
		setScale(pScale);
		
		setBounds(new Rectangle(0, 0, 810, 360));

		//
		// try {
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		// } catch (Exception e) {
		// ErrorLogHelper.exception(this, "VNAMultiScanWindow", e);
		// }

		// create desktop
		Container content = getContentPane();
		content.setBackground(Color.white);
		desktop = new JDesktopPane();
		desktop.setBackground(Color.GRAY);
		content.add(desktop, BorderLayout.CENTER);

		//	create control window
		control = new VNAMultiScanControl(this);
		desktop.add(control);

		doDialogInit();
	}

	@Override
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		TraceHelper.exit(this, "doCANCEL");

	}

	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		config.restoreWindowPosition("MultiTune", this, new Point(100, 100));
		pack();
		config.restoreWindowSize("MultiTune", this, new Dimension(640, 480));
		setVisible(true);
		TraceHelper.exit(this, "doInit");

	}

	@Override
	public void dispose() {
		TraceHelper.entry(this, "dispose");
		config.storeWindowPosition("MultiTune", this);
		config.storeWindowSize("MultiTune", this);
		if (control != null) {
			control.dispose();
		}
		super.dispose();
		TraceHelper.exit(this, "dispose");
	}

	public VNAMainFrame getMainFrame() {
		return mainFrame;
	}

	public TypedProperties getConfig() {
		return config;
	}

	public VNAMultiScanControl getControl() {
		return control;
	}

	public JDesktopPane getDesktop() {
		return desktop;
	}

	public void setScale(VNAMeasurementScale scale) {
		this.scale = scale;
	}

	public VNAMeasurementScale getScale() {
		return scale;
	}
}
