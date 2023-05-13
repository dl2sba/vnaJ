package krause.vna.headless;

import java.util.Locale;

import krause.common.exception.InitializationException;
import krause.common.exception.InvalidParameterException;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.LogManager;
import krause.vna.config.VNAConfig;
import krause.vna.config.VNAConfigHeadlessDefaultProperties;
import krause.vna.data.VNADataPool;
import krause.vna.resources.VNAMessages;
import purejavacomm.PureJavaComm;

public class VNAHeadless {

	private static final String P_CONFIGFILE = "configfile";
	private static final String P_CONFIGFILE_DEFAULT = "vna.settings.xml";

	/**
	 * Load the config instance. Then init the logging and the create the GUI.
	 * 
	 * @param args
	 *            No parms needed
	 */
	public static void main(String[] args) {
		try {
			// get name of config file
			String cfn = System.getProperty(P_CONFIGFILE, P_CONFIGFILE_DEFAULT);

			// force init of config instance
			VNAConfig.init(cfn, new VNAConfigHeadlessDefaultProperties());

			VNAConfig config = VNAConfig.getSingleton();

			Locale loc = config.getLocale();
			if (loc != null) {
				Locale.setDefault(loc);
			}
			System.out.println("INFO::Java version...........[" + System.getProperty("java.version") + "]");
			System.out.println("INFO::Java runtime.version...[" + System.getProperty("java.runtime.version") + "]");
			System.out.println("INFO::Java vm.version........[" + System.getProperty("java.vm.version") + "]");
			System.out.println("INFO::Java vm.vendor.........[" + System.getProperty("java.vm.vendor") + "]");
			System.out.println("INFO::OS.....................[" + System.getProperty("os.arch") + " " + System.getProperty("os.name") + " " + System.getProperty("os.version") + "]");
			System.out.println("INFO::Country/Language.......[" + Locale.getDefault().getCountry() + "/" + Locale.getDefault().getLanguage() + "/" + Locale.getDefault().getDisplayVariant() + "]");
			System.out.println("INFO::Application version....[" + VNAMessages.getString("Application.version") + "]");
			System.out.println("INFO::            date ......[" + VNAMessages.getString("Application.date") + "]");
			System.out.println("INFO::User ..................[" + System.getProperty("user.name") + "]");
			System.out.println("INFO::User.home .............[" + System.getProperty("user.home") + "]");
			System.out.println("INFO::User.dir ..............[" + System.getProperty("user.home") + "]");
			System.out.println("INFO::Installation dir ......[" + config.getInstallationDirectory() + "]");
			System.out.println("INFO::Configuration dir .....[" + config.getVNAConfigDirectory() + "]");
			System.out.println("INFO::Configuration file.....[" + config.getPropertiesFileName() + "]");
			System.out.println("INFO::Serial library version [" + PureJavaComm.getVersion() + "/" + PureJavaComm.getFork() + "]");

			// force init of log manager
			LogManager.getSingleton().initialize(VNAConfig.getSingleton());

			// force init of data pool
			VNADataPool.init(VNAConfig.getSingleton());

			// execute the single threaded scan
			new VNAHeadlessRunner().run();

		} catch (InvalidParameterException e) {
			System.exit(1);
		} catch (InitializationException e) {
			System.exit(2);
		} catch (ProcessingException e) {
			System.exit(3);
		}
		System.exit(0);
	}
}
