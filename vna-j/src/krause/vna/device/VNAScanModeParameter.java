package krause.vna.device;

import krause.vna.data.VNAScanMode;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public class VNAScanModeParameter {
	private boolean requiresLoad = false;
	private boolean requiresLoop = false;
	private boolean requiresOpen = false;
	private boolean requiresShort = false;
	private boolean requiresRSS1 = false;
	private boolean requiresRSS2 = false;
	private boolean requiresRSS3 = false;
	private SCALE_TYPE scaleLeft;
	private SCALE_TYPE scaleRight;
	private VNAScanMode mode;

	/**
	 * 
	 * @param pMode
	 *            mode for which these requirements apply
	 * @param pOpen
	 *            open calibration required
	 * @param pShort
	 *            short calibration required
	 * @param pLoad
	 *            load calibration required
	 * @param pLoop
	 *            loop calibration required
	 */
	public VNAScanModeParameter(VNAScanMode pMode, boolean pOpen, boolean pShort, boolean pLoad, boolean pLoop, SCALE_TYPE pScaleLeft, SCALE_TYPE pScaleRight) {
		requiresLoad = pLoad;
		requiresOpen = pOpen;
		requiresShort = pShort;
		requiresLoop = pLoop;
		scaleLeft = pScaleLeft;
		scaleRight = pScaleRight;
		mode = pMode;
	}

	public SCALE_TYPE getScaleLeft() {
		return scaleLeft;
	}

	public SCALE_TYPE getScaleRight() {
		return scaleRight;
	}

	public boolean isRequiresLoad() {
		return requiresLoad;
	}

	public boolean isRequiresLoop() {
		return requiresLoop;
	}

	public boolean isRequiresOpen() {
		return requiresOpen;
	}

	public boolean isRequiresRSS1() {
		return requiresRSS1;
	}

	public boolean isRequiresRSS2() {
		return requiresRSS2;
	}

	public boolean isRequiresRSS3() {
		return requiresRSS3;
	}

	public boolean isRequiresShort() {
		return requiresShort;
	}

	public void setRequiresLoad(boolean requiresLoad) {
		this.requiresLoad = requiresLoad;
	}

	public void setRequiresLoop(boolean requiresLoop) {
		this.requiresLoop = requiresLoop;
	}

	public void setRequiresOpen(boolean requiresOpen) {
		this.requiresOpen = requiresOpen;
	}

	public void setRequiresRSS1(boolean requiresRSS1) {
		this.requiresRSS1 = requiresRSS1;
	}

	public void setRequiresRSS2(boolean requiresRSS2) {
		this.requiresRSS2 = requiresRSS2;
	}

	public void setRequiresRSS3(boolean requiresRSS3) {
		this.requiresRSS3 = requiresRSS3;
	}

	public void setRequiresShort(boolean requiresShort) {
		this.requiresShort = requiresShort;
	}

	public void setScaleLeft(SCALE_TYPE scaleLeft) {
		this.scaleLeft = scaleLeft;
	}

	public void setScaleRight(SCALE_TYPE scaleRight) {
		this.scaleRight = scaleRight;
	}

	public void setMode(VNAScanMode mode) {
		this.mode = mode;
	}

	public VNAScanMode getMode() {
		return mode;
	}

	public String toString() {
		return mode.toString();
	}
}
