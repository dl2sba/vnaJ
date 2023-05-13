/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: Marker0EventEvaluator.java
 *  Part of:   vna-j
 */

package krause.vna.gui.panels.marker;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * @author Dietmar
 * 
 */
public class Marker0EventEvaluator implements IMarkerEventEvaluator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * krause.vna.gui.panels.marker.IMarkerEventEvaluator#isMyMouseEvent(java
	 * .awt.event.MouseEvent)
	 */
	public boolean isMyMouseEvent(MouseEvent e) {
		return ((e.getButton() == MouseEvent.BUTTON1) && ((e.getModifiersEx() & (InputEvent.SHIFT_DOWN_MASK + InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK)) == 0));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * krause.vna.gui.panels.marker.IMarkerEventEvaluator#isMyMouseWheelEvent
	 * (java.awt.event.MouseWheelEvent)
	 */
	public boolean isMyMouseWheelEvent(MouseWheelEvent e) {
		return ((e.getModifiersEx() & (InputEvent.SHIFT_DOWN_MASK + InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK)) == 0);
	}
}
