package krause.vna.gui.cable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.gui.format.VNAFormatFactory;

public class VNAPhaseCrossingTableModel extends AbstractTableModel {

	private static NumberFormat nfFrq = VNAFormatFactory.getFrequencyFormat();
	private static NumberFormat nfLoss = VNAFormatFactory.getReflectionLossFormat();

	private static List<VNACalibratedSample> list = new ArrayList<>();

	public int getSize() {
		return list.size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return list.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		VNACalibratedSample item = list.get(row);
		switch (column) {
		case 0:
			return nfFrq.format(item.getFrequency());

		case 1:
			return nfLoss.format(item.getReflectionLoss());

		default:
			return "???";
		}
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Freq. (Hz)";
		case 1:
			return "Loss (dB)";
		default:
			return "???";
		}
	}

	public VNACalibratedSample getDataAtRow(int row) {
		try {
			return list.get(row);
		} catch (IndexOutOfBoundsException e) {
			// nfa
		}
		return null;
	}

	public void clear() {
		list.clear();
	}

	public void add(VNACalibratedSample aSample) {
		list.add(aSample);
	}
}
