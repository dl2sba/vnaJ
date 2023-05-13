package krause.vna.data.observer;


public interface VNADeviceTypeObserver extends VNAObserver {
	public void changeDeviceType(String oldType, String newType);

}
