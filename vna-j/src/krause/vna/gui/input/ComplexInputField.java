/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: ComplexInputField.java
 *  Part of:   vna-j
 */

package krause.vna.gui.input;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.math3.complex.Complex;

import krause.common.validation.ComplexValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.resources.VNAMessages;

/**
 * @author Dietmar
 * 
 */
public class ComplexInputField extends JPanel {

	private transient ComplexInputFieldValueChangeListener listener = null;
	private JTextField txtRefR;
	private JTextField txtRefI;
	private Complex value = new Complex(0, 0);
	private Complex minimum = new Complex(-Double.MAX_VALUE, -Double.MAX_VALUE);
	private Complex maximum = new Complex(Double.MAX_VALUE, Double.MAX_VALUE);

	private NumberFormat numberFormat = NumberFormat.getNumberInstance();

	public ComplexInputField(Complex val) {
		TraceHelper.entry(this, "ComplexInputField");

		this.add(new JLabel(VNAMessages.getString("ComplexField.real")));
		txtRefR = new JTextField(5);
		this.add(txtRefR);

		this.add(new JLabel(VNAMessages.getString("ComplexField.img")));
		txtRefI = new JTextField(5);
		this.add(txtRefI);

		if (val != null) {
			value = val;
		}

		complexValueToFields();

		txtRefI.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				JTextField tf = (JTextField) e.getComponent();
				tf.select(0, 999);
			}

			@Override
			public void focusLost(FocusEvent e) {
				Complex old = value;
				ValidationResults results = new ValidationResults();

				double img = ComplexValidator.parseImaginary(txtRefI.getText(), minimum, maximum, VNAMessages.getString("ComplexField.realField"), results);

				if (results.isEmpty()) {
					value = new Complex(old.getReal(), img);
					complexValueToFields();
					if (listener != null) {
						listener.valueChanged(old, value);
					}
				} else {
					new ValidationResultsDialog(null, results, VNAMessages.getString("ComplexField.ErrorDialogHeader"));
				}
			}
		});
		txtRefR.addFocusListener(new FocusAdapter() {
			@Override

			public void focusGained(FocusEvent e) {
				JTextField tf = (JTextField) e.getComponent();
				tf.select(0, 999);
			}

			@Override
			public void focusLost(FocusEvent e) {
				Complex old = value;
				ValidationResults results = new ValidationResults();

				double real = ComplexValidator.parseReal(txtRefR.getText(), minimum, maximum, VNAMessages.getString("ComplexField.imaginaryField"), results);

				if (results.isEmpty()) {
					value = new Complex(real, old.getImaginary());
					complexValueToFields();
					if (listener != null) {
						listener.valueChanged(old, value);
					}
				} else {
					new ValidationResultsDialog(null, results, VNAMessages.getString("ComplexField.ErrorDialogHeader"));
				}
			}
		});
		TraceHelper.exit(this, "ComplexInputField");
	}

	/**
	 * 
	 */
	private void complexValueToFields() {
		TraceHelper.entry(this, "complexValueToFields");
		txtRefI.setText(numberFormat.format(value.getImaginary()));
		txtRefR.setText(numberFormat.format(value.getReal()));
		TraceHelper.exit(this, "complexValueToFields");
	}

	public Complex getComplexValue() {
		return value;
	}

	public void setComplexValue(Complex complexValue) {
		this.value = complexValue;
		complexValueToFields();
	}

	public ComplexInputFieldValueChangeListener getListener() {
		return listener;
	}

	public void setListener(ComplexInputFieldValueChangeListener listener) {
		this.listener = listener;
	}

	public Complex getMinimum() {
		return minimum;
	}

	public void setMinimum(Complex minimum) {
		this.minimum = minimum;
	}

	public Complex getMaximum() {
		return maximum;
	}

	public void setMaximum(Complex maximum) {
		this.maximum = maximum;
	}
}
