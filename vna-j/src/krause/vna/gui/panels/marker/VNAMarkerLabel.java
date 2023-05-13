/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 *
 *	This file: VNAMarkerLabel.java
 *  Part of:   vna-j
 */

package krause.vna.gui.panels.marker;

import java.awt.Font;

import javax.swing.JLabel;

import krause.vna.config.VNASystemConfig;
import krause.vna.config.VNASystemConfig.OS_PLATFORM;

public class VNAMarkerLabel extends JLabel {
	private final Font textFont = new Font("Arial", java.awt.Font.PLAIN, 12);

	/**
	 * @param string
	 */
	public VNAMarkerLabel(String string) {
		super(string);
		if (VNASystemConfig.getPlatform() == OS_PLATFORM.MAC) {
			setFont(textFont);
		}
	}
}
