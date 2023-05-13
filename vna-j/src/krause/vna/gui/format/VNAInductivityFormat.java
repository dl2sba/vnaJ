package krause.vna.gui.format;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class VNAInductivityFormat extends NumberFormat {
	private NumberFormat iFormat = null;

	public VNAInductivityFormat() {
		iFormat = NumberFormat.getNumberInstance();
		iFormat.setGroupingUsed(false);
		iFormat.setMaximumFractionDigits(2);
		iFormat.setMinimumFractionDigits(2);
		iFormat.setMaximumIntegerDigits(4);
		iFormat.setMinimumIntegerDigits(1);
	}

	@Override
	public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
		if (number < 0.000000001) {
			return toAppendTo.append(iFormat.format(number * 1E12)).append(" pH");
		} else if (number < 0.000001) {
			return toAppendTo.append(iFormat.format(number * 1E9)).append(" nH");
		} else if (number < 0.001) {
			return toAppendTo.append(iFormat.format(number * 1E6)).append(" uH");
		} else if (number < 1) {
			return toAppendTo.append(iFormat.format(number * 1E3)).append(" mH");
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
		return null;
	}

}
