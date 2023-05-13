package krause.vna.gui.generator.digit;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.border.EtchedBorder;

import krause.vna.resources.VNAMessages;

public class VNADigitTextField extends VNADigitLabel implements MouseListener {
	private long factor;

	public VNADigitTextField(int pFactor, long pValue, int pFontSize) {
		this(pFactor, pValue);
		setFont(getFont().deriveFont((float) pFontSize));
		setToolTipText(VNAMessages.getString("VNADigitTextField.tooltip"));
	}

	/**
	 * @wbp.parser.constructor
	 */
	public VNADigitTextField(long pFactor, long pValue) {
		super();

		setForeground(Color.YELLOW);
		setBackground(Color.BLACK);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setToolTipText(VNAMessages.getString("VNADigitTextField.tooltip"));

		setFactor(pFactor);
		setValue(pValue);

		// we want inverted digits, when mouse is over
		addMouseListener(this);
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setFactor(long value) {
		this.factor = value;
	}

	/**
	 * @return the value
	 */
	public long getFactor() {
		return factor;
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		setForeground(Color.BLACK);
		setBackground(Color.YELLOW);
	}

	public void mouseExited(MouseEvent e) {
		setForeground(Color.YELLOW);
		setBackground(Color.BLACK);
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public long getValue() {
		return Long.parseLong(getText());
	}

	public void setValue(long value) {
		setText("" + value);
	}
}
