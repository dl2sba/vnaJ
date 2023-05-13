package krause.vna.firmware;

import krause.common.exception.ProcessingException;
import krause.vna.device.IVNADriver;

public interface IVNAFirmwareFlasher {

	public void burnBuffer(FirmwareFileParser hfp, IVNADriver driver) throws ProcessingException;

	public String getDeviceType();

	public int getPageSize();

	public int getFlashSize();

	public int getEEpromSize();

	public int getPagePtr();

	public int getRetryCount();

	public void setAutoReset(boolean autoReset);

	public void setMessenger(StringMessenger messenger);

}
