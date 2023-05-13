package krause.vna.gui.format;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class VNACapacityFormat extends NumberFormat {
	private NumberFormat iFormat = null;

	public VNACapacityFormat() {
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
			return toAppendTo.append(iFormat.format(number * 1E12)).append(" pF");
		} else if (number < 0.000001) {
			return toAppendTo.append(iFormat.format(number * 1E9)).append(" nF");
		} else if (number < 0.001) {
			return toAppendTo.append(iFormat.format(number * 1E6)).append(" uF");
		} else if (number < 1) {
			return toAppendTo.append(iFormat.format(number * 1E3)).append(" mF");
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
