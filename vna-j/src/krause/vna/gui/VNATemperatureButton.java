package krause.vna.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.resources.VNAMessages;

public class VNATemperatureButton extends JButton implements ActionListener {

	private VNAMainFrame mainFrame;

	private static final int MAX_LIST = 1000;
	private double[] tempList = new double[MAX_LIST];
	private boolean firstTemp = true;

	public VNATemperatureButton(final VNAMainFrame pMainFrame, String pResPrefix, ActionListener pListener) {
		mainFrame = pMainFrame;

		final String command = VNAMessages.getString(pResPrefix + ".Command");
		final String tooltip = VNAMessages.getString(pResPrefix + ".Tooltip");
		// Create and initialize the button.
		setActionCommand(command);
		setToolTipText(tooltip);
		if (pListener != null) {
			addActionListener(pListener);
		}
		addActionListener(this);

		//
		for (int i = 0; i < MAX_LIST; ++i) {
			tempList[i] = 0;
		}

		setTemperature(null);
	}

	public void setTemperature(Double temp) {
		TraceHelper.entry(this, "setTemperature");

		if (temp != null) {
			setText(VNAFormatFactory.getTemperatureFormat().format(temp) + "°C");

			if (firstTemp) {
				for (int i = 0; i < MAX_LIST; ++i) {
					tempList[i] = temp;
				}
				firstTemp = false;
			} else {
				// shift every thing one to the left
				for (int i = 1; i < MAX_LIST; ++i) {
					tempList[i - 1] = tempList[i];
				}

				// and put new value at first
				tempList[MAX_LIST - 1] = temp;
			}
		} else {
			setToolTipText("");
		}
		TraceHelper.exit(this, "setTemperature");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		new VNATemperatureDetailsDialog(mainFrame.getJFrame(), tempList);
	}
}
