package krause.vna.gui.util;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JTextField;
import javax.swing.SwingConstants;

import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;

public class RangeCheckedTextField extends JTextField implements FocusListener {
	private static final Dimension DEFAULT_SIZE = new Dimension(100, 20);

	private double lowerLimit = 1000000;

	private double upperLimit = 100000000;

	private boolean validData = false;

	private boolean emptyValid = false;

	private NumberFormat formatToUse = null;

	public RangeCheckedTextField(NumberFormat fmt) {
		this(fmt, false);
	}

	public RangeCheckedTextField(NumberFormat fmt, boolean emptyValid) {
		//
		setHorizontalAlignment(SwingConstants.TRAILING);
		setMinimumSize(DEFAULT_SIZE);
		setMaximumSize(getMinimumSize());
		setPreferredSize(getMinimumSize());
		setEmptyValid(emptyValid);

		//
		this.formatToUse = fmt;
		//
		setInputVerifier(new RangeCheckInputVerifier(formatToUse, isEmptyValid()));
		//
		addFocusListener(this);
	}

	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public void setUpperLimit(double upperLimit) {
		this.upperLimit = upperLimit;
	}

	/**
	 * @return Returns the lowerLimit.
	 */
	public double getLowerLimit() {
		return lowerLimit;
	}

	/**
	 * @return Returns the upperLimit.
	 */
	public double getUpperLimit() {
		return upperLimit;
	}

	/**
	 * 
	 * @param f
	 */
	public void setValue(Double f) {
		if (f != null) {
			setText(NumberFormat.getNumberInstance().format(f));
		} else {
			setText("");
		}
	}

	/**
	 * 
	 * @return
	 */
	public Double getValue() {
		Double rc = null;
		String text = getText().trim();

		if (text.length() == 0 && this.emptyValid) {
		} else {
			try {
				rc = NumberFormat.getNumberInstance().parse(text).doubleValue();
			} catch (ParseException e) {
				ErrorLogHelper.exception(this, "getValue", e);
			}
		}
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	public void focusGained(FocusEvent e) {
		TraceHelper.entry(this, "focusGained");
		RangeCheckedTextField tf = (RangeCheckedTextField) e.getComponent();
		tf.select(0, 999);
		TraceHelper.exit(this, "focusGained");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	public void focusLost(FocusEvent e) {
		// nfa
	}

	/**
	 * @return Returns the validData.
	 */
	public boolean isValidData() {
		return validData;
	}

	/**
	 * @param validData
	 *            The validData to set.
	 */
	public void setValidData(boolean validData) {
		this.validData = validData;
	}

	public boolean isEmptyValid() {
		return emptyValid;
	}

	public void setEmptyValid(boolean emptyValid) {
		this.emptyValid = emptyValid;
	}
}
