package krause.vna.net.data;

import java.io.Serializable;

import krause.vna.data.VNASampleBlock;
import krause.vna.device.VNADeviceInfoBlock;

public class VNAServerResponse implements Serializable {
	public enum SERVER_RESPONSES {
		SCAN_DATA, GEN_STARTED, GEN_STOPPED, ERROR, PINGED, FIRMWARE_VERSION
	};

	private VNAClientRequest request;
	private String infoText;
	private VNASampleBlock data;
	private SERVER_RESPONSES responseType;
	private VNADeviceInfoBlock dib;

	public VNAServerResponse(SERVER_RESPONSES pType) {
		responseType=pType;
	}

	public VNAClientRequest getRequest() {
		return request;
	}

	public void setRequest(VNAClientRequest request) {
		this.request = request;
	}

	public VNASampleBlock getData() {
		return data;
	}

	public void setData(VNASampleBlock data) {
		this.data = data;
	}

	public void setInfoText(String infoText) {
		this.infoText = infoText;
	}

	public String getInfoText() {
		return infoText;
	}

	/**
	 * @param responseType
	 *            the responseType to set
	 */
	public void setResponseType(SERVER_RESPONSES responseType) {
		this.responseType = responseType;
	}

	/**
	 * @return the responseType
	 */
	public SERVER_RESPONSES getResponseType() {
		return responseType;
	}

	/**
	 * @param dib
	 *            the dib to set
	 */
	public void setDeviceInfoBlock(VNADeviceInfoBlock dib) {
		this.dib = dib;
	}

	/**
	 * @return the dib
	 */
	public VNADeviceInfoBlock getDeviceInfoBlock() {
		return dib;
	}

}
