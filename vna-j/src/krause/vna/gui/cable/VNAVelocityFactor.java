package krause.vna.gui.cable;

/**
 * 
 * @author dietmar krause
 * 
 */
public class VNAVelocityFactor {
	private String name;
	private double z0;
	private String f1;
	private String attenF1;
	private String f2;
	private String attenF2;
	private double vf;

	public VNAVelocityFactor(String name, double z0, double vf, String attenF1, String f1, String attenF2, String f2) {
		super();
		this.name = name;
		this.z0 = z0;
		this.f1 = f1;
		this.attenF1 = attenF1;
		this.f2 = f2;
		this.attenF2 = attenF2;
		this.vf = vf;
	}

	/**
	 * Create an empty instance
	 */
	public VNAVelocityFactor() {
	}

	/**
	 * Create an instance with 50 Ohms
	 * 
	 * @param name
	 * @param vf
	 */
	public VNAVelocityFactor(String name, double vf) {
		this.name = name;
		this.z0 = 50.0;
		this.vf = vf;
	}

	/**
	 * 
	 * @param name
	 * @param z0
	 * @param vf
	 */
	public VNAVelocityFactor(String name, double z0, double vf) {
		this(name, vf);
		this.z0 = z0;
	}

	@Override
	public String toString() {
		return "VNAVelocityFactor [name=" + name + ", z0=" + z0 + ", f1=" + f1 + ", attenF1=" + attenF1 + ", f2=" + f2 + ", attenF2=" + attenF2 + ", vf=" + vf + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getZ0() {
		return z0;
	}

	public void setZ0(double z0) {
		this.z0 = z0;
	}

	public String getF1() {
		return f1;
	}

	public void setF1(String f1) {
		this.f1 = f1;
	}

	public String getAttenF1() {
		return attenF1;
	}

	public void setAttenF1(String attenF1) {
		this.attenF1 = attenF1;
	}

	public String getF2() {
		return f2;
	}

	public void setF2(String f2) {
		this.f2 = f2;
	}

	public String getAttenF2() {
		return attenF2;
	}

	public void setAttenF2(String attenF2) {
		this.attenF2 = attenF2;
	}

	public double getVf() {
		return vf;
	}

	public void setVf(double vf) {
		this.vf = vf;
	}

}
