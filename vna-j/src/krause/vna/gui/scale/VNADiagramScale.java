/**
 * Copyright (C) 2020 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.scale;

import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * A generic Scale used for the frequency and the measured value scales.
 * 
 * @author Dietmar Krause
 * 
 */
public abstract class VNADiagramScale extends JPanel {
	@Override
	public void paint(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}
