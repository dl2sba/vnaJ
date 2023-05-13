package krause.vna.gui.format;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class VNADistanceFormat extends NumberFormat {
	private NumberFormat iFormat = null;

	public VNADistanceFormat() {
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
			return toAppendTo.append(iFormat.format(number * 1E12)).append(" pm");
		} else if (number < 0.000001) {
			return toAppendTo.append(iFormat.format(number * 1E9)).append(" nm");
		} else if (number < 0.001) {
			return toAppendTo.append(iFormat.format(number * 1E6)).append(" um");
		} else if (number < 0.01) {
			return toAppendTo.append(iFormat.format(number * 1E3)).append(" mm");
		} else if (number < 1) {
			return toAppendTo.append(iFormat.format(number * 1E2)).append(" cm");
		} else if (number < 1000) {
			return toAppendTo.append(iFormat.format(number)).append(" m");
		} else {
			return toAppendTo.append(iFormat.format(number / 1E3)).append(" km");
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
