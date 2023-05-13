package krause.vna.gui.cable;

import java.io.FileReader;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.format.VNAFormatFactory;

public class VNAVelocityFactorTableModel extends AbstractTableModel {

	private static final VNAConfig config = VNAConfig.getSingleton();

	private static final NumberFormat nfResistance = VNAFormatFactory.getResistanceFormat();
	private static final NumberFormat nfVelocity = VNAFormatFactory.getVelocityFormat();

	private static final String FILE_SEP = System.getProperty("file.separator");
	private static final String CONFIG_FILE = "cables.csv";
	private static final String CONFIG_PATHNAME = config.getVNAConfigDirectory() + FILE_SEP + CONFIG_FILE;

	private static final List<VNAVelocityFactor> cables = new ArrayList<>();

	private void fillDefaults() {
		TraceHelper.entry(this, "filLDefaults");
		cables.add(new VNAVelocityFactor("5D-2V", 0.67));
		cables.add(new VNAVelocityFactor("8D-2V", 0.67));
		cables.add(new VNAVelocityFactor("5D-FB", 0.80));
		cables.add(new VNAVelocityFactor("8D-FB", 0.80));
		cables.add(new VNAVelocityFactor("3.5D-SFA", 0.83));
		cables.add(new VNAVelocityFactor("5D-SFA", 0.83));
		cables.add(new VNAVelocityFactor("8D-SFA", 0.83));
		cables.add(new VNAVelocityFactor("Aircell5", 50.0, 0.82, "9.40", "100.0", "31.09", "1000.0"));
		cables.add(new VNAVelocityFactor("Aircell7", 50.0, 0.83, "6.28", "100.0", "21.25", "1000.0"));
		cables.add(new VNAVelocityFactor("Aircom Plus", 50.0, 0.83, "3.80", "100.0", "13.40", "1000.0"));
		cables.add(new VNAVelocityFactor("Ecoflex10 Std.", 50.0, 0.83, "4.00", "100.0", "14.20", "1000.0"));
		cables.add(new VNAVelocityFactor("Ecoflex15 Std", 50.0, 0.83, "2.81", "100.0", "9.81", "1000.0"));
		cables.add(new VNAVelocityFactor("Belden 8240", 50.0, 0.660, "4.90", "100.0", "20.0", "1000.0"));
		cables.add(new VNAVelocityFactor("Belden 8267", 50.0, 0.660, "2.20", "100.0", "8.0", "1000.0"));
		cables.add(new VNAVelocityFactor("Belden 8208", 50.0, 0.660, "", "", "8.0", "1000.0"));
		cables.add(new VNAVelocityFactor("Belden 9258", 50.0, 0.780, "3.70", "100.0", "12.8", "1000.0"));
		cables.add(new VNAVelocityFactor("Belden 9880", 50.0, 0.820, "1.30", "100.0", "4.5", "1000.0"));
		cables.add(new VNAVelocityFactor("Belden 9913", 50.0, 0.820, "1.30", "100.0", "4.5", "1000.0"));
		cables.add(new VNAVelocityFactor("Belden 9914", 50.0, 0.660, "", "", "9.0", "1000.0"));
		cables.add(new VNAVelocityFactor("Belden H155A01", 50.0, 0.8, "", "", "9.0", "1000.0"));
		cables.add(new VNAVelocityFactor("Foam (0.100 in. nominal diameter)", 0.66));
		cables.add(new VNAVelocityFactor("Foam (0.195 in. nominal diameter)", 0.75));
		cables.add(new VNAVelocityFactor("Foam (0.240 in. nominal diameter)", 0.83));
		cables.add(new VNAVelocityFactor("Foam (0.300 in. nominal diameter)", 0.83));
		cables.add(new VNAVelocityFactor("Foam (0.400 in. nominal diameter)", 0.85));
		cables.add(new VNAVelocityFactor("Foam (0.500 in. nominal diameter)", 0.86));
		cables.add(new VNAVelocityFactor("Foam (0.600 in. nominal diameter)", 0.87));
		cables.add(new VNAVelocityFactor("H155", 50, 0.81));
		cables.add(new VNAVelocityFactor("H2000 Flex", 50, 0.83));
		cables.add(new VNAVelocityFactor("Hyperflex 5", 50, 0.87, "4.16", "28", "17.00", "430"));
		cables.add(new VNAVelocityFactor("Hyperflex 10", 50, 0.87, "2.07", "28", "8.60", "430"));
		cables.add(new VNAVelocityFactor("Highflexx 7", 50, 0.87, "3.00", "28", "12.30", "430"));
		cables.add(new VNAVelocityFactor("RG-5 /U", 52.5, 0.659, "0.77", "10.0", "2.90", "100"));
		cables.add(new VNAVelocityFactor("RG-5 B/U", 50.0, 0.659, "0.66", "10.0", "2.40", "100.0"));
		cables.add(new VNAVelocityFactor("RG-6 A/U", 75.0, 0.659, "0.78", "10.0", "2.90", "100.0"));
		cables.add(new VNAVelocityFactor("RG-6 Foam", 75.0, 0.780, "5.30", "50.0", "16.20", "500.0"));
		cables.add(new VNAVelocityFactor("RG-8 A/U", 50.0, 0.659, "0.55", "10.0", "2.00", "100.0"));
		cables.add(new VNAVelocityFactor("RG-8 foam", 50.0, 0.800, "1.70", "100.0", "6.0", "1000.0"));
		cables.add(new VNAVelocityFactor("RG-9 /U", 51.0, 0.659, "0.57", "10.0", "2.00", "100.0"));
		cables.add(new VNAVelocityFactor("RG-9 B/U", 50.0, 0.659, "0.61", "10.0", "2.10", "100.0"));
		cables.add(new VNAVelocityFactor("RG-10 A/U", 50.0, 0.659, "0.55", "10.0", "2.00", "100.0"));
		cables.add(new VNAVelocityFactor("RG-11 A/U", 75.0, 0.660, "0.70", "10.0", "2.30", "100.0"));
		cables.add(new VNAVelocityFactor("RG-11 foam", 75.0, 0.780, "3.30", "50.0", "12.10", "500.0"));
		cables.add(new VNAVelocityFactor("RG-12 A/U", 75.0, 0.659, "0.66", "10.0", "2.30", "100.0"));
		cables.add(new VNAVelocityFactor("RG-13 A/U", 75.0, 0.659, "0.66", "10.0", "2.30", "100.0"));
		cables.add(new VNAVelocityFactor("RG-14 A/U", 50.0, 0.659, "0.41", "10.0", "1.40", "100.0"));
		cables.add(new VNAVelocityFactor("RG-16 A/U", 52.0, 0.670, "0.40", "10.0", "1.20", "100.0"));
		cables.add(new VNAVelocityFactor("RG-17 A/U", 50.0, 0.659, "0.23", "10.0", "0.80", "100.0"));
		cables.add(new VNAVelocityFactor("RG-18 A/U", 50.0, 0.659, "0.23", "10.0", "0.80", "100.0"));
		cables.add(new VNAVelocityFactor("RG-19 A/U", 50.0, 0.659, "0.17", "10.0", "0.68", "100.0"));
		cables.add(new VNAVelocityFactor("RG-20 A/U", 50.0, 0.659, "0.17", "10.0", "0.68", "100.0"));
		cables.add(new VNAVelocityFactor("RG-21 A/U", 50.0, 0.659, "4.40", "10.0", "13.00", "100.0"));
		cables.add(new VNAVelocityFactor("RG-29 /U", 53.5, 0.659, "1.20", "10.0", "4.40", "100.0"));
		cables.add(new VNAVelocityFactor("RG-34 A/U", 75.0, 0.659, "0.29", "10.0", "1.30", "100.0"));
		cables.add(new VNAVelocityFactor("RG-34 B/U", 75.0, 0.660, "0.30", "10.0", "1.40", "100.0"));
		cables.add(new VNAVelocityFactor("RG-35 A/U", 75.0, 0.659, "0.24", "10.0", "0.85", "100.0"));
		cables.add(new VNAVelocityFactor("RG-54 A/U", 58.0, 0.659, "0.74", "10.0", "3.10", "100.0"));
		cables.add(new VNAVelocityFactor("RG-55 B/U", 53.5, 0.659, "1.30", "10.0", "4.80", "100.0"));
		cables.add(new VNAVelocityFactor("RG-55 A/U", 50.0, 0.659, "1.30", "10.0", "4.80", "100.0"));
		cables.add(new VNAVelocityFactor("RG-58 /U", 53.5, 0.660, "1.25", "10.0", "4.65", "100.0"));
		cables.add(new VNAVelocityFactor("RG-58 A/U", 53.5, 0.659, "1.25", "10.0", "4.65", "100.0"));
		cables.add(new VNAVelocityFactor("RG-58 C/U", 50.0, 0.659, "1.40", "10.0", "4.90", "100.0"));
		cables.add(new VNAVelocityFactor("RG-58 foam", 53.5, 0.790, "3.80", "100.0", "6.0", "300.0"));
		cables.add(new VNAVelocityFactor("RG-59 A/U", 75.0, 0.659, "1.10", "10.0", "3.40", "100.0"));
		cables.add(new VNAVelocityFactor("RG-59 B/U", 75.0, 0.660, "1.10", "10.0", "3.40", "100.0"));
		cables.add(new VNAVelocityFactor("RG-59 foam", 75.0, 0.790, "3.80", "100.0", "6.0", "300.0"));
		cables.add(new VNAVelocityFactor("RG-62 A/U", 93.0, 0.840, "0.85", "10.0", "2.70", "100.0"));
		cables.add(new VNAVelocityFactor("RG-74 A/U", 50.0, 0.659, "0.38", "10.0", "1.50", "100.0"));
		cables.add(new VNAVelocityFactor("RG-83/U", 35.0, 0.660, "0.80", "10.0", "2.80", "100.0"));
		cables.add(new VNAVelocityFactor("RG-142 B/U", 50, 0.7));
		cables.add(new VNAVelocityFactor("RG-174 A/U", 50.0, 0.660, "3.40", "10.0", "10.60", "100.0"));
		cables.add(new VNAVelocityFactor("RG-178 B/U", 50, 0.7));
		cables.add(new VNAVelocityFactor("RG-179 B/U", 75, 0.7));
		cables.add(new VNAVelocityFactor("RG-188 B/U", 50, 0.7));
		cables.add(new VNAVelocityFactor("RG-213/U", 50.0, 0.660, "0.60", "10.0", "1.90", "100.0"));
		cables.add(new VNAVelocityFactor("RG-218/U", 50.0, 0.660, "0.20", "10.0", "1.00", "100.0"));
		cables.add(new VNAVelocityFactor("RG-220/U", 50.0, 0.660, "0.20", "10.0", "0.70", "100.0"));
		cables.add(new VNAVelocityFactor("RG-316 B/U", 50, 0.7));
		cables.add(new VNAVelocityFactor("SUHNER RG-233/U-01", 50.0, 0.660, "0.20", "10.0", "0.70", "100.0"));
		cables.add(new VNAVelocityFactor("UR-43", 52.0, 0.660, "1.30", "10.0", "4.3", "100.0"));
		cables.add(new VNAVelocityFactor("UR-57", 75.0, 0.660, "0.60", "10.0", "1.9", "100.0"));
		cables.add(new VNAVelocityFactor("UR-63", 75.0, 0.960, "0.15", "10.0", "0.5", "100.0"));
		cables.add(new VNAVelocityFactor("UR-67", 50.0, 0.660, "0.60", "10.0", "2.0", "100.0"));
		cables.add(new VNAVelocityFactor("UR-70", 75.0, 0.660, "1.50", "10.0", "4.9", "100.0"));
		cables.add(new VNAVelocityFactor("UR-74", 51.0, 0.660, "0.30", "10.0", "1.0", "100.0"));
		cables.add(new VNAVelocityFactor("UR-76", 51.0, 0.660, "1.60", "10.0", "5.3", "100.0"));
		cables.add(new VNAVelocityFactor("UR-77", 75.0, 0.660, "0.30", "10.0", "1.0", "100.0"));
		cables.add(new VNAVelocityFactor("UR-79", 50.0, 0.960, "0.16", "10.0", "0.5", "100.0"));
		cables.add(new VNAVelocityFactor("UR-83", 50.0, 0.960, "0.25", "10.0", "0.8", "100.0"));
		cables.add(new VNAVelocityFactor("UR-85", 75.0, 0.960, "0.20", "10.0", "0.7", "100.0"));
		cables.add(new VNAVelocityFactor("UR-90", 75.0, 0.660, "1.10", "10.0", "3.5", "100.0"));
		cables.add(new VNAVelocityFactor("UR-95", 50.0, 0.660, "2.60", "10.0", "8.2", "100.0"));
		TraceHelper.entry(this, "filLDefaults");
	}

