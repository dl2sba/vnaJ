package krause.vna.device;

public interface IVNAFlashableDevice {

	/**
	 * Get the baudrate to use during firmware flashing
	 * 
	 * @return the baudrate
	 * 
	 */
	public int getFirmwareLoaderBaudRate();

	/**
	 * Return the class name of the firmware loader which should be used to
	 * download the firmware to the target device
	 * 
	 * @return the class name of the firmware loader
	 */
	public String getFirmwareLoaderClassName();

	/**
	 * Specifies, whether the device has a reset button, which can be used to
	 * start the firmware download
	 * 
	 * @return true=has a reset button
	 */
	public abstract boolean hasResetButton();

	/**
	 * Specifies, whether the device support auto reset thru the device
	 * interface
	 * 
	 * @return true=support false=no support
	 */
	public abstract boolean supportsAutoReset();

}
