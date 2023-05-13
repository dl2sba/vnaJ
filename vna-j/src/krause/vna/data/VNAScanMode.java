package krause.vna.data;

import java.io.Serializable;

import krause.common.TypedProperties;
import krause.common.exception.ProcessingException;
import krause.vna.resources.VNAMessages;

public class VNAScanMode implements Serializable {
	public static final int MODENUM_UNKNOWN = -1;
	public static final int MODENUM_TRANSMISSION = 1;
	public static final int MODENUM_REFLECTION = 2;
	public static final int MODENUM_RSS1 = 3;
	public static final int MODENUM_RSS2 = 4;
	public static final int MODENUM_RSS3 = 5;
	public static final int MODENUM_COMBI = 10;
	public static final int MODENUM_TEST = 99;

	private static final long serialVersionUID = 7585259522736849574L;

	public static final VNAScanMode MODE_TRANSMISSION = new VNAScanMode(MODENUM_TRANSMISSION);
	public static final VNAScanMode MODE_REFLECTION = new VNAScanMode(MODENUM_REFLECTION);
	public static final VNAScanMode MODE_RSS1 = new VNAScanMode(MODENUM_RSS1);
	public static final VNAScanMode MODE_RSS2 = new VNAScanMode(MODENUM_RSS2);
	public static final VNAScanMode MODE_RSS3 = new VNAScanMode(MODENUM_RSS3);
	public static final VNAScanMode MODE_TEST = new VNAScanMode(MODENUM_TEST);
	public static final VNAScanMode MODE_COMBI = new VNAScanMode(MODENUM_COMBI);

	public static final String TEXT_REFLECTION = VNAMessages.getString("VNAScanMode.Reflection");
	public static final String TEXT_TRANSMISSION = VNAMessages.getString("VNAScanMode.Transmission");
	public static final String TEXT_RSS1 = VNAMessages.getString("VNAScanMode.RSS1");
	public static final String TEXT_RSS2 = VNAMessages.getString("VNAScanMode.RSS2");
	public static final String TEXT_RSS3 = VNAMessages.getString("VNAScanMode.RSS3");
	public static final String TEXT_COMBI = VNAMessages.getString("VNAScanMode.COMBI");
	public static final String TEXT_TEST = VNAMessages.getString("VNAScanMode.TEST");

	private int mode = MODENUM_TRANSMISSION;

	public VNAScanMode() {
		mode = MODENUM_UNKNOWN;
	}

	/**
	 * @param pMode
	 *            true==transmission, false==reflection
	 */
	public VNAScanMode(int pMode) {
		mode = pMode;
	}

	public void setRssMode1() {
		mode = MODENUM_RSS1;
	}

	public void setTransmissionMode() {
		mode = MODENUM_TRANSMISSION;
	}

	public void setReflectionMode() {
		mode = MODENUM_REFLECTION;
	}

	public boolean isTransmissionMode() {
		return mode == MODENUM_TRANSMISSION;
	}

	public boolean isReflectionMode() {
		return mode == MODENUM_REFLECTION;
	}

	public boolean isRss1Mode() {
		return mode == MODENUM_RSS1;
	}

	public boolean isRss2Mode() {
		return mode == MODENUM_RSS2;
	}

	public boolean isRss3Mode() {
		return mode == MODENUM_RSS3;
	}

	public boolean isTestMode() {
		return mode == MODENUM_TEST;
	}

	public boolean isCombiMode() {
		return mode == MODENUM_COMBI;
	}

	@Override
	public int hashCode() {
		return this.mode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg0) {
		boolean rc = false;
		if (arg0 instanceof VNAScanMode) {
			VNAScanMode o = (VNAScanMode) arg0;
			rc = (o.getMode() == getMode());
		}
		return rc;
	}

	@Override
	public String toString() {
		if (isReflectionMode())
			return TEXT_REFLECTION;
		else if (isTransmissionMode())
			return TEXT_TRANSMISSION;
		else if (isRss1Mode())
			return TEXT_RSS1;
		else if (isRss2Mode())
			return TEXT_RSS2;
		else if (isRss3Mode())
			return TEXT_RSS3;
		else if (isCombiMode())
			return TEXT_COMBI;
		else if (isTestMode())
			return TEXT_TEST;
		else
			return "???";
	}

	public int getMode() {
		return mode;
	}

	public String key() {
		return "" + mode;
	}

	public Object shortText() {
		if (isReflectionMode())
			return "REFL";
		else if (isTransmissionMode())
			return "TRAN";
		else if (isRss1Mode())
			return "RSS1";
		else if (isRss2Mode())
			return "RSS2";
		else if (isRss3Mode())
			return "RSS3";
		else if (isCombiMode())
			return "COMBI";
		else if (isTestMode())
			return "TEST";
		else
			return "XXX";
	}

	public void saveToProperties(TypedProperties props) {
		props.putInteger(getClass().getCanonicalName() + ".scanMode", mode);
	}

	public void restoreFromProperties(TypedProperties props) {
		mode = props.getInteger(getClass().getCanonicalName() + ".scanMode", mode);
	}

	public static VNAScanMode restoreFromString(String p) throws ProcessingException {
		if ("REFL".equals(p)) {
			return MODE_REFLECTION;
		} else if ("TRAN".equals(p)) {
			return MODE_TRANSMISSION;
		} else {
			throw new ProcessingException("Illegal mode [" + p + "]");
		}
	}
}
