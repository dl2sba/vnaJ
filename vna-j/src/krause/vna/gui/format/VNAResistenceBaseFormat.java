/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: VNAResistenceBaseFormat.java
 *  Part of:   vna-j
 */

package krause.vna.gui.format;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * @author Dietmar
 * 
 */
public class VNAResistenceBaseFormat extends NumberFormat {

	private NumberFormat iFormat = null;

	public VNAResistenceBaseFormat() {
		iFormat = NumberFormat.getNumberInstance();
		iFormat.setGroupingUsed(false);
		iFormat.setMaximumFractionDigits(2);
		iFormat.setMinimumFractionDigits(2);
		iFormat.setMaximumIntegerDigits(4);
		iFormat.setMinimumIntegerDigits(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.text.NumberFormat#format(double, java.lang.StringBuffer,
	 * java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
		return iFormat.format(number, toAppendTo, pos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.text.NumberFormat#format(long, java.lang.StringBuffer,
	 * java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
		return iFormat.format(number, toAppendTo, pos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.text.NumberFormat#parse(java.lang.String,
	 * java.text.ParsePosition)
	 */
	@Override
	public Number parse(String source, ParsePosition parsePosition) {
		return iFormat.parse(source, parsePosition);
	}
}
