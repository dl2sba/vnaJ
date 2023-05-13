/**
 * Copyright (C) 2009 Dietmar Krause, DL2SBA
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package krause.util.ras.logging;

import java.util.Properties;

import krause.common.exception.InitializationException;
import krause.util.PropertiesHelper;
import krause.util.component.ManageableComponent;

public class LogManager implements ManageableComponent {
	// =================================================================================
	// Attribute
	// =================================================================================
	public final static String CLASSNAME = "classname";

	public final static String ERRORLOGGER_PREFIX = "ErrorLogger.";

	public final static String ERRORLOGGER_CLASSNAME = ERRORLOGGER_PREFIX + CLASSNAME;

	public final static String ERRORLOGGER_ENABLE = ERRORLOGGER_PREFIX + "logging";

	public final static String APPLOGGER_PREFIX = "ApplicationLogger.";

	public final static String APPLOGGER_CLASSNAME = APPLOGGER_PREFIX + CLASSNAME;

	public final static String APPLOGGER_ENABLE = APPLOGGER_PREFIX + "logging";

	public final static String TRACER_PREFIX = "Tracer.";

	public final static String TRACER_CLASSNAME = TRACER_PREFIX + CLASSNAME;

	public final static String TRACER_ENABLE = TRACER_PREFIX + "tracing";

	// =================================================================================

	// =================================================================================
	// Attribute
	// =================================================================================
	private static LogManager singleton = null;

	private Logger fieldErrorLogger = null;

	private Logger fieldApplicationLogger = null;

	private Tracer fieldTracer = null;

	private boolean errorLoggingEnabled = false;

	private boolean applicationLoggingEnabled = false;

	private boolean tracingEnabled = false;

	// =================================================================================

	// =================================================================================
	// Konstruktor
	// =================================================================================
	/**
	 * Private to avoid foreign instanciation
	 */
	private LogManager() {
	}

	// =================================================================================

	// =================================================================================
	// INTERFACE ManageableComponent
	// =================================================================================
	/**
	 * If a logger implementation is set, the destroy() method is called on it
	 * and the logger is set to null
	 */
	public void destroy() {
		TraceHelper.entry(this, "destroy");
		try {
			if (fieldErrorLogger != null) {
				fieldErrorLogger.destroy();
				fieldErrorLogger = null;
			}
			//
			if (fieldApplicationLogger != null) {
				fieldApplicationLogger.destroy();
				fieldApplicationLogger = null;
			}
			//
			if (fieldTracer != null) {
				fieldTracer.destroy();
				fieldTracer = null;
			}
			singleton = null;
		} catch (Exception ex) {
		}
		System.out.println("LogManager::destroy-exit");
	}

	// ------------------------------------------------------------------------------
	public boolean readyToDestroy() {
		return true;
	}

	// ------------------------------------------------------------------------------
	public void suspendActivity() {
	}

	// ------------------------------------------------------------------------------
	public void resumeActivity() {
	}

	// ------------------------------------------------------------------------------
	public void initialize(Properties theProps) throws InitializationException {
		try {
			//
			String loggerClass = (String) theProps.get(ERRORLOGGER_CLASSNAME);
			if (loggerClass != null) {
				Logger newLogger = (Logger) Class.forName(loggerClass.trim()).getDeclaredConstructor().newInstance();
				//
				newLogger.initialize(PropertiesHelper.createProperties(theProps, ERRORLOGGER_PREFIX, true));
				//
				fieldErrorLogger = newLogger;
				//
				errorLoggingEnabled = "true".equalsIgnoreCase(theProps.getProperty(ERRORLOGGER_ENABLE));
			}
			//
			String appLoggerClass = (String) theProps.get(APPLOGGER_CLASSNAME);
			if (appLoggerClass != null) {
				Logger newLogger = (Logger) Class.forName(appLoggerClass.trim()).getDeclaredConstructor().newInstance();
				//
				newLogger.initialize(PropertiesHelper.createProperties(theProps, APPLOGGER_PREFIX, true));
				//
				fieldApplicationLogger = newLogger;
				//
				applicationLoggingEnabled = "true".equalsIgnoreCase(theProps.getProperty(APPLOGGER_ENABLE));
			}
			//
			String tracerClass = (String) theProps.get(TRACER_CLASSNAME);
			if (tracerClass != null) {
				Tracer newTracer = (Tracer) Class.forName(tracerClass.trim()).getDeclaredConstructor().newInstance();
				//
				newTracer.initialize(PropertiesHelper.createProperties(theProps, TRACER_PREFIX, true));
				//
				fieldTracer = newTracer;
				//
				tracingEnabled = "true".equalsIgnoreCase(theProps.getProperty(TRACER_ENABLE));
			}
		} catch (Exception ex) {
			throw new InitializationException(ex);
		}
	}

	// =================================================================================

	// =================================================================================
	// Getter / Setter
	// =================================================================================
	/**
	 * Method getLogger.
	 * 
	 * @return Logger
	 */
	public Logger getErrorLogger() {
		return fieldErrorLogger;
	}

	/**
	 * Method getApplicationLogger.
	 * 
	 * @return Logger
	 */
	public Logger getApplicationLogger() {
		return fieldApplicationLogger;
	}

	/**
	 * Method getTracer.
	 * 
	 * @return Logger
	 */
	public Tracer getTracer() {
		return fieldTracer;
	}

	/**
	 * Returns a single instance of LogManager. <br>
	 * Implements the singleton-pattern. <br>
	 * If no instance of this class is available, an new instance is created and
	 * the initializeDefault() method is called. <br>
	 * If instanciation fails, simply a stack trace is printed to System.out.
	 * <br>
	 * <b>Implements the singleton-pattern. </b> <br>
	 */
	public static LogManager getSingleton() {
		if (singleton == null) {
			synchronized (LogManager.class) {
				if (singleton == null) {
					try {
						LogManager manager = new LogManager();
						manager.initializeDefault();
						singleton = manager;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		return singleton;
	}

	/**
	 * @see ManageableComponent#initialize(Properties)
	 */
	public void initializeDefault() {
		fieldErrorLogger = new ConsoleLogger();
		fieldApplicationLogger = new ConsoleLogger();
		fieldTracer = new ConsoleTracer();
		errorLoggingEnabled = true;
		applicationLoggingEnabled = true;
		tracingEnabled = true;
	}

	public boolean isApplicationLoggingEnabled() {
		return applicationLoggingEnabled;
	}

	public boolean isErrorLoggingEnabled() {
		return errorLoggingEnabled;
	}

	public void setApplicationLoggingEnabled(boolean applicationLoggingEnabled) {
		this.applicationLoggingEnabled = applicationLoggingEnabled;
	}

	public void setErrorLoggingEnabled(boolean loggingEnabled) {
		this.errorLoggingEnabled = loggingEnabled;
	}

	public void setTracingEnabled(boolean tracingEnabled) {
		this.tracingEnabled = tracingEnabled;
	}

	public boolean isTracingEnabled() {
		return tracingEnabled;
	}
	// =================================================================================

	public String getVersion() {
		return "1.0";
	}
}