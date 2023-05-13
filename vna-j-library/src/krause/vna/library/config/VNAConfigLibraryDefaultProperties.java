package krause.vna.library.config;

import java.util.Properties;

public class VNAConfigLibraryDefaultProperties extends Properties {

	public VNAConfigLibraryDefaultProperties() {
		put("ErrorLogger.classname", "krause.util.ras.logging.ConsoleErrorLogger");
		put("ErrorLogger.logging", "true");
		put("ErrorLogger.shortclassname", "true");

		put("ApplicationLogger.classname", "krause.util.ras.logging.ConsoleLogger");
		put("ApplicationLogger.logging", "false");
		put("ApplicationLogger.shortclassname", "true");

		put("Tracer.classname", "krause.util.ras.logging.ConsoleTracer");
		put("Tracer.tracing", "false");
		put("Tracer.shortclassname", "true");
	}
}