	private static final CellProcessor[] cellProcessors = new CellProcessor[] {
			new NotNull(), // name
			new ParseDouble(), // z0
			new Optional(), // f1
			new Optional(), // f1Atten
			new Optional(), // f2
			new Optional(), // f2Atten
			new ParseDouble(), // vf
	};

	/**
	 * 
	 */
	public VNAVelocityFactorTableModel() {
		TraceHelper.entry(this, "VNAVelocityFactorTableModel");
		readCSV();
		TraceHelper.exit(this, "VNAVelocityFactorTableModel");
	}

	/**
	 * 
	 */
	private void readCSV() {
		final String methodName = "readCSV";
		TraceHelper.entry(this, methodName);

		cables.clear();
		try (CsvBeanReader beanReader = new CsvBeanReader(new FileReader(CONFIG_PATHNAME), CsvPreference.STANDARD_PREFERENCE);) {
			// try to read the list

			// the header elements are used to map the values to the bean (names must match)
			final String[] header = beanReader.getHeader(true);

			VNAVelocityFactor cable;
			while ((cable = beanReader.read(VNAVelocityFactor.class, header, cellProcessors)) != null) {
				cables.add(cable);
			}
		} catch (Exception ex) {
			ErrorLogHelper.text(this, methodName, ex.getMessage());
			//
			cables.clear();

			// fill with default cables
			fillDefaults();

			// create CSV file
			createCSV();
		}
		TraceHelper.exit(this, methodName);
	}

