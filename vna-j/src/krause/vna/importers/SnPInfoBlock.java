/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: SnPInfoBlock.java
 *  Part of:   vna-j
 */

package krause.vna.importers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.complex.Complex;

/**
 * @author Dietmar
 * 
 */
public class SnPInfoBlock {

	public enum PARAMETER {
		S, Y, Z, H, G
	};

	public enum FORMAT {
		DB, MA, RI
	};

	private String comment;
	private String filename;
	private Complex reference;
	private long frequencyMultiplier;
	private FORMAT format;
	private PARAMETER parameter;
	private List<SnPRecord> records = new ArrayList<SnPRecord>();

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Complex getReference() {
		return reference;
	}

	public void setReference(Complex reference) {
		this.reference = reference;
	}

	public long getFrequencyMultiplier() {
		return frequencyMultiplier;
	}

	public void setFrequencyMultiplier(long frequencyMultiplier) {
		this.frequencyMultiplier = frequencyMultiplier;
	}

	public FORMAT getFormat() {
		return format;
	}

	public void setFormat(FORMAT format) {
		this.format = format;
	}

	public PARAMETER getParameter() {
		return parameter;
	}

	public void setParameter(PARAMETER parameter) {
		this.parameter = parameter;
	}

	public List<SnPRecord> getRecords() {
		return records;
	}

	public void setRecords(List<SnPRecord> records) {
		this.records = records;
	}

	@Override
	public String toString() {
		return "SnPInfoBlock [comment=" + comment + ", filename=" + filename + ", format=" + format + ", frequencyMultiplier=" + frequencyMultiplier + ", parameter=" + parameter + ", records=" + records + ", reference=" + reference + "]";
	}
}
