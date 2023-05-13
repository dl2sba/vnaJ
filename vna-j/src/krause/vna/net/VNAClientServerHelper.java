package krause.vna.net;

import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.net.data.VNAClientRequest;
import krause.vna.net.data.VNAClientRequest.CLIENT_CMDS;
import krause.vna.net.data.VNAServerResponse;
import krause.vna.net.data.VNAServerResponse.SERVER_RESPONSES;

public class VNAClientServerHelper {
	public static boolean responseTypeMatchesRequest(VNAServerResponse resp, VNAClientRequest req) {

		SERVER_RESPONSES respType = resp.getResponseType();
		CLIENT_CMDS reqType = req.getCommand();

		if (reqType == CLIENT_CMDS.SCAN) {
			return (respType == SERVER_RESPONSES.SCAN_DATA) || (respType == SERVER_RESPONSES.ERROR);
		} else if (reqType == CLIENT_CMDS.START_GEN) {
			return (respType == SERVER_RESPONSES.GEN_STARTED) || (respType == SERVER_RESPONSES.ERROR);
		} else if (reqType == CLIENT_CMDS.STOP_GEN) {
			return (respType == SERVER_RESPONSES.GEN_STOPPED) || (respType == SERVER_RESPONSES.ERROR);
		} else if (reqType == CLIENT_CMDS.PING) {
			return (respType == SERVER_RESPONSES.PINGED);
		} else {
			return false;
		}
	}

	public static boolean requestTypeMatchesDriver(VNAClientRequest req, IVNADriver driver) {
		VNADeviceInfoBlock dibReq = req.getDeviceInfoBlock();
		VNADeviceInfoBlock dibDrv = driver.getDeviceInfoBlock();
		String reqType = dibReq.getType();
		String drvType = dibDrv.getType();
		if ("21".equals(reqType) && "1".equals(drvType)) {
			return true;
		} else if ("22".equals(reqType) && "2".equals(drvType)) {
			return true;
		} else if ("20".equals(reqType) && "0".equals(drvType)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean responseDriverMatchesRequestDriver(VNAServerResponse resp, VNAClientRequest req) {
		if ("0".equals(resp.getDeviceInfoBlock().getType())) {
			return ("20".equals(req.getDeviceInfoBlock().getType()));
		} else if ("1".equals(resp.getDeviceInfoBlock().getType())) {
			return ("21".equals(req.getDeviceInfoBlock().getType()));
		} else {
			return false;
		}
	}
}
