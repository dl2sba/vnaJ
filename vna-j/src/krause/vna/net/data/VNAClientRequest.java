package krause.vna.net.data;

import java.io.Serializable;

import krause.vna.device.VNADeviceInfoBlock;

public class VNAClientRequest implements Serializable {

	public enum CLIENT_CMDS {
		PING, SCAN, START_GEN, STOP_GEN, READ_FIRMWARE
	};

	private long startFrequency;
	private long stopFrequency;
	private int numberOfSamples;
	private boolean transmissionMode;
	private VNADeviceInfoBlock dib;
	private int frequencyI;
	private int frequencyQ;
	private int attenuationI;
	private int attenuationQ;
	private int phase;
	private int mainAttenuation;

	private CLIENT_CMDS command;

	public VNAClientRequest(CLIENT_CMDS cmd, VNADeviceInfoBlock deviceInfoBlock) {
		command = cmd;
		dib = deviceInfoBlock;
	}

	public long getStartFrequency() {
		return startFrequency;
	}

	public void setStartFrequency(long startFrequency) {
		this.startFrequency = startFrequency;
	}

	public long getStopFrequency() {
		return stopFrequency;
	}

	public void setStopFrequency(long stopFrequency) {
		this.stopFrequency = stopFrequency;
	}

	public int getNumberOfSamples() {
		return numberOfSamples;
	}

	public void setNumberOfSamples(int numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
	}

	public boolean isTransmissionMode() {
		return transmissionMode;
	}

	public void setTransmissionMode(boolean transmissionMode) {
		this.transmissionMode = transmissionMode;
	}

	public CLIENT_CMDS getCommand() {
		return command;
	}

	public void setCommand(CLIENT_CMDS command) {
		this.command = command;
	}

	public VNADeviceInfoBlock getDeviceInfoBlock() {
		return dib;
	}

	public int getFrequencyI() {
		return frequencyI;
	}

	public void setFrequencyI(int frequencyI) {
		this.frequencyI = frequencyI;
	}

	public int getFrequencyQ() {
		return frequencyQ;
	}

	public void setFrequencyQ(int frequencyQ) {
		this.frequencyQ = frequencyQ;
	}

	public int getAttenuationI() {
		return attenuationI;
	}

	public void setAttenuationI(int attenuationI) {
		this.attenuationI = attenuationI;
	}

	public int getAttenuationQ() {
		return attenuationQ;
	}

	public void setAttenuationQ(int attenuationQ) {
		this.attenuationQ = attenuationQ;
	}

	public int getPhase() {
		return phase;
	}

	public void setPhase(int phase) {
		this.phase = phase;
	}

	public int getMainAttenuation() {
		return mainAttenuation;
	}

	public void setMainAttenuation(int mainAttenuation) {
		this.mainAttenuation = mainAttenuation;
	}
}
