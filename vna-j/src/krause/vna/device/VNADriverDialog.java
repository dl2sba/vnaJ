package krause.vna.device;

import javax.swing.JFrame;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.VNAMainFrame;

public abstract class VNADriverDialog extends KrauseDialog {
	protected transient VNAMainFrame mainFrame = null;
	protected transient VNAConfig config = VNAConfig.getSingleton();

	public VNADriverDialog(JFrame frame, VNAMainFrame pMainFrame) {
		super(frame, true);
		final String methodName = "VNADriverDialog";
		TraceHelper.entry(this, methodName);
		mainFrame = pMainFrame;
		TraceHelper.exit(this, methodName);
	}
}
