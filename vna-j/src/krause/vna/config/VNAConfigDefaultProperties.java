package krause.vna.config;

import java.util.Properties;

import krause.vna.device.VNADriverFactoryDefaultProperties;

public class VNAConfigDefaultProperties extends Properties {

	public VNAConfigDefaultProperties() {
		put("ErrorLogger.classname", "krause.util.ras.logging.ConsoleErrorLogger");
		put("ErrorLogger.logging", "true");
		put("ErrorLogger.shortclassname", "true");

		put("ApplicationLogger.classname", "krause.util.ras.logging.ConsoleLogger");
		put("ApplicationLogger.logging", "false");
		put("ApplicationLogger.shortclassname", "true");

		put("Tracer.classname", "krause.util.ras.logging.ConsoleTracer");
		put("Tracer.tracing", "false");
		put("Tracer.shortclassname", "true");

		put("askOnExit", "false");
		put("showToolbar", "true");
		put(VNASystemConfig.VNA_PREFIX + "type", "" + VNADriverFactoryDefaultProperties.TYPE_SAMPLE);
		put("VNADriver.Sample.PortName", "DummySamplePort");
		
		put(VNASystemConfig.VNA_PREFIX + "exportFileName", "VNA_{0,date,yyMMdd}_{0,time,HHmmss}");
		put(VNASystemConfig.VNA_PREFIX + "exportComment", "Date:        {0}\nMode:        {1}\nAnalyser:    {2} / {3}\nScan\n   Start:    {4} / {6}\n   Stop:     {5} / {7}\n   Samples:  {8}\n   Overscan: {9}\nCalibration\n   Samples:  {10}\n   Overscan: {11}\n   File:     {12}\nUser:        {13}\nHeadline:     {14}\nPort extension len: {15}m\nPort extension vf: {16}");
		put(VNASystemConfig.VNA_PREFIX + "exportTitle", "{2} - {0,date,yyMMdd}_{0,time,HHmmss}");
	}
}
