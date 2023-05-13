/**
 * Copyright (C) 2007 Dietmar Krause, DL2SBA
 * 
 */
package krause.vna.gui;

import java.util.Locale;

import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.LogManager;
import krause.vna.config.VNAConfig;
import krause.vna.config.VNAConfigDefaultProperties;
import krause.vna.data.VNADataPool;
import krause.vna.gui.laf.VNALookAndFeelHelper;
import krause.vna.resources.VNAMessages;
import purejavacomm.PureJavaComm;

/**
 * Main class to launch the GUI VNA application.
 * 
 * @author Dietmar Krause
 * 
 */
public class VNAMain {
	private static final String P_CONFIGFILE = "configfile";
	private static final String P_CONFIGFILE_DEFAULT = "vna.settings.xml";

	/**
	 * Load the config instance. Then init the logging and the create the GUI.
	 * 
	 * @param args
	 *            No parms needed
	 */
	public static void main(String[] args) {
		if ("Wolfhard".equalsIgnoreCase(System.getProperty("user.name"))) {
			System.out.println("user not supported - bye");
			System.exit(1);
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//
				try {
					// get name of config file
					String cfn = System.getProperty(P_CONFIGFILE, P_CONFIGFILE_DEFAULT);

					// System.setProperty("purejavacomm.loglevel", "255");

					// force init of config instance
					VNAConfig.init(cfn, new VNAConfigDefaultProperties());

					VNAConfig config = VNAConfig.getSingleton();

					Locale loc = config.getLocale();
					if (loc != null) {
						Locale.setDefault(loc);
					}
					System.out.println("INFO::Application version.......[" + VNAMessages.getString("Application.version") + " " + VNAMessages.getString("Application.date") + "]");
					System.out.println("INFO::Java version..............[" + System.getProperty("java.version") + "]");
					System.out.println("INFO::Java runtime.version......[" + System.getProperty("java.runtime.version") + "]");
					System.out.println("INFO::Java vm.version...........[" + System.getProperty("java.vm.version") + "]");
					System.out.println("INFO::Java vm.vendor............[" + System.getProperty("java.vm.vendor") + "]");
					System.out.println("INFO::OS........................[" + System.getProperty("os.arch") + " " + System.getProperty("os.name") + " " + System.getProperty("os.version") + "]");
					System.out.println("INFO::Country/Language..........[" + Locale.getDefault().getCountry() + "/" + Locale.getDefault().getLanguage() + "/" + Locale.getDefault().getDisplayVariant() + "]");
					System.out.println("INFO::                          [" + Locale.getDefault().getDisplayCountry() + "/" + Locale.getDefault().getDisplayLanguage() + "/" + Locale.getDefault().getDisplayVariant() + "]");
					System.out.println("INFO::User .....................[" + System.getProperty("user.name") + "]");
					System.out.println("INFO::User.home ................[" + System.getProperty("user.home") + "]");
					System.out.println("INFO::User.dir .................[" + System.getProperty("user.home") + "]");
					System.out.println("INFO::Installation directory....[" + config.getInstallationDirectory() + "]");
					System.out.println("INFO::Configuration directory...[" + config.getVNAConfigDirectory() + "] overwrite with -Duser.home=XXX");
					System.out.println("INFO::Configuration file........[" + cfn + "] overwrite with -Dconfigfile=YYY");
					System.out.println("INFO::Serial library version ...[" + PureJavaComm.getVersion() + "/" + PureJavaComm.getFork() + "]");

					// force init of log manager
					LogManager.getSingleton().initialize(VNAConfig.getSingleton());

					// force init of data pool
					VNADataPool.init(VNAConfig.getSingleton());

					// setup ui
					try {
						if (config.isMac()) {
							System.setProperty("apple.laf.useScreenMenuBar", "true");
						} else {
							new VNALookAndFeelHelper().setThemeBasedOnConfig();
						}
					} catch (UnsupportedLookAndFeelException e) {
						ErrorLogHelper.exception(this, "main", e);
					}
					// force init of main frame
					new VNAMainFrame();
				} catch (ProcessingException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
