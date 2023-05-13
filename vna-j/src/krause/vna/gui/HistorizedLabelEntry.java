/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 *
 *	This file: VNAHistorizedStatusLabelEntry.java
 *  Part of:   vna-j
 */

package krause.vna.gui;

/**
 * @author Dietmar
 * 
 */
public class HistorizedLabelEntry {
	private String text;
	private long timestamp;

	/**
	 * @param pText
	 * @param pTime
	 */
	public HistorizedLabelEntry(String pText, long pTime) {
		text = pText;
		timestamp = pTime;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
