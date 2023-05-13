package krause.vna.gui.format;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class VNAResistenceFormat extends NumberFormat {
	public static final char OMEGA = '\u03A9';

	private NumberFormat baseFormat = new VNAResistenceBaseFormat();

	@Override
	public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
		if (number < 0.000000001) {
			return toAppendTo.append(baseFormat.format(number * 1E12)).append("p").append(OMEGA);
		} else if (number < 0.000001) {
			return toAppendTo.append(baseFormat.format(number * 1E9)).append("n").append(OMEGA);
		} else if (number < 0.001) {
			return toAppendTo.append(baseFormat.format(number * 1E6)).append("u").append(OMEGA);
		} else if (number < 1) {
			return toAppendTo.append(baseFormat.format(number * 1E3)).append("m").append(OMEGA);
		} else if (number < 1E3) {
			return toAppendTo.append(baseFormat.format(number / 1E0)).append("").append(OMEGA);
		} else if (number < 1E6) {
			return toAppendTo.append(baseFormat.format(number / 1E3)).append("k").append(OMEGA);
		} else if (number < 1E9) {
			return toAppendTo.append(baseFormat.format(number / 1E6)).append("M").append(OMEGA);
		} else {
			return toAppendTo;
		}
	}

	@Override
	public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
		return null;
	}

	@Override
	public Number parse(String source, ParsePosition parsePosition) {
		return baseFormat.parse(source, parsePosition);
	}

}
