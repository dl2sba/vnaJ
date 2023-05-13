package krause.net.server.data;


/**
 * Used to control a simple server.
 * Controlling is based on a callback mechanism from the server to the controlling application
 *  
 * @author DL2SBA
 *
 */
public interface ServerController {

	/**
	 * Should the server stop?
	 * 
	 * @return true, server should stop
	 */
	public boolean serverShouldStop();

	/**
	 * Publishes a server status report to the controlling application
	 * 
	 * @param status
	 */
	public void reportServerStatus(ServerStatusBlock status);
}
