package krause.vna.gui.mac;

import java.awt.Desktop;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.PreferencesEvent;
import java.awt.desktop.PreferencesHandler;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;

import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.VNAMainFrame;

/**
 * Similiar implementation
 * 
 * https://alvinalexander.com/source-code/java-macos-about-preferences-quit-handlers-desktop-awt/
 * 
 * @author dietmar
 *
 */
public class MacApplicationHandler implements QuitHandler, AboutHandler, PreferencesHandler {

	private VNAMainFrame mainframe = null;

	public MacApplicationHandler(VNAMainFrame vnaMainFrame) {
		final String methodName = "MacApplicationHandler";
		TraceHelper.entry(this, methodName);
		this.mainframe = vnaMainFrame;
		Desktop desktop = Desktop.getDesktop();
		desktop.setQuitHandler(this);
		desktop.setAboutHandler(this);
		desktop.setPreferencesHandler(this);
		TraceHelper.exit(this, methodName);
	}

	@Override
	public void handleQuitRequestWith(QuitEvent arg0, QuitResponse arg1) {
		TraceHelper.entry(this, "handleQuitRequestWith");
		this.mainframe.doShutdown();
		arg1.cancelQuit();
		TraceHelper.exit(this, "handleQuitRequestWith");
	}

	@Override
	public void handleAbout(AboutEvent arg0) {
		TraceHelper.entry(this, "handleAbout");
		this.mainframe.getMenuAndToolbarHandler().doAboutDialog();
		TraceHelper.exit(this, "handleAbout");
	}

	@Override
	public void handlePreferences(PreferencesEvent arg0) {
		TraceHelper.entry(this, "handlePreferences");
		this.mainframe.getMenuAndToolbarHandler().doConfigDialog();
		TraceHelper.exit(this, "handlePreferences");
	}
}
