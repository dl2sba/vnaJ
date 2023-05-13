/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.data.calibrated;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.HashSet;

import org.apache.commons.math3.complex.Complex;
import org.jdom.Element;

import krause.util.ras.logging.ErrorLogHelper;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public class VNACalibratedSample implements Serializable {
	private static HashSet<String> beanNames = new HashSet<String>() {
	};

	private static final long serialVersionUID = -231006451669990001L;
	public static final String XML_NAME_SAMPLE = "sample";
	private static final String XML_NAME_SAMPLE_FREQ = "frequency";
	private static final String XML_NAME_SAMPLE_GRPDEL = "Tgr";
	private static final String XML_NAME_SAMPLE_INDEX = "index";
	private static final String XML_NAME_SAMPLE_MAG = "magnitude";
	private static final String XML_NAME_SAMPLE_R = "r";
	private static final String XML_NAME_SAMPLE_RL = "reflectionloss";
	private static final String XML_NAME_SAMPLE_RP = "reflectionphase";
	private static final String XML_NAME_SAMPLE_RSS1 = "rss1";
	private static final String XML_NAME_SAMPLE_RSS2 = "rss2";
	private static final String XML_NAME_SAMPLE_RSS3 = "rss3";
	private static final String XML_NAME_SAMPLE_SWR = "swr";
	private static final String XML_NAME_SAMPLE_THETA = "theta";
	private static final String XML_NAME_SAMPLE_TL = "transmissionloss";
	private static final String XML_NAME_SAMPLE_TP = "transmissionphase";
	private static final String XML_NAME_SAMPLE_X = "x";
	private static final String XML_NAME_SAMPLE_Z = "z";

	private transient int diagramX = 0;
	private long frequency = 0;
	private double groupDelay = 0;
	private double mag = 0;
	private double R = 0;
	private double reflectionLoss = 0;
	private double reflectionPhase = 0;
	private double RelativeSignalStrength1 = 0;
	private double RelativeSignalStrength2 = 0;
	private double RelativeSignalStrength3 = 0;
	private transient Complex RHO = null;
	private double swr = 0;
	private double theta = 0;
	private double transmissionLoss = 0;
	private double transmissionPhase = 0;
	private double X = 0;
	private double z = 0;
	private transient Complex zComplex50Ohms = null;

	public static VNACalibratedSample fromElement(Element e) {
		VNACalibratedSample rc = new VNACalibratedSample();
		rc.setFrequency(getLongFromElement(e, XML_NAME_SAMPLE_FREQ));
		rc.setMag(getDoubleFromElement(e, XML_NAME_SAMPLE_MAG));
		rc.setReflectionLoss(getDoubleFromElement(e, XML_NAME_SAMPLE_RL));
		rc.setReflectionPhase(getDoubleFromElement(e, XML_NAME_SAMPLE_RP));
		rc.setTransmissionLoss(getDoubleFromElement(e, XML_NAME_SAMPLE_TL));
		rc.setTransmissionPhase(getDoubleFromElement(e, XML_NAME_SAMPLE_TP));
		rc.setR(getDoubleFromElement(e, XML_NAME_SAMPLE_R));
		rc.setRelativeSignalStrength1(getDoubleFromElement(e, XML_NAME_SAMPLE_RSS1));
		rc.setRelativeSignalStrength2(getDoubleFromElement(e, XML_NAME_SAMPLE_RSS2));
		rc.setRelativeSignalStrength3(getDoubleFromElement(e, XML_NAME_SAMPLE_RSS3));
		rc.setSWR(getDoubleFromElement(e, XML_NAME_SAMPLE_SWR));
		rc.setTheta(getDoubleFromElement(e, XML_NAME_SAMPLE_THETA));
		rc.setX(getDoubleFromElement(e, XML_NAME_SAMPLE_X));
		rc.setZ(getDoubleFromElement(e, XML_NAME_SAMPLE_Z));
		return rc;
	}

	private static double getDoubleFromElement(Element e, String name) {
		double rc = 0;
		if (e != null) {
			String t = e.getChildText(name);
			if (t != null && !"".equals(t)) {
				rc = Double.parseDouble(t);
			}
		}
		return rc;
	}

	private static long getLongFromElement(Element e, String name) {
		long rc = 0;
		if (e != null) {
			String t = e.getChildText(name);
			if (t != null && !"".equals(t)) {
				rc = Long.parseLong(t);
			}
		}
		return rc;
	}

	private static void setupBeanInfo() {
		BeanInfo info = null;
		try {
			info = Introspector.getBeanInfo(VNACalibratedSample.class);
			PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; ++i) {
				PropertyDescriptor pd = propertyDescriptors[i];
				if (beanNames.contains(pd.getName())) {
					pd.setValue("transient", Boolean.TRUE);
				}
			}
		} catch (IntrospectionException e) {
			ErrorLogHelper.exception("", "setupBeanInfo", e);
		}
	}

	public VNACalibratedSample() {
		beanNames.add("RHO");
		beanNames.add("ZComplex50Ohms");
		beanNames.add("DiagramX");
		setupBeanInfo();
	}

	/**
	 * Copy all data from pSource to this instance except the frequency value
	 * 
	 * @param pSource
	 */
	public void copy(VNACalibratedSample pSource) {
		setGroupDelay(pSource.getGroupDelay());
		setMag(pSource.getMag());
		setR(pSource.getR());
		setReflectionLoss(pSource.getReflectionLoss());
		setReflectionPhase(pSource.getReflectionPhase());
		setRelativeSignalStrength1(pSource.getRelativeSignalStrength1());
		setRelativeSignalStrength2(pSource.getRelativeSignalStrength2());
		setRelativeSignalStrength3(pSource.getRelativeSignalStrength3());
		setRHO(pSource.getRHO());
		setSWR(pSource.getSWR());
		setTheta(pSource.getTheta());
		setTransmissionLoss(pSource.getTransmissionLoss());
		setTransmissionPhase(pSource.getTransmissionPhase());
		setX(pSource.getX());
		setZ(pSource.getZ());
		setZComplex50Ohms(pSource.getZComplex50Ohms());
	}

	public Element asElement(int index) {
		Element rc = new Element(XML_NAME_SAMPLE);
		rc.addContent(new Element(XML_NAME_SAMPLE_INDEX).setText(Long.toString(index)));
		rc.addContent(new Element(XML_NAME_SAMPLE_FREQ).setText(Long.toString(getFrequency())));
		rc.addContent(new Element(XML_NAME_SAMPLE_MAG).setText(Double.toString(getMag())));
		rc.addContent(new Element(XML_NAME_SAMPLE_R).setText(Double.toString(getR())));
		rc.addContent(new Element(XML_NAME_SAMPLE_RSS1).setText(Double.toString(getRelativeSignalStrength1())));
		rc.addContent(new Element(XML_NAME_SAMPLE_RSS2).setText(Double.toString(getRelativeSignalStrength2())));
		rc.addContent(new Element(XML_NAME_SAMPLE_RSS3).setText(Double.toString(getRelativeSignalStrength3())));
		rc.addContent(new Element(XML_NAME_SAMPLE_RL).setText(Double.toString(getReflectionLoss())));
		rc.addContent(new Element(XML_NAME_SAMPLE_RP).setText(Double.toString(getReflectionPhase())));
		rc.addContent(new Element(XML_NAME_SAMPLE_TL).setText(Double.toString(getTransmissionLoss())));
		rc.addContent(new Element(XML_NAME_SAMPLE_TP).setText(Double.toString(getTransmissionPhase())));
		rc.addContent(new Element(XML_NAME_SAMPLE_THETA).setText(Double.toString(getTheta())));
		rc.addContent(new Element(XML_NAME_SAMPLE_SWR).setText(Double.toString(getSWR())));
		rc.addContent(new Element(XML_NAME_SAMPLE_X).setText(Double.toString(getX())));
		rc.addContent(new Element(XML_NAME_SAMPLE_Z).setText(Double.toString(getZ())));
		rc.addContent(new Element(XML_NAME_SAMPLE_GRPDEL).setText(Double.toString(getGroupDelay())));
		return rc;
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	public double getDataByScaleType(SCALE_TYPE type) {
		if (type == SCALE_TYPE.SCALE_RETURNPHASE)
			return reflectionPhase;
		else if (type == SCALE_TYPE.SCALE_TRANSMISSIONPHASE)
			return transmissionPhase;
		else if (type == SCALE_TYPE.SCALE_TRANSMISSIONLOSS)
			return transmissionLoss;
		else if (type == SCALE_TYPE.SCALE_RETURNLOSS)
			return reflectionLoss;
		else if (type == SCALE_TYPE.SCALE_RSS)
			return RelativeSignalStrength1;
		else if (type == SCALE_TYPE.SCALE_RS)
			return R;
		else if (type == SCALE_TYPE.SCALE_SWR)
			return swr;
		else if (type == SCALE_TYPE.SCALE_THETA)
			return theta;
		else if (type == SCALE_TYPE.SCALE_XS)
			return X;
		else if (type == SCALE_TYPE.SCALE_Z_ABS)
			return z;
		else if (type == SCALE_TYPE.SCALE_GRPDLY)
			return groupDelay;
		else
			return 0;
	}

	/**
	 * @return the diagramX
	 */
	public int getDiagramX() {
		return diagramX;
	}

	public long getFrequency() {
		return frequency;
	}

	public double getGroupDelay() {
		return groupDelay;
	}

	/**
	 * @return the mAG
	 */
	public double getMag() {
		return mag;
	}

	/**
	 * @return the r
	 */
	public double getR() {
		return R;
	}

	public double getReflectionLoss() {
		return reflectionLoss;
	}

	public double getReflectionPhase() {
		return reflectionPhase;
	}

	/**
	 * @return the relativeSignalStrength1
	 */
	public double getRelativeSignalStrength1() {
		return RelativeSignalStrength1;
	}

	/**
	 * @return the relativeSignalStrength2
	 */
	public double getRelativeSignalStrength2() {
		return RelativeSignalStrength2;
	}

	/**
	 * @return the relativeSignalStrength3
	 */
	public double getRelativeSignalStrength3() {
		return RelativeSignalStrength3;
	}

	/**
	 * @return the rHO
	 */
	public Complex getRHO() {
		return RHO;
	}

	/**
	 * @return the sWR
	 */
	public double getSWR() {
		return swr;
	}

	public double getTheta() {
		return theta;
	}

	public double getTransmissionLoss() {
		return transmissionLoss;
	}

	public double getTransmissionPhase() {
		return transmissionPhase;
	}

	/**
	 * @return the x
	 */
	public double getX() {
		return X;
	}

	/**
	 * @return the zMAG
	 */
	public double getZ() {
		return z;
	}

	/**
	 * @return the zComplex50Ohms
	 */
	public Complex getZComplex50Ohms() {
		return zComplex50Ohms;
	}

	/**
	 * @param diagramX
	 *            the diagramX to set
	 */
	public void setDiagramX(int diagramX) {
		this.diagramX = diagramX;
	}

	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}

	public void setGroupDelay(double groupDelay) {
		this.groupDelay = groupDelay;
	}

	/**
	 * @param mAG
	 *            the mAG to set
	 */
	public void setMag(double mAG) {
		mag = mAG;
	}

	/**
	 * @param r
	 *            the r to set
	 */
	public void setR(double r) {
		R = r;
	}

	public void setReflectionLoss(double reflectionLoss) {
		this.reflectionLoss = reflectionLoss;
	}

	public void setReflectionPhase(double phase) {
		this.reflectionPhase = phase;
	}

	/**
	 * @param relativeSignalStrength1
	 *            the relativeSignalStrength1 to set
	 */
	public void setRelativeSignalStrength1(double relativeSignalStrength1) {
		RelativeSignalStrength1 = relativeSignalStrength1;
	}

	/**
	 * @param relativeSignalStrength2
	 *            the relativeSignalStrength2 to set
	 */
	public void setRelativeSignalStrength2(double relativeSignalStrength2) {
		RelativeSignalStrength2 = relativeSignalStrength2;
	}

	/**
	 * @param relativeSignalStrength3
	 *            the relativeSignalStrength3 to set
	 */
	public void setRelativeSignalStrength3(double relativeSignalStrength3) {
		RelativeSignalStrength3 = relativeSignalStrength3;
	}

	/**
	 * @param rHO
	 *            the rHO to set
	 */
	public void setRHO(Complex rHO) {
		RHO = rHO;
	}

	/**
	 * @param sWR
	 *            the sWR to set
	 */
	public void setSWR(double pSWR) {
		swr = pSWR;
	}

	public void setTheta(double pTheta) {
		theta = pTheta;
	}

	public void setTransmissionLoss(double transmissionLoss) {
		this.transmissionLoss = transmissionLoss;
	}

	public void setTransmissionPhase(double transmissionPhase) {
		this.transmissionPhase = transmissionPhase;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(double x) {
		X = x;
	}

	/**
	 * @param z
	 *            the zMAG to set
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * @param zComplex50Ohms
	 *            the zComplex50Ohms to set
	 */
	public void setZComplex50Ohms(Complex zComplex50Ohms) {
		this.zComplex50Ohms = zComplex50Ohms;
	}
}
