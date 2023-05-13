package krause.vna.gui;

import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import krause.util.ResourceLoader;
import krause.util.ras.logging.ErrorLogHelper;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;

public class BatteryButton extends JButton {

	private ImageIcon iconEmpty;
	private ImageIcon iconRed;
	private ImageIcon iconYellow;
	private ImageIcon iconGreen;

	private static ImageIcon readIconFromResource(String resName, String altText) {
		byte[] iconBytes = null;
		try {
			iconBytes = ResourceLoader.getResourceAsByteArray(resName);
		} catch (IOException ex) {
			ErrorLogHelper.exception(SwingUtil.class, "readIconFromResource", ex);
		}
		if (iconBytes == null) {
			return new ImageIcon();
		} else {
			return new ImageIcon(iconBytes, altText);
		}
	}

	public BatteryButton(String pResPrefix, ActionListener pListener) {
		String command = VNAMessages.getString(pResPrefix + ".Command");
		String tooltip = VNAMessages.getString(pResPrefix + ".Tooltip");
		// Create and initialize the button.
		setActionCommand(command);
		setToolTipText(tooltip);
		if (pListener != null) {
			addActionListener(pListener);
		}

		iconEmpty = readIconFromResource(VNAMessages.getString(pResPrefix + ".Image.Empty"), "Empty");
		iconRed = readIconFromResource(VNAMessages.getString(pResPrefix + ".Image.Red"), "Red");
		iconGreen = readIconFromResource(VNAMessages.getString(pResPrefix + ".Image.Green"), "Green");
		iconYellow = readIconFromResource(VNAMessages.getString(pResPrefix + ".Image.Yellow"), "Yellow");

		setState(0);
	}

	public void setState(int pState) {
		if (pState == 1) {
			setIcon(iconGreen);
		} else if (pState == 2) {
			setIcon(iconYellow);
		} else if (pState == 3) {
			setIcon(iconRed);
		} else {
			setIcon(iconEmpty);
		}
	}
}
