package krause.vna.gui.util;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import krause.util.ResourceLoader;
import krause.util.ras.logging.ErrorLogHelper;
import krause.vna.gui.VNAMenuAndToolbarHandler;
import krause.vna.resources.VNAMessages;

public abstract class SwingUtil {

	/**
	 * 
	 * @param tb
	 * @param enabled
	 */
	public static void enableToolbar(JToolBar tb, boolean enabled) {
		Component comps[] = tb.getComponents();
		for (int i = 0; i < comps.length; ++i) {
			comps[i].setEnabled(enabled);
		}
	}

	/**
	 * 
	 * @param id
	 * @param listener
	 * @param tooltipLabel
	 * @return
	 */
	public static JMenuItem createJMenuItem(String id, ActionListener listener, JLabel tooltipLabel) {
		return createJMenuItem(VNAMessages.getBundle(), id, listener, tooltipLabel);
	}

	/**
	 * 
	 * @param bundle
	 * @param id
	 * @param listener
	 * @param tooltipLabel
	 * @return
	 */
	public static JMenuItem createJMenuItem(ResourceBundle bundle, String id, ActionListener listener, JLabel tooltipLabel) {
		JMenuItem rc = new JMenuItem(bundle.getString(id));
		rc.setActionCommand(bundle.getString(id + ".Command"));
		rc.setMnemonic(bundle.getString(id + ".Key").charAt(0));
		rc.setToolTipText(bundle.getString(id + ".Tooltip"));
		if (listener != null) {
			rc.addActionListener(listener);
		}
		new JMenuItemTooltipHelper(rc, tooltipLabel);
		return rc;
	}

	/**
	 * 
	 * @param id
	 * @param listener
	 * @param tooltipLabel
	 * @return
	 */
	public static JRadioButtonMenuItem createJRadioButtonMenuItem(String id, ActionListener listener, JLabel tooltipLabel) {
		JRadioButtonMenuItem rc = new JRadioButtonMenuItem(VNAMessages.getString(id));
		rc.setActionCommand(VNAMessages.getString(id + ".Command"));
		rc.setMnemonic(VNAMessages.getString(id + ".Key").charAt(0));
		rc.setToolTipText(VNAMessages.getString(id + ".Tooltip"));
		if (listener != null) {
			rc.addActionListener(listener);
		}
		new JMenuItemTooltipHelper(rc, tooltipLabel);
		return rc;
	}

	/**
	 * 
	 * @param id
	 * @param tooltipLabel
	 * @return
	 */
	public static JMenu createJMenu(String id, JLabel tooltipLabel) {
		JMenu rc = new JMenu(VNAMessages.getString(id));

		rc.setMnemonic(VNAMessages.getString(id + ".Key").charAt(0));
		rc.setToolTipText(VNAMessages.getString(id + ".Tooltip"));

		new JMenuTooltipHelper(rc, tooltipLabel);
		return rc;
	}

	/**
	 * 
	 * @param id
	 * @param listener
	 * @return
	 */
	public static JButton createJButton(String id, ActionListener listener) {
		JButton rc = new JButton(VNAMessages.getString(id));

		rc.setActionCommand(VNAMessages.getString(id + ".Command"));
		rc.setMnemonic(VNAMessages.getString(id + ".Key").charAt(0));
		rc.setToolTipText(VNAMessages.getString(id + ".Tooltip"));
		if (listener != null) {
			rc.addActionListener(listener);
		}
		return rc;
	}

	/**
	 * 
	 * @param id
	 * @param listener
	 * @return
	 */
	public static JRadioButton createJRadioButton(String id, ActionListener listener) {
		JRadioButton rc = new JRadioButton(VNAMessages.getString(id));
		rc.setActionCommand(VNAMessages.getString(id + ".Command"));
		rc.setMnemonic(VNAMessages.getString(id + ".Key").charAt(0));
		rc.setToolTipText(VNAMessages.getString(id + ".Tooltip"));
		if (listener != null) {
			rc.addActionListener(listener);
		}
		return rc;
	}

