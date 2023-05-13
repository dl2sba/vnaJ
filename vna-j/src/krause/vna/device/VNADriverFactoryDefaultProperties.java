package krause.vna.device;

import java.util.Properties;

public class VNADriverFactoryDefaultProperties extends Properties implements VNADriverFactorySymbols {

	{
		put(VNADriverFactory.DRIVER_PREFIX + TYPE_SAMPLE, "krause.vna.device.sample.VNADriverSample");
		put(VNADriverFactory.DRIVER_PREFIX + TYPE_MINIVNA, "krause.vna.device.serial.std.VNADriverSerialStd");
		put(VNADriverFactory.DRIVER_PREFIX + TYPE_MINIVNA2, "krause.vna.device.serial.std2.VNADriverSerialStd2");
		put(VNADriverFactory.DRIVER_PREFIX + TYPE_MININVNAPRO, "krause.vna.device.serial.pro.VNADriverSerialPro");
		put(VNADriverFactory.DRIVER_PREFIX + TYPE_MININVNAPRO_EXT, "krause.vna.device.serial.proext.VNADriverSerialProExt");
		put(VNADriverFactory.DRIVER_PREFIX + TYPE_MAX6, "krause.vna.device.serial.max6.VNADriverSerialMax6");
		put(VNADriverFactory.DRIVER_PREFIX + TYPE_MAX6_500, "krause.vna.device.serial.max6_500.VNADriverSerialMax6_500");
		put(VNADriverFactory.DRIVER_PREFIX + TYPE_MINIVNA_LF, "krause.vna.device.serial.std.lf.VNADriverSerialStdLf");
		put(VNADriverFactory.DRIVER_PREFIX + TYPE_MININVNAPRO_LF, "krause.vna.device.serial.pro.lf.VNADriverSerialProLf");
		put(VNADriverFactory.DRIVER_PREFIX + TYPE_TINYVNA, "krause.vna.device.serial.tiny.VNADriverSerialTiny");
		put(VNADriverFactory.DRIVER_PREFIX + TYPE_METROVNA, "krause.vna.device.serial.metro.VNADriverSerialMetro");
		put(VNADriverFactory.DRIVER_PREFIX + TYPE_VNARDUINO, "krause.vna.device.serial.vnarduino.VNADriverSerialVNArduino");
		put(VNADriverFactory.DRIVER_PREFIX + TYPE_MININVNAPRO2, "krause.vna.device.serial.pro2.VNADriverSerialPro2");
		// put(VNADriverFactory.DRIVER_PREFIX + TYPE_TINYVNA2, "krause.vna.device.serial.tiny2.VNADriverSerialTiny2")
	}
}