	/**
	 * 
	 */
	private void createCSV() {
		final String methodName = "createCSV";
		TraceHelper.entry(this, methodName);

		try (CsvBeanWriter beanWriter = new CsvBeanWriter(new FileWriter(CONFIG_PATHNAME), CsvPreference.STANDARD_PREFERENCE)) {
			// the header elements are used to map the bean values to each column (names must match)
			final String[] header = new String[] {
					"name",
					"z0",
					"f1",
					"attenF1",
					"f2",
					"attenF2",
					"vf",
			};
			// write the header
			beanWriter.writeHeader(header);

			// write the beans
			for (final VNAVelocityFactor cable : cables) {
				beanWriter.write(cable, header, cellProcessors);
			}
		} catch (Exception ex) {
			ErrorLogHelper.exception(this, methodName, ex);
		}
		TraceHelper.exit(this, methodName);
	}

	public int getSize() {
		return cables.size();
	}

	@Override
	public int getColumnCount() {
		return 7;
	}

	@Override
	public int getRowCount() {
		return cables.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		VNAVelocityFactor item = cables.get(row);
		switch (column) {
		case 0:
			return item.getName();

		case 1:
			return nfVelocity.format(item.getVf());

		case 2:
			return nfResistance.format(item.getZ0());

		case 3:
			return item.getF1();

		case 4:
			return item.getAttenF1();

		case 5:
			return item.getF2();

		case 6:
			return item.getAttenF2();

		default:
			return "???";
		}
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Type";

		case 1:
			return "Vf";

		case 2:
			return "Z0";

		case 3:
			return "f1 (MHz)";

		case 4:
			return "loss@f1 (dB/100m)";

		case 5:
			return "f2 (MHz)";

		case 6:
			return "loss@f2 (dB/100m)";

		default:
			return "??";
		}
	}

	public VNAVelocityFactor getDataAtRow(int row) {
		if (row >= 0 && row < cables.size()) {
			return cables.get(row);
		} else {
			return null;
		}
	}
}
