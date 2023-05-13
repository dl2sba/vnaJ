package krause.vna.data.calibrated;

import java.io.File;
import java.util.List;

import org.jdom.Element;

import krause.vna.data.VNAMinMaxPair;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public class VNACalibratedSampleBlock {
	public static final String XML_NAME_COMMENT = "comment";
	public static final String XML_NAME_FREQ = "frequency-range";
	public static final String XML_NAME_MAX = "max";
	public static final String XML_NAME_MIN = "min";
	public static final String XML_NAME_MINMAX = "min-max-values";
	public static final String XML_NAME_ROOT = "vna-j-scandata";
	public static final String XML_NAME_SAMPLES = "samples";

	public static VNACalibratedSampleBlock fromElement(Element root) {
		VNACalibratedSampleBlock rc = null;
		VNACalibratedSample[] samples = readDataElements(root);
		if (samples != null) {
			rc = new VNACalibratedSampleBlock(samples.length);
			rc.setComment(root.getChildText(XML_NAME_COMMENT));
			rc.setCalibratedSamples(samples);
			readMinMaxValueElements(rc, root);
		}
		return rc;
	}

	@SuppressWarnings("unchecked")
	private static VNACalibratedSample[] readDataElements(final Element root) {
		VNACalibratedSample[] rc = null;
		final Element e = root.getChild(XML_NAME_SAMPLES);
		if (e != null) {
			List<Element> samples = e.getChildren(VNACalibratedSample.XML_NAME_SAMPLE);
			if (samples != null) {
				int len = samples.size();
				if (len > 0) {
					rc = new VNACalibratedSample[len];
					for (int i = 0; i < len; ++i) {
						rc[i] = VNACalibratedSample.fromElement(samples.get(i));
					}
				}
			}
		}
		return rc;
	}

	/**
	 * 
	 * @param sb
	 * @param root
	 */
	private static void readMinMaxValueElements(VNACalibratedSampleBlock sb, Element root) {
		Element mms = root.getChild(XML_NAME_MINMAX);
		sb.setMmRP(VNAMinMaxPair.fromElement(mms, SCALE_TYPE.SCALE_RETURNPHASE.toString()));
		if (sb.getMmRP().getMinIndex() == -1) {
			sb.setMmRP(VNAMinMaxPair.fromElement(mms, "SCALE_PHASE"));
		}
		sb.setMmTP(VNAMinMaxPair.fromElement(mms, SCALE_TYPE.SCALE_TRANSMISSIONPHASE.toString()));
		if (sb.getMmTP().getMinIndex() == -1) {
			sb.setMmTP(VNAMinMaxPair.fromElement(mms, "SCALE_PHASE"));
		}
		sb.setMmRL(VNAMinMaxPair.fromElement(mms, SCALE_TYPE.SCALE_RETURNLOSS.toString()));
		sb.setMmTL(VNAMinMaxPair.fromElement(mms, SCALE_TYPE.SCALE_TRANSMISSIONLOSS.toString()));
		sb.setMmRS(VNAMinMaxPair.fromElement(mms, SCALE_TYPE.SCALE_RS.toString()));
		sb.setMmRSS(VNAMinMaxPair.fromElement(mms, SCALE_TYPE.SCALE_RSS.toString()));
		sb.setMmSWR(VNAMinMaxPair.fromElement(mms, SCALE_TYPE.SCALE_SWR.toString()));
		sb.setMmTheta(VNAMinMaxPair.fromElement(mms, SCALE_TYPE.SCALE_THETA.toString()));
		sb.setMmXS(VNAMinMaxPair.fromElement(mms, SCALE_TYPE.SCALE_XS.toString()));
		sb.setMmZABS(VNAMinMaxPair.fromElement(mms, SCALE_TYPE.SCALE_Z_ABS.toString()));
		sb.setMmGRPDLY(VNAMinMaxPair.fromElement(mms, SCALE_TYPE.SCALE_GRPDLY.toString()));
	}

	private String blockComment;
	private VNACalibratedSample[] calibratedSamples;
	private File file = null;

	private VNAMinMaxPair mmGroupDelay = new VNAMinMaxPair(SCALE_TYPE.SCALE_GRPDLY);
	private VNAMinMaxPair mmRL = new VNAMinMaxPair(SCALE_TYPE.SCALE_RETURNLOSS);
	private VNAMinMaxPair mmRLPHASE = new VNAMinMaxPair(SCALE_TYPE.SCALE_RETURNPHASE);
	private VNAMinMaxPair mmRS = new VNAMinMaxPair(SCALE_TYPE.SCALE_RS);
	private VNAMinMaxPair mmRSS = new VNAMinMaxPair(SCALE_TYPE.SCALE_RSS);
	private VNAMinMaxPair mmSWR = new VNAMinMaxPair(SCALE_TYPE.SCALE_SWR);
	private VNAMinMaxPair mmTheta = new VNAMinMaxPair(SCALE_TYPE.SCALE_THETA);
	private VNAMinMaxPair mmTL = new VNAMinMaxPair(SCALE_TYPE.SCALE_TRANSMISSIONLOSS);
	private VNAMinMaxPair mmTLPHASE = new VNAMinMaxPair(SCALE_TYPE.SCALE_TRANSMISSIONPHASE);
	private VNAMinMaxPair mmXS = new VNAMinMaxPair(SCALE_TYPE.SCALE_XS);
	private VNAMinMaxPair mmZABS = new VNAMinMaxPair(SCALE_TYPE.SCALE_Z_ABS);

	public VNACalibratedSampleBlock(int listLength) {
		calibratedSamples = new VNACalibratedSample[listLength];
	}

	// put
	public Element asElement() {
		Element rc = new Element(XML_NAME_ROOT);

		// min max values
		Element minmax = new Element(XML_NAME_MINMAX);
		minmax.addContent(getMmGRPDLY().asElement());
		minmax.addContent(getMmRL().asElement());
		minmax.addContent(getMmRP().asElement());
		minmax.addContent(getMmTL().asElement());
		minmax.addContent(getMmTP().asElement());
		minmax.addContent(getMmRS().asElement());
		minmax.addContent(getMmRSS().asElement());
		minmax.addContent(getMmSWR().asElement());
		minmax.addContent(getMmTheta().asElement());
		minmax.addContent(getMmXS().asElement());
		minmax.addContent(getMmZABS().asElement());
		rc.addContent(minmax);

		// frequency range
		Element fr = new Element(XML_NAME_FREQ);
		VNACalibratedSample[] samples = getCalibratedSamples();
		if (samples != null && samples.length > 0) {
			fr.addContent(new Element(XML_NAME_MIN).setText("" + samples[0].getFrequency()));
			fr.addContent(new Element(XML_NAME_MAX).setText("" + samples[samples.length - 1].getFrequency()));
		}
		rc.addContent(fr);

		// comment
		if (getComment() != null) {
			rc.addContent(new Element(XML_NAME_COMMENT).setText(getComment()));
		}

		// and now the data
		Element data = new Element(XML_NAME_SAMPLES);
		VNACalibratedSample[] ss = getCalibratedSamples();
		for (int i = 0; i < ss.length; ++i) {
			VNACalibratedSample s = ss[i];
			data.addContent(s.asElement(i));
		}
		rc.addContent(data);

		return rc;
	}

	public void consumeCalibratedSample(VNACalibratedSample sample, int index) {
		// determine the min and max values
		mmRL.consume(sample.getReflectionLoss(), index);
		mmTL.consume(sample.getTransmissionLoss(), index);
		mmRLPHASE.consume(sample.getReflectionPhase(), index);
		mmTLPHASE.consume(sample.getTransmissionPhase(), index);
		mmXS.consume(sample.getX(), index);
		mmRS.consume(sample.getR(), index);
		mmZABS.consume(sample.getZ(), index);
		mmSWR.consume(sample.getSWR(), index);
		mmRSS.consume(sample.getRelativeSignalStrength1(), index);
		mmTheta.consume(sample.getTheta(), index);
		mmGroupDelay.consume(sample.getGroupDelay(), index);

		// and store the data
		calibratedSamples[index] = sample;
	}

	public VNACalibratedSample[] getCalibratedSamples() {
		return calibratedSamples;
	}

	public String getComment() {
		return blockComment;
	}

	public File getFile() {
		return file;
	}

	/**
	 * @param key
	 * @return
	 */
	public VNAMinMaxPair getMinMaxPair(SCALE_TYPE key) {
		if (key == SCALE_TYPE.SCALE_RETURNPHASE)
			return mmRLPHASE;
		else if (key == SCALE_TYPE.SCALE_TRANSMISSIONPHASE)
			return mmTLPHASE;
		else if (key == SCALE_TYPE.SCALE_RETURNLOSS)
			return mmRL;
		else if (key == SCALE_TYPE.SCALE_TRANSMISSIONLOSS)
			return mmTL;
		else if (key == SCALE_TYPE.SCALE_RS)
			return mmRS;
		else if (key == SCALE_TYPE.SCALE_RSS)
			return mmRSS;
		else if (key == SCALE_TYPE.SCALE_SWR)
			return mmSWR;
		else if (key == SCALE_TYPE.SCALE_THETA)
			return mmTheta;
		else if (key == SCALE_TYPE.SCALE_XS)
			return mmXS;
		else if (key == SCALE_TYPE.SCALE_Z_ABS)
			return mmZABS;
		else if (key == SCALE_TYPE.SCALE_GRPDLY)
			return mmGroupDelay;
		else
			return null;
	}

	public VNAMinMaxPair getMmGRPDLY() {
		return mmGroupDelay;
	}

	public VNAMinMaxPair getMmRL() {
		return mmRL;
	}

	public VNAMinMaxPair getMmRP() {
		return mmRLPHASE;
	}

	public VNAMinMaxPair getMmRS() {
		return mmRS;
	}

	public VNAMinMaxPair getMmRSS() {
		return mmRSS;
	}

	public VNAMinMaxPair getMmSWR() {
		return mmSWR;
	}

	public VNAMinMaxPair getMmTheta() {
		return mmTheta;
	}

	public VNAMinMaxPair getMmTL() {
		return mmTL;
	}

	public VNAMinMaxPair getMmTP() {
		return mmTLPHASE;
	}

	public VNAMinMaxPair getMmXS() {
		return mmXS;
	}

	public VNAMinMaxPair getMmZABS() {
		return mmZABS;
	}

	public void setCalibratedSamples(final VNACalibratedSample[] calibratedSamples) {
		this.calibratedSamples = calibratedSamples;
	}

	public void setComment(final String blockComment) {
		this.blockComment = blockComment;
	}

	public void setFile(final File file) {
		this.file = file;
	}

	public void setMmGRPDLY(final VNAMinMaxPair mmGRPDLY) {
		this.mmGroupDelay = mmGRPDLY;
	}

	public void setMmRL(final VNAMinMaxPair mmRL) {
		this.mmRL = mmRL;
	}

	public void setMmRP(final VNAMinMaxPair mmPHASE) {
		this.mmRLPHASE = mmPHASE;
	}

	public void setMmRS(final VNAMinMaxPair mmRS) {
		this.mmRS = mmRS;
	}

	public void setMmRSS(final VNAMinMaxPair mmRSS) {
		this.mmRSS = mmRSS;
	}

	public void setMmSWR(final VNAMinMaxPair mmSWR) {
		this.mmSWR = mmSWR;
	}

	public void setMmTheta(final VNAMinMaxPair mmTheta) {
		this.mmTheta = mmTheta;
	}

	public void setMmTL(final VNAMinMaxPair mmTL) {
		this.mmTL = mmTL;
	}

	public void setMmTP(final VNAMinMaxPair mmTLPHASE) {
		this.mmTLPHASE = mmTLPHASE;
	}

	public void setMmXS(final VNAMinMaxPair mmXS) {
		this.mmXS = mmXS;
	}

	public void setMmZABS(final VNAMinMaxPair mmZABS) {
		this.mmZABS = mmZABS;
	}
}
