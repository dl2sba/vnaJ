/**
 * Copyright (C) 2007 Dietmar Krause, DL2SBA
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package krause.vna.gui.panels.marker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JTextField;
import javax.swing.SwingConstants;

import krause.vna.config.VNASystemConfig;
import krause.vna.config.VNASystemConfig.OS_PLATFORM;

/**
 * @author Dietmar Krause
 * 
 */
public class VNAMarkerTextField extends JTextField {
	private final Font annotateFont = new Font("Arial", java.awt.Font.PLAIN, 8);
	private final Font textFont = new Font("Arial", java.awt.Font.PLAIN, 12);

	private VNAMarkerSearchMode markerSearchMode;

	public VNAMarkerTextField(int columns) {
		super(columns);
		setEditable(false);
		setHorizontalAlignment(SwingConstants.RIGHT);
	}

	public VNAMarkerTextField(int columns, boolean editable) {
		super(columns);
		setEditable(editable);
		setHorizontalAlignment(SwingConstants.TRAILING);
		if (VNASystemConfig.getPlatform() == OS_PLATFORM.MAC) {
			setFont(textFont);
		}
	}

	@Override
	public void paint(Graphics g) {
		// TraceHelper.entry(this, "paint");
		super.paint(g);
		g.setColor(Color.BLACK);
		g.setFont(annotateFont);
		if (markerSearchMode != null) {
			if (markerSearchMode.isMinimum()) {
				g.drawString("*", 1, 22);
			} else if (markerSearchMode.isMaximum()) {
				g.drawString("*", 1, 10);
			}
		}
		// TraceHelper.exit(this, "paint");
	}

	public VNAMarkerSearchMode getMarkerSearchMode() {
		return markerSearchMode;
	}

	public void setMarkerSearchMode(VNAMarkerSearchMode markerSearchMode) {
		this.markerSearchMode = markerSearchMode;
	}

	/**
	 * 
	 * @return false no more search mode active
	 */
	public boolean toggleSearchMode() {
		boolean rc = false;
		if (markerSearchMode != null) {
			rc = markerSearchMode.toggle();
			repaint();
		}
		return rc;
	}

	public void clearSearchMode() {
		if (markerSearchMode != null) {
			markerSearchMode.clearSearchMode();
			repaint();
		}
	}
}
