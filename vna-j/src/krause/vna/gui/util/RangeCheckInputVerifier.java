package krause.vna.gui.util;

import java.awt.Toolkit;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import krause.vna.resources.VNAMessages;

public class RangeCheckInputVerifier extends InputVerifier {
	private String message = "";
	private NumberFormat formatToUse;
	private boolean emptyValid = false;

	public RangeCheckInputVerifier(NumberFormat pFormatToUse, boolean emptyValid) {
		this.formatToUse = pFormatToUse;
		this.emptyValid = emptyValid;
	}

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
		boolean rc = true;
		int multi = 1;
		double val = 0;
		RangeCheckedTextField source = (RangeCheckedTextField) input;
		String text = source.getText().toUpperCase().trim();

		if ((text.length() == 0) && this.emptyValid) {
			rc = true;
		} else {
			try {
				val = NumberFormat.getNumberInstance().parse(text).doubleValue() * multi;
				source.setText(formatToUse.format(val));
			} catch (ParseException e) {
				message = VNAMessages.getString("Input.Frq.6");
				rc = false;
			}
			if (rc) {
				if (val < source.getLowerLimit()) {
					message = MessageFormat.format(VNAMessages.getString("Input.Frq.7"), formatToUse.format(source.getLowerLimit()));

					rc = false;
				}
			}
			if (rc) {
				if (val > source.getUpperLimit()) {
					message = MessageFormat.format(VNAMessages.getString("Input.Frq.8"), formatToUse.format(source.getUpperLimit()));
					rc = false;
				}
			}
		}

		// set the valid flag in the component
		source.setValidData(rc);

		return rc;
	}
}
