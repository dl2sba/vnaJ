package krause.vna.gui.panels.marker;

public class VNAMarkerSearchMode {
	public enum MARKERFIELDTYPE {
		RL, TL, PHASE, SWR, Z, R, X
	};

	private MARKERFIELDTYPE field = MARKERFIELDTYPE.RL;
	private boolean minimum;
	private boolean maximum;

	public boolean isMinimum() {
		return minimum;
	}

	public VNAMarkerSearchMode(MARKERFIELDTYPE field) {
		super();
		this.field = field;
		this.minimum = false;
		this.maximum = false;
	}

	public void setMinimum(boolean minimum) {
		this.minimum = minimum;
	}

	public MARKERFIELDTYPE getField() {
		return field;
	}

	public void setField(MARKERFIELDTYPE field) {
		this.field = field;
	}

	public boolean isMaximum() {
		return maximum;
	}

	public void setMaximum(boolean maximum) {
		this.maximum = maximum;
	}

	public boolean toggle() {
		if (maximum) {
			maximum = false;
			minimum = false;
			return false;
		} else if (minimum) {
			maximum = true;
			minimum = false;
			return true;
		} else {
			maximum = false;
			minimum = true;
			return true;
		}
	}

	public void clearSearchMode() {
		maximum = false;
		minimum = false;
	}
}
