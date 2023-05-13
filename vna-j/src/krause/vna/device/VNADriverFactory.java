/**
 * Copyright (C) 2020 Dietmar Krause, DL2SBA
 */
package krause.vna.device;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import krause.common.exception.ProcessingException;
import krause.util.PropertiesHelper;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;

public class VNADriverFactory {
	public static final String DRIVER_PREFIX = "Drv.";
	public static final String MENU_PREFIX = "Menu.";

	private static final Properties properties = new Properties();

	private static VNADriverFactory singleton = null;

	private static Map<String, IVNADriver> driverMapByType = new HashMap<>();
	private static Map<String, IVNADriver> driverMapByClassName = new HashMap<>();
	private static Map<String, IVNADriver> driverMapByShortName = new HashMap<>();

	/**
	 * 
	 * @return
	 */
	public List<String> getDriverClassnameList() {
		List<String> rc = new ArrayList<>();
		Properties loc = PropertiesHelper.createProperties(properties, DRIVER_PREFIX);
		Enumeration<Object> keys = loc.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			rc.add(loc.getProperty(key));
		}

		return rc;
	}

	/**
	 * Return the one and only instance of the config object
	 * 
	 * @return the only instance of this class
	 */
	public static synchronized VNADriverFactory getSingleton() {
		if (singleton == null) {
			try {
				singleton = new VNADriverFactory();
				singleton.load();
			} catch (Exception ex) {
				ErrorLogHelper.exception(null, "VNADriverFactory", ex);
			}
		}
		return singleton;
	}

	/**
	 * 
	 */
	private void load() {
		final String methodName = "load";
		TraceHelper.entry(this, methodName);
		properties.putAll(PropertiesHelper.loadXMLProperties("drivers.xml", new VNADriverFactoryDefaultProperties()));
		//
		List<String> drvList = getDriverClassnameList();

		for (String driverClassName : drvList) {
			IVNADriver driver;
			try {
				driver = createDriverForClassname(driverClassName);
				driverMapByClassName.put(driverClassName, driver);
				driverMapByType.put(driver.getDeviceInfoBlock().getType(), driver);
				driverMapByShortName.put(driver.getDeviceInfoBlock().getShortName(), driver);
			} catch (ProcessingException e) {
				ErrorLogHelper.exception(this, methodName, e);
			}
		}
		TraceHelper.exit(this, methodName);
	}

	/**
	 * 
	 * @param vnaDriverType
	 * @return
	 */
	public IVNADriver getDriverForType(String vnaDriverType) {
		TraceHelper.entry(this, "getDriverForType", "" + vnaDriverType);
		IVNADriver driver = driverMapByType.get(vnaDriverType);
		TraceHelper.exit(this, "getDriverForType");
		return driver;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public IVNADriver getDriverForShortName(String name) {
		TraceHelper.entry(this, "getDriverForShortName", "" + name);
		IVNADriver driver = driverMapByShortName.get(name);
		TraceHelper.exitWithRC(this, "getDriverForShortName", driver != null);
		return driver;
	}

	/**
	 * 
	 * @param drvClassName
	 * @return
	 * @throws ProcessingException
	 */
	protected IVNADriver createDriverForClassname(String drvClassName) throws ProcessingException {
		final String methodName = "createDriverForClassname";
		TraceHelper.entry(this, methodName, "class=[%s]", drvClassName);

		IVNADriver driver = null;
		try {
			driver = (IVNADriver) Class.forName(drvClassName.trim()).getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			ErrorLogHelper.exception(this, "createDriverForClassname", e);
			throw new ProcessingException(e);
		}
		TraceHelper.exit(this, methodName);
		return driver;
	}

	/**
	 * @return
	 */
	public List<IVNADriver> getDriverList() {
		List<IVNADriver> rc = new ArrayList<>();
		TraceHelper.entry(this, "getDriverList");
		Iterator<Entry<String, IVNADriver>> it = driverMapByClassName.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, IVNADriver> ent = it.next();
			rc.add(ent.getValue());
		}
		TraceHelper.exit(this, "getDriverList");
		return rc;
	}
}
