package krause.vna.firmware;

/**
 * 
 * @author dietmar krause, 2014
 * 
 */
public abstract class VNABaseFirmwareFlasher implements IVNAFirmwareFlasher {
	protected StringMessenger messenger;
	protected String deviceType;
	protected int pageSize; // in bytes
	protected int bootSize; // in words
	protected int flashSize; // in bytes
	protected int eEpromSize; // in bytes
	protected int pagePtr;
	protected int retryCount;

	private boolean autoReset;

	public VNABaseFirmwareFlasher() {

	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getBootSize() {
		return bootSize;
	}

	public void setBootSize(int bootSize) {
		this.bootSize = bootSize;
	}

	public int getFlashSize() {
		return flashSize;
	}

	public void setFlashSize(int flashSize) {
		this.flashSize = flashSize;
	}

	public int getEEpromSize() {
		return eEpromSize;
	}

	public void setEEpromSize(int eEpromSize) {
		this.eEpromSize = eEpromSize;
	}

	public int getPagePtr() {
		return pagePtr;
	}

	public void setPagePtr(int pagePtr) {
		this.pagePtr = pagePtr;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public boolean isAutoReset() {
		return autoReset;
	}

	public void setAutoReset(boolean autoReset) {
		this.autoReset = autoReset;
	}

	/**
	 * 
	 * @return
	 */
	public StringMessenger getMessenger() {
		return messenger;
	}

	/**
	 * 
	 * @param messenger
	 */
	public void setMessenger(StringMessenger messenger) {
		this.messenger = messenger;
	}

}
