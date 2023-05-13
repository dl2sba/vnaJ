package krause.vna.gui.input;

import java.awt.Toolkit;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.resources.VNAMessages;

public class FrequencyInputVerifier extends InputVerifier {
	private String message = "";

	public boolean shouldYieldFocus(JComponent input) {
		boolean inputOK = verify(input);
		if (inputOK) {
			return true;
		}

		// Avoid possible focus-transfer problems when bringing up
		// the dialog by temporarily removing the input verifier.
		// This is a workaround for bug #4532517.
		input.setInputVerifier(null);

		// Pop up the message dialog.
		message += VNAMessages.getString("Input.Frq.3");
		JOptionPane.showMessageDialog(input.getParent().getParent(), message, VNAMessages.getString("Input.Frq.4"), JOptionPane.WARNING_MESSAGE);

		// Reinstall the input verifier.
		input.setInputVerifier(this);

		// Beep and then tell whoever called us that we don't
		// want to yield focus.
		Toolkit.getDefaultToolkit().beep();
		return false;
	}

	/**
	 * 
	 */
	public boolean verify(JComponent input) {
		final String methodName = "verify";
		TraceHelper.entry(this, methodName);

		boolean rc = true;
		int multi = 1;
		double val = 0;
		FrequencyInputField source = (FrequencyInputField) input;
		TraceHelper.text(this, "verify", source.getName());
		String text = source.getText().toUpperCase();
		// check for char at end of text
		if (text.length() < 2) {
			message = VNAMessages.getString("Input.Frq.5");
			rc = false;
		}
		if (rc) {
			if (text.endsWith("G")) {
				multi = 1000000000;
				text = text.substring(0, text.length() - 1);
			}
			if (text.endsWith("M")) {
				multi = 1000000;
				text = text.substring(0, text.length() - 1);
			}
			if (text.endsWith("K")) {
				multi = 1000;
				text = text.substring(0, text.length() - 1);
			}
			//
			if ("MIN".equals(text)) {
				val = source.getLowerLimit();
				source.setText(VNAFormatFactory.getFrequencyFormat().format(val));
			} else if ("MAX".equals(text)) {
				val = source.getUpperLimit();
				source.setText(VNAFormatFactory.getFrequencyFormat().format(val));
			} else {
				try {
					val = NumberFormat.getNumberInstance().parse(text).doubleValue() * multi;
					source.setText(VNAFormatFactory.getFrequencyFormat().format(val));
				} catch (ParseException e) {
					message = VNAMessages.getString("Input.Frq.6");
					rc = false;
				}
			}
		}
		if ((rc) && (val < source.getLowerLimit())) {
			message = MessageFormat.format(VNAMessages.getString("Input.Frq.7"), VNAFormatFactory.getFrequencyFormat().format(source.getLowerLimit()));
			rc = false;
		}
		if ((rc) && (val > source.getUpperLimit())) {
			message = MessageFormat.format(VNAMessages.getString("Input.Frq.8"), VNAFormatFactory.getFrequencyFormat().format(source.getUpperLimit()));
			rc = false;
		}
		// set the valid flag in the component
		source.setValidData(rc);
		//
		TraceHelper.exitWithRC(this, methodName, "rc=%d", rc);
		return rc;
	}
}