	public static JCheckBox createJCheckBox(String id, ActionListener listener) {
		JCheckBox rc = new JCheckBox(VNAMessages.getString(id));
		rc.setActionCommand(VNAMessages.getString(id + ".Command"));
		rc.setMnemonic(VNAMessages.getString(id + ".Key").charAt(0));
		rc.setToolTipText(VNAMessages.getString(id + ".Tooltip"));
		if (listener != null) {
			rc.addActionListener(listener);
		}
		return rc;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static JButton createToolbarButton(String name, ActionListener listener) {
		String command = VNAMessages.getString(name + ".Command");
		String tooltip = VNAMessages.getString(name + ".Tooltip");
		String image = VNAMessages.getString(name + ".Image");
		String altText = VNAMessages.getString(name);
		// Create and initialize the button.
		JButton rc = new JButton();
		rc.setActionCommand(command);
		rc.setToolTipText(tooltip);
		if (listener != null) {
			rc.addActionListener(listener);
		}

		try {
			byte[] iconBytes = ResourceLoader.getResourceAsByteArray(image);
			rc.setIcon(new ImageIcon(iconBytes, altText));
		} catch (IOException ex) {
			ErrorLogHelper.exception(SwingUtil.class, "createToolbarButton", ex);
			rc.setText(altText);
		} catch (NullPointerException ex) {
			ErrorLogHelper.exception(SwingUtil.class, "createToolbarButton", ex);
			rc.setText(altText);
		}
		return rc;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static JLabel createImageLabel(String name) {
		String tooltip = VNAMessages.getString(name + ".Tooltip");
		String image = VNAMessages.getString(name + ".Image");
		String altText = VNAMessages.getString(name);
		// Create and initialize the label.
		JLabel label = new JLabel();
		label.setToolTipText(tooltip);

		try {
			byte[] iconBytes = ResourceLoader.getResourceAsByteArray(image);
			label.setIcon(new ImageIcon(iconBytes, altText));
		} catch (IOException ex) {
			ErrorLogHelper.exception(SwingUtil.class, "createImageLabel", ex);
			label.setText(altText);
		} catch (NullPointerException ex) {
			ErrorLogHelper.exception(SwingUtil.class, "createImageLabel", ex);
			label.setText(altText);
		}
		return label;
	}

	/**
	 * 
	 * @param id
	 * @param listener
	 * @return
	 */
	public static JCheckBox createJCheckbox(String id, ActionListener listener) {
		JCheckBox rc = new JCheckBox(VNAMessages.getString(id));
		rc.setActionCommand(VNAMessages.getString(id + ".Command"));
		rc.setMnemonic(VNAMessages.getString(id + ".Key").charAt(0));
		rc.setToolTipText(VNAMessages.getString(id + ".Tooltip"));
		if (listener != null) {
			rc.addActionListener(listener);
		}
		return rc;
	}

	public static JToggleButton createToggleButton(String name, ActionListener listener) {
		String tooltip = VNAMessages.getString(name + ".Tooltip");
		String image = VNAMessages.getString(name + ".Image");
		String altText = VNAMessages.getString(name);
		// Create and initialize the label.
		JToggleButton label = new JToggleButton();
		label.setToolTipText(tooltip);

		try {
			byte[] iconBytes = ResourceLoader.getResourceAsByteArray(image);
			label.setIcon(new ImageIcon(iconBytes, altText));
		} catch (IOException ex) {
			ErrorLogHelper.exception(SwingUtil.class, "createImageLabel", ex);
			label.setText(altText);
		}
		if (listener != null) {
			label.addActionListener(listener);
		}
		return label;
	}

	/**
	 * 
	 * @param name
	 * @param listener
	 * @return
	 */
	public static JButton createImageButton(String name, ActionListener listener) {
		String tooltip = VNAMessages.getString(name + ".Tooltip");
		String image = VNAMessages.getString(name + ".Image");
		String altText = VNAMessages.getString(name);
		// Create and initialize the label.
		JButton newButton = new JButton();
		newButton.setToolTipText(tooltip);
		newButton.setBorderPainted(false);

		try {
			byte[] iconBytes = ResourceLoader.getResourceAsByteArray(image);
			newButton.setIcon(new ImageIcon(iconBytes, altText));
		} catch (IOException ex) {
			ErrorLogHelper.exception(SwingUtil.class, "createImageLabel", ex);
			newButton.setText(altText);
		}
		if (listener != null) {
			newButton.addActionListener(listener);
		}
		return newButton;
	}

	public static JMenuItem createJMenuItem(String id, VNAMenuAndToolbarHandler listener, JLabel tooltipLabel, KeyStroke keyStroke) {
		JMenuItem rc = createJMenuItem(id, listener, tooltipLabel);
		rc.setAccelerator(keyStroke);
		return rc;
	}
}
