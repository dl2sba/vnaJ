package krause.common.gui;

/* A 1.4 class used by TextComponentDemo.java. */

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class DocumentSizeFilter extends DocumentFilter {
	int maxCharacters;

	public DocumentSizeFilter(int maxChars) {
		this.maxCharacters = maxChars;
	}

	@Override
	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
		// This rejects the entire insertion if it would make
		// the contents too long. Another option would be
		// to truncate the inserted string so the contents
		// would be exactly maxCharacters in length.
		if ((fb.getDocument().getLength() + str.length()) <= this.maxCharacters)
			super.insertString(fb, offs, str, a);
		else
			Toolkit.getDefaultToolkit().beep();
	}

	@Override
	public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
		// This rejects the entire replacement if it would make
		// the contents too long. Another option would be
		// to truncate the replacement string so the contents
		// would be exactly maxCharacters in length.
		if ((fb.getDocument().getLength() + str.length() - length) <= this.maxCharacters)
			super.replace(fb, offs, length, str, a);
		else
			Toolkit.getDefaultToolkit().beep();
	}

}