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
package krause.vna.gui.util;

import java.text.NumberFormat;

import krause.vna.gui.format.VNAFormatFactory;

/**
 * @author Dietmar Krause
 * 
 */
public class VNAFrequencyPair {
	private long startFrequency = 0;
	private long stopFrequency = 0;

	public VNAFrequencyPair() {
	}

	public VNAFrequencyPair(long start, long stop) {
		setStartFrequency(start);
		setStopFrequency(stop);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VNAFrequencyPair) {
			VNAFrequencyPair p = (VNAFrequencyPair) obj;
			return ((p.getStartFrequency() == getStartFrequency()) && (p.getStopFrequency() == getStopFrequency()));
		} else {
			return super.equals(obj);
		}
	}

	public boolean isWithinPair(long frq) {
		return ((frq >= startFrequency) && (frq <= stopFrequency));
	}

	public long getStartFrequency() {
		return startFrequency;
	}

	public long getStopFrequency() {
		return stopFrequency;
	}

	public void setStartFrequency(long startFrequency) {
		this.startFrequency = startFrequency;
	}

	public void setStopFrequency(long stopFrequency) {
		this.stopFrequency = stopFrequency;
	}

	public String toString() {
		NumberFormat nf = VNAFormatFactory.getFrequencyFormat();
		return nf.format(getStartFrequency()) + "-" + nf.format(getStopFrequency());
	}
}
