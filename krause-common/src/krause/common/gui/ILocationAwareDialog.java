package krause.common.gui;

/**
 * Such dialog can save and restore their position on the screen
 * 
 */
public interface ILocationAwareDialog {

	public void restoreWindowPosition();
	public void restoreWindowSize();

	public void storeWindowPosition();
	public void storeWindowSize();
	
	public void showInPlace();
}
