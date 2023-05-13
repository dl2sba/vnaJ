package krause.vna.gui.generator.digit;

import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class VNADigitLabel extends JLabel {

	private void setupLookAndFeel() {
		setOpaque(true);
		setHorizontalAlignment(SwingConstants.CENTER);
		setFont(new Font("Tahoma", Font.PLAIN, 51));
	}

	public VNADigitLabel() {
		super();
		setupLookAndFeel();
	}

	public VNADigitLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
		setupLookAndFeel();
	}

	public VNADigitLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
		setupLookAndFeel();
	}

	public VNADigitLabel(Icon image) {
		super(image);
		setupLookAndFeel();
	}

	public VNADigitLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		setupLookAndFeel();
	}

	public VNADigitLabel(String text) {
		super(text);
		setupLookAndFeel();
	}

	public void setValue(long value) {
		setText("" + value);
	}

	public long getValue() {
		return Long.parseLong(getText());
	}
}
