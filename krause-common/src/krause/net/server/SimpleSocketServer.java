package krause.net.server;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Properties;

import krause.common.exception.ProcessingException;
import krause.net.server.data.ServerController;
import krause.net.server.data.ServerStatusBlock;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;

public class SimpleSocketServer extends Thread {
	public final static String CONFIG_ACCEPT_PORT = "SimpleSocketServer.AcceptPort";
	public final static String CONFIG_ACCEPT_TIMEOUT = "SimpleSocketServer.AcceptTimeout";
	public final static String CONFIG_HANDLER_CLASSNAME = "SimpleSocketServer.SocketRequestHandlerClassname";

	private int acceptPort;
	private int acceptTimeout;
	private InetAddress bindAddr;
	private ServerController serverController;
	private ServerStatusBlock status = new ServerStatusBlock();
	private Properties parameters = null;
	//
	private String socketRequestHandlerClassname = null;
	private Class<SimpleServerSocketBaseRequestHandler> socketRequestHandlerClass = null;
	private Constructor<SimpleServerSocketBaseRequestHandler> socketRequestHandlerConstructor = null;

	/**
	 * Listen on TCP port pPort.
	 * 
	 * Bind the Listener to address pBindAddr.
	 * 
	 * This server is controlled via pServerControl.
	 * 
	 * The parameters pProps are parsed to the used socket handler.
	 * 
	 * @param pPort
	 * @param pTimeout
	 * @param pBindAddr
	 * @param pServerControl
	 * @param pProps
	 * @throws ProcessingException
	 */
	public SimpleSocketServer(InetAddress pBindAddr, ServerController pServerControl, Properties pProps) throws ProcessingException {
		TraceHelper.entry(this, "SimpleSocketServer");
		parameters = pProps;
		bindAddr = pBindAddr;
		serverController = pServerControl;

		acceptPort = Integer.parseInt(parameters.getProperty(CONFIG_ACCEPT_PORT));
		acceptTimeout = Integer.parseInt(parameters.getProperty(CONFIG_ACCEPT_TIMEOUT));
		//
		setupClassFactory();

		TraceHelper.exit(this, "SimpleSocketServer");
	}

	@SuppressWarnings("unchecked")
	private void setupClassFactory() throws ProcessingException {
		final String methodName = "setupClassFactory";
		TraceHelper.entry(this, methodName);
		this.socketRequestHandlerClassname = this.parameters.getProperty(CONFIG_HANDLER_CLASSNAME);
		try {
			this.socketRequestHandlerClass = (Class<SimpleServerSocketBaseRequestHandler>) Class.forName(this.socketRequestHandlerClassname);
			this.socketRequestHandlerConstructor = this.socketRequestHandlerClass.getConstructor(Socket.class, Properties.class, ServerStatusBlock.class);
		} catch (ClassNotFoundException | NoSuchMethodException e) {
			ErrorLogHelper.exception(this, methodName, e);
			throw new ProcessingException(e);
		}
		TraceHelper.exit(this, methodName);
	}

	public void run() {
		TraceHelper.entry(this, "run");

		ServerSocket clientConnect;
		this.status.setStartTime(new Date());
		try {
			clientConnect = new ServerSocket(this.acceptPort, 0, this.bindAddr);

			// nach dieser zeit bricht das accept ab
			clientConnect.setSoTimeout(this.acceptTimeout);

			// solange der server laufen soll
			while (!this.serverController.serverShouldStop()) {
				// handle control info
				this.status.setLastLifesign(new Date());
				this.serverController.reportServerStatus(this.status);
				Socket clientReq = null;
				try {
					// wait for a connection
					clientReq = clientConnect.accept();

					// create handler object for it
					SimpleServerSocketBaseRequestHandler socketHandler = createNewSocketRequestHandler(clientReq);
					socketHandler.handle();
				} catch (SocketTimeoutException e) {
				} catch (Exception e) {
					ErrorLogHelper.exception(this, "run", e);
				} finally {
					if (clientReq != null) {
						try {
							clientReq.close();
						} catch (IOException e) {
							ErrorLogHelper.exception(this, "run", e);
						}
					}

				}
			}
			clientConnect.close();
		} catch (IOException e1) {
		}
		TraceHelper.exit(this, "run");
	}

	private SimpleServerSocketBaseRequestHandler createNewSocketRequestHandler(Socket clientReq) throws ProcessingException {
		SimpleServerSocketBaseRequestHandler rc = null;
		TraceHelper.entry(this, "createNewSocketRequestHandler");
		try {
			rc = (SimpleServerSocketBaseRequestHandler) socketRequestHandlerConstructor.newInstance(clientReq, parameters, status);
		} catch (IllegalArgumentException e) {
			ErrorLogHelper.exception(this, "createNewSocketRequestHandler", e);
			throw new ProcessingException(e);
		} catch (InstantiationException e) {
			ErrorLogHelper.exception(this, "createNewSocketRequestHandler", e);
			throw new ProcessingException(e);
		} catch (IllegalAccessException e) {
			ErrorLogHelper.exception(this, "createNewSocketRequestHandler", e);
			throw new ProcessingException(e);
		} catch (InvocationTargetException e) {
			ErrorLogHelper.exception(this, "createNewSocketRequestHandler", e);
			throw new ProcessingException(e);
		}
		TraceHelper.exit(this, "createNewSocketRequestHandler");
		return rc;
	}
}
