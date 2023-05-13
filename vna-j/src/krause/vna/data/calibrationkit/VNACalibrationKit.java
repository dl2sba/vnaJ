package krause.vna.data.calibrationkit;

import java.util.UUID;

public class VNACalibrationKit {

	private String id;
	private String name;
	private double openOffset;
	private double openLoss;
	private double shortOffset;
	private double shortLoss;
	private boolean female;

	private double openCapCoeffC0;
	private double openCapCoeffC1;
	private double openCapCoeffC2;
	private double openCapCoeffC3;

	private double shortInductance;
	private double thruLength;

	public VNACalibrationKit(final String newName) {
		initFields(newName);
	}

	public VNACalibrationKit() {
		initFields("DEFAULT");
	}

	private void initFields(final String name) {
		this.name = name;
		this.setId(UUID.randomUUID().toString());
		this.female = false;
		this.openCapCoeffC0 = 0;
		this.openCapCoeffC1 = 0;
		this.openCapCoeffC2 = 0;
		this.openCapCoeffC3 = 0.01;
		this.openLoss = 0;
		this.openOffset = 0;
		this.shortInductance = 0;
		this.shortOffset = 0;
		this.thruLength = 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getOpenOffset() {
		return openOffset;
	}

	public void setOpenOffset(double openOffset) {
		this.openOffset = openOffset;
	}

	public double getOpenLoss() {
		return openLoss;
	}

	public void setOpenLoss(double openLoss) {
		this.openLoss = openLoss;
	}

	public double getShortOffset() {
		return shortOffset;
	}

	public void setShortOffset(double shortOffset) {
		this.shortOffset = shortOffset;
	}

	public double getShortLoss() {
		return shortLoss;
	}

	public void setShortLoss(double shortLoss) {
		this.shortLoss = shortLoss;
	}

	public boolean isFemale() {
		return female;
	}

	public void setFemale(boolean female) {
		this.female = female;
	}

	public double getOpenCapCoeffC0() {
		return openCapCoeffC0;
	}

	public void setOpenCapCoeffC0(double openCapCoeffC0) {
		this.openCapCoeffC0 = openCapCoeffC0;
	}

	public double getOpenCapCoeffC1() {
		return openCapCoeffC1;
	}

	public void setOpenCapCoeffC1(double openCapCoeffC1) {
		this.openCapCoeffC1 = openCapCoeffC1;
	}

	public double getOpenCapCoeffC2() {
		return openCapCoeffC2;
	}

	public void setOpenCapCoeffC2(double openCapCoeffC2) {
		this.openCapCoeffC2 = openCapCoeffC2;
	}

	public double getOpenCapCoeffC3() {
		return openCapCoeffC3;
	}

	public void setOpenCapCoeffC3(double openCapCoeffC3) {
		this.openCapCoeffC3 = openCapCoeffC3;
	}

	public double getShortInductance() {
		return shortInductance;
	}

	public void setShortInductance(double shortInductance) {
		this.shortInductance = shortInductance;
	}

	public double getThruLength() {
		return thruLength;
	}

	public void setThruLength(double thruLength) {
		this.thruLength = thruLength;
	}

	public String toString() {
		return this.name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
