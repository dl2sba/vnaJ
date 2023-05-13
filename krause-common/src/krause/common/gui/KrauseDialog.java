/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.common.gui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;

public abstract class KrauseDialog extends JDialog implements WindowListener {

	private Window owner = null;
	private String configurationPrefix = null;
	private TypedProperties properties = null;

	/**
	 * 
	 * @param aFrame
	 * @param b
	 */
	public KrauseDialog(Window aWnd, boolean modal) {
		super(aWnd, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
		addWindowListener(this);
		this.owner = aWnd;
	}

	/**
	 * 
	 * @param aFrame
	 * @param b
	 */
	public KrauseDialog(boolean modal) {
		super((Frame) null, modal);
		addWindowListener(this);
		this.owner = null;
	}

	public KrauseDialog(Dialog aDlg, boolean modal) {
		super(aDlg, modal);
		addWindowListener(this);
		this.owner = aDlg;
	}

	@Override
	public void dispose() {
		TraceHelper.entry(this, "dispose");
		if (getConfigurationPrefix() != null && getProperties() != null) {
			TraceHelper.text(this, "dispose", "Saving properties ...");
			getProperties().storeWindowPosition(getConfigurationPrefix(), this);
			getProperties().storeWindowSize(getConfigurationPrefix(), this);
		}
		super.dispose();
		TraceHelper.exit(this, "dispose");
	}

	protected void showCenteredOnScreen() {
		pack();
		Dimension dimRoot = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimRoot.width / 2) - (getSize().width / 2));
		int y = (int) ((dimRoot.height / 2) - (getSize().height / 2));
		// center on component
		setLocation(x, y);
		setVisible(true);
	}

	/**
	 * 
	 * @param root
	 * @param width
	 * @param height
	 */
	protected void showNormal(int width, int height) {
		pack();
		setSize(width, height);
		setVisible(true);
	}

	/**
	 * 
	 * @param root
	 * @param width
	 * @param height
	 */
	protected void showCentered(int width, int height) {
		pack();
		setSize(width, height);
		centerOnComponent(getOwner());
		setVisible(true);
	}

	/**
	 * 
	 * @param root
	 */
	protected void showCentered(Component root) {
		pack();
		centerOnComponent(root);
		setVisible(true);
	}

	protected void centerOnComponent(Component root) {
		Dimension dimRoot = root.getSize();
		Point locRoot = root.getLocation();
		int x = (int) (locRoot.getX() + (dimRoot.width / 2) - (getSize().width / 2));
		int y = (int) (locRoot.getY() + (dimRoot.height / 2) - (getSize().height / 2));

		if (x < 0) {
			x = 0;
		}

		if (y < 0) {
			y = 0;
		}
		// center on component
		setLocation(x, y);
	}

	protected void addEscapeKey() {
		// add escape key to window
		Action actionListener = new AbstractAction() {
			public void actionPerformed(ActionEvent actionEvent) {
				doDialogCancel();
			}
		};
		KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
		InputMap inputMap = this.rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(stroke, "ESCAPE");
		this.rootPane.getActionMap().put("ESCAPE", actionListener);
	}

	protected abstract void doDialogCancel();

	protected abstract void doDialogInit();

	/**
	 * @return the owner
	 */
	@Override
	public Window getOwner() {
		return this.owner;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(Window owner) {
		this.owner = owner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	public void windowActivated(WindowEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	public void windowClosed(WindowEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	public void windowClosing(WindowEvent e) {
		doDialogCancel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent )
	 */
	public void windowDeactivated(WindowEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent )
	 */
	public void windowDeiconified(WindowEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	public void windowIconified(WindowEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	public void windowOpened(WindowEvent e) {
	}

	/**
	 * 
	 */
	public void showCenteredOnOwner() {
		centerOnComponent(this.owner);
		pack();
		setVisible(true);
	}

	/**
	 * @param configurationPrefix
	 *            the configurationPrefix to set
	 */
	public void setConfigurationPrefix(String configurationPrefix) {
		this.configurationPrefix = configurationPrefix;
	}

	/**
	 * @return the configurationPrefix
	 */
	public String getConfigurationPrefix() {
		return this.configurationPrefix;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(TypedProperties properties) {
		this.properties = properties;
	}

	/**
	 * @return the properties
	 */
	public TypedProperties getProperties() {
		return this.properties;
	}

	public void doDialogShow() {
		TraceHelper.entry(this, "doDialogShow");
		if (getConfigurationPrefix() != null && getProperties() != null) {
			Dimension sz = this.getPreferredSize();
			getProperties().restoreWindowPosition(getConfigurationPrefix(), this, new Point(100, 100));
			pack();
			getProperties().restoreWindowSize(getConfigurationPrefix(), this, sz);
			setVisible(true);
		} else {
			pack();
			setVisible(true);
		}
		TraceHelper.exit(this, "doDialogShow");
	}
}
