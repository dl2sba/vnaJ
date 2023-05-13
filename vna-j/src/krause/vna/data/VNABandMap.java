package krause.vna.data;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.util.VNAFrequencyPair;

public class VNABandMap {
	private static  final VNAConfig config=VNAConfig.getSingleton();
	private static  final List<VNAFrequencyPair> list = new ArrayList<>();
	private static  final String FILE_SEP = System.getProperty("file.separator");
	private static  final String CONFIG_FILE = "bandmap.csv";
	private static  final String CONFIG_PATHNAME = config.getVNAConfigDirectory() + FILE_SEP + CONFIG_FILE;

	private static final CellProcessor[] cellProcessors = new CellProcessor[] {
			new ParseLong(), // f1
			new ParseLong(), // f2
	};

	public List<VNAFrequencyPair> getList() {
		return list;
	}

	public VNABandMap() {
		loadBandmap();
	}

	/**
	 * 
	 */
	private void loadBandmap() {
		TraceHelper.entry(this, "loadBandmap");

		try (CsvBeanReader beanReader = new CsvBeanReader(new FileReader(CONFIG_PATHNAME), CsvPreference.STANDARD_PREFERENCE);) {
			// try to read the list

			// the header elements are used to map the values to the bean (names
			// must match)
			final String[] header = beanReader.getHeader(true);

			VNAFrequencyPair frqPair;
			while ((frqPair = beanReader.read(VNAFrequencyPair.class, header, cellProcessors)) != null) {
				list.add(frqPair);
			}
		} catch (IOException ex) {
			ErrorLogHelper.text(this, "readCSV", ex.getMessage());

			// clear all bad entries
			list.clear();

			//
			loadDefaultBandmap();

			// save Bandmap to file
			saveBandmap();
		}
		TraceHelper.exit(this, "load");
	}

	/**
	 * 
	 */
	private void loadDefaultBandmap() {
		TraceHelper.entry(this, "loadDefaultBandmap");
		list.add(new VNAFrequencyPair(135700, 137800));
		list.add(new VNAFrequencyPair(472000, 479000));
		list.add(new VNAFrequencyPair(1810000, 2000000));
		list.add(new VNAFrequencyPair(3500000, 3800000));
		list.add(new VNAFrequencyPair(7000000, 7200000));
		list.add(new VNAFrequencyPair(10100000, 10150000));
		list.add(new VNAFrequencyPair(14000000, 14350000));
		list.add(new VNAFrequencyPair(18068000, 18168000));
		list.add(new VNAFrequencyPair(21000000, 21450000));
		list.add(new VNAFrequencyPair(24890000, 24990000));
		list.add(new VNAFrequencyPair(28000000, 29700000));
		list.add(new VNAFrequencyPair(50000000, 52000000));
		list.add(new VNAFrequencyPair(70000000, 70500000));
		list.add(new VNAFrequencyPair(144000000, 146000000));
		list.add(new VNAFrequencyPair(430000000, 440000000));
		list.add(new VNAFrequencyPair(1240000000, 1325000000));
		list.add(new VNAFrequencyPair(2310000000l, 2450000000l));
		TraceHelper.exit(this, "loadDefaultBandmap");
	}

	/**
	 * 
	 */
	private void saveBandmap() {
		TraceHelper.entry(this, "saveBandmap");

		try (CsvBeanWriter beanWriter = new CsvBeanWriter(new FileWriter(CONFIG_PATHNAME), CsvPreference.STANDARD_PREFERENCE);) {
			// the header elements are used to map the bean values to each
			// column (names must match)
			final String[] header = new String[] {
					"startFrequency",
					"stopFrequency"
			};
			// write the header
			beanWriter.writeHeader(header);

			// write the beans
			for (final VNAFrequencyPair frqPair : list) {
				beanWriter.write(frqPair, header, cellProcessors);
			}
		} catch (IOException ex) {
			ErrorLogHelper.exception(this, "saveBandmap", ex);
		}
		TraceHelper.exit(this, "saveBandmap");
	}
}
