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
package krause.vna.gui.scale;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.text.NumberFormat;

import javax.swing.JPanel;
import javax.swing.UIManager;

import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.format.VNAFormatFactory;

/**
 * @author Dietmar Krause
 * 
 */
public class VNAFrequencyScale extends VNADiagramScale {
	private transient VNADataPool datapool = VNADataPool.getSingleton();

	private final Color fontColor = UIManager.getColor("Panel.foreground");
	private final Font myFont = new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10);
	private static final long[][] SCALER = {

			{
					5000000000l,
					1000000000
			}, // 5000 - 10000 MHz 1000 MHz
			{
					2500000000l,
					500000000
			}, // 2500 - 5000 MHz 500 MHz
			{
					1000000000,
					200000000
			}, // 1000-2000MHz 200MHz
			{
					500000000,
					100000000
			}, // 500 - 1000 MHz 100 MHz
			{
					250000000,
					50000000
			}, // 250 - 500 MHz 50 MHz

			{
					100000000,
					20000000
			}, // 100 - 200 MHz 20 MHz
			{
					50000000,
					10000000
			}, // 50 - 100 MHz 10 MHz
			{
					25000000,
					5000000
			}, // 25 - 50 MHz 5 MHz

			{
					10000000,
					2500000
			}, // 10 - 25 MHz 2500 kHz
			{
					5000000,
					1000000
			}, // 5 - 10 MHz 1000 kHz
			{
					2500000,
					500000
			}, // 2500 - 5000 kHz 500 kHz

			{
					1000000,
					250000
			}, // 1 - 2500 kHz 250 kHz
			{
					500000,
					100000
			}, // 500 - 1000 kHz 100 kHz
			{
					250000,
					50000
			}, // 250 - 500 kHz 50 kHz

			{
					100000,
					25000
			}, // 100 - 250 kHz 25 kHz
			{
					50000,
					10000
			}, // 50 - 100 kHz 10 kHZ
			{
					25000,
					5000
			}, // 25 - 50 kHz 5 kHZ

			{
					10000,
					2500
			}, // 10 - 25 kHz 2 kHZ
			{
					5000,
					1000
			}, // 5 - 10 kHz 1 kHZ
			{
					2500,
					500
			}, // 2,5 - 5 kHz 500 HZ
			{
					1000,
					250
			}, // 1 - 2,5 kHz 200 HZ
	};

	private JPanel leftPanelObject;

	private JPanel rightPanelObject;

	/**
	 * 
	 */
	public VNAFrequencyScale(JPanel leftObject, JPanel rightObject) {
		super();
		leftPanelObject = leftObject;
		rightPanelObject = rightObject;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int realWidth = getParent().getWidth();
		int xOffset = 0;
		if (leftPanelObject != null) {
			realWidth -= leftPanelObject.getWidth();
			xOffset = leftPanelObject.getWidth();
		}
		if (rightPanelObject != null) {
			realWidth -= rightPanelObject.getWidth();
		}
		//
		VNACalibratedSampleBlock lastBlock = datapool.getCalibratedData();
		if (lastBlock != null) {
			VNACalibratedSample[] lastVnaData = lastBlock.getCalibratedSamples();

			// any data present ?
			if (lastVnaData.length > 0) {
				// yes
				long min = lastVnaData[0].getFrequency();
				long max = lastVnaData[lastVnaData.length - 1].getFrequency();
				//
				// get difference
				long diff = max - min;
				if (diff != 0) {
					if (diff < 0) {
						diff = -diff;
					}
					//
					long ticker = 0;
					int scalerIdx = 0;
					while (diff < SCALER[scalerIdx][0]) {
						++scalerIdx;
					}
					ticker = SCALER[scalerIdx][1];
					int ticks = (int) Math.round(diff * 1.0 / ticker);
					long lowFrq = (min / ticker) * ticker;
					if (lowFrq < min) {
						lowFrq += ticker;
					}

					int divisor = 1000;
					String unit = "kHz";
					if (scalerIdx < 1) {
						divisor = 1000000;
						unit = "MHz";
					}
					//
					g.setColor(this.fontColor);
					g.setFont(this.myFont);
					g.setClip(0, 0, getParent().getWidth(), getParent().getHeight());
					g.drawString(unit, getWidth() - 30, 10);
					g.setClip(xOffset, 0, realWidth, getHeight());
					//
					float part = diff / (realWidth * 1.0f);
					NumberFormat ff = VNAFormatFactory.getFrequencyFormat();
					for (scalerIdx = 0; scalerIdx < ticks; ++scalerIdx) {
						int x = xOffset + (int) ((lowFrq - min) / part);
						g.drawLine(x, 0, x, x + getParent().getHeight());
						g.drawString(ff.format(lowFrq / divisor), x + 3, 10);
						lowFrq += ticker;
					}
				}
			}
		}
	}
}
