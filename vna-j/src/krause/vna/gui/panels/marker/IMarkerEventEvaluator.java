/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: IMarkerEventEvaluator.java
 *  Part of:   vna-j
 */

package krause.vna.gui.panels.marker;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * @author Dietmar
 * 
 */
public interface IMarkerEventEvaluator {

	/**
	 * 
	 * @param e
	 * @return
	 */
	public boolean isMyMouseEvent(MouseEvent e);

	/**
	 * 
	 * @param e
	 * @return
	 */
	public boolean isMyMouseWheelEvent(MouseWheelEvent e);
}
