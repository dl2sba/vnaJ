package krause.vna.device;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

public class VNADriverMenuItem extends JMenuItem {

	private String driverClassname;
	private String mathHelperClassName;
	private String type;

	public VNADriverMenuItem() {
		super();
	}

	public VNADriverMenuItem(Action a) {
		super(a);
	}

	public VNADriverMenuItem(Icon icon) {
		super(icon);
	}

	public VNADriverMenuItem(String text, Icon icon) {
		super(text, icon);
	}

	public VNADriverMenuItem(String text, int mnemonic) {
		super(text, mnemonic);
	}

	public VNADriverMenuItem(String text) {
		super(text);
	}

	/**
	 * @return the driverClassname
	 */
	public String getDriverClassname() {
		return driverClassname;
	}

	/**
	 * @param driverClassname
	 *            the driverClassname to set
	 */
	public void setDriverClassname(String driverClassname) {
		this.driverClassname = driverClassname;
	}

	/**
	 * @return the mathHelperClassName
	 */
	public String getMathHelperClassName() {
		return mathHelperClassName;
	}

	/**
	 * @param mathHelperClassName
	 *            the mathHelperClassName to set
	 */
	public void setMathHelperClassName(String mathHelperClassName) {
		this.mathHelperClassName = mathHelperClassName;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}
