package krause.vna.gui.analyse;

import java.text.NumberFormat;

import javax.swing.JComboBox;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataset;

import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.scale.VNAGenericScale;

public class VNATooltipRenderer implements XYToolTipGenerator {
	NumberFormat fmtFreq = VNAFormatFactory.getFrequencyFormat();

	JComboBox combo;

	public VNATooltipRenderer(JComboBox cbScale) {
		combo = cbScale;
	}

	public String generateToolTip(XYDataset arg0, int arg1, int arg2) {
		long freq = (long) arg0.getXValue(arg1, arg2);
		double val = arg0.getYValue(arg1, arg2);
		VNAGenericScale scale = (VNAGenericScale) combo.getSelectedItem();
		StringBuffer rc = new StringBuffer();
		rc.append("<html>f=");
		rc.append(fmtFreq.format(freq));
		rc.append(" Hz<br>");
		rc.append(scale.getName());
		rc.append("=");
		rc.append(scale.getFormat().format(val));
		rc.append(" ");
		rc.append(scale.getUnit());
		rc.append("</html>");
		return rc.toString();
	}

}
