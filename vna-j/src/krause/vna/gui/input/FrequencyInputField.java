package krause.vna.gui.input;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;

import javax.swing.JTextField;
import javax.swing.SwingConstants;

import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.resources.VNAMessages;

public class FrequencyInputField extends JTextField implements FocusListener {
	public static final int MAX_LEN = 9;

	public static final Dimension DEFAULT_SIZE = new Dimension(100, 20);

	private long lowerLimit = 1000000;

	private long upperLimit = 9999999999l;

	private boolean validData = false;

	public FrequencyInputField(String name, long defaultValue) {
		//
		setHorizontalAlignment(SwingConstants.TRAILING);
		setFrequency(defaultValue);
		//
		addFocusListener(this);
		//
		setToolTipText(VNAMessages.getString("Input.Frq.1"));
		//
		setName(name);
		setInputVerifier(new FrequencyInputVerifier());
	}

	public void setLowerLimit(long lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public void setUpperLimit(long upperLimit) {
		this.upperLimit = upperLimit;
	}

	public FrequencyInputField(String name, long defaultValue, long lowLimit, long highLimit) {
		super(MAX_LEN);
		//
		setHorizontalAlignment(SwingConstants.TRAILING);
		setFrequency(defaultValue);
		//
		addFocusListener(this);
		//
		setToolTipText(VNAMessages.getString("Input.Frq.1"));
		//
		setName(name);
		lowerLimit = lowLimit;
		upperLimit = highLimit;
		setInputVerifier(new FrequencyInputVerifier());
	}

	/**
	 * @return Returns the lowerLimit.
	 */
	public long getLowerLimit() {
		return lowerLimit;
	}

	/**
	 * @return Returns the upperLimit.
	 */
	public long getUpperLimit() {
		return upperLimit;
	}

	/**
	 * 
	 * @param f
	 */
	public void setFrequency(long f) {
		setText(VNAFormatFactory.getFrequencyFormat().format(f));
		setValidData(true);
	}

	/**
	 * 
	 * @return
	 */
	public long getFrequency() {
		long rc = 0;
		try {
			rc = VNAFormatFactory.getFrequencyFormat().parse(getText()).longValue();
		} catch (ParseException e) {
			ErrorLogHelper.exception(this, "getFrequency", e);
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
		FrequencyInputField tf = (FrequencyInputField) e.getComponent();
		tf.select(0, 999);
		TraceHelper.exit(this, "focusGained");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	public void focusLost(FocusEvent e) {
		TraceHelper.entry(this, "focusLost");
		TraceHelper.exit(this, "focusLost");
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
}
