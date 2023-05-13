/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: ComplexInputFieldValueChangeListener.java
 *  Part of:   vna-j
 */

package krause.vna.gui.input;

import org.apache.commons.math3.complex.Complex;

/**
 * @author Dietmar
 * 
 */
public interface ComplexInputFieldValueChangeListener {
	public void valueChanged(Complex oldValue, Complex newValue);

}
