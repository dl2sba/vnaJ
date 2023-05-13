package krause.vna.gui.util;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JMenuItemTooltipHelper implements ChangeListener {
	private JMenuItem menuItem;
	private JLabel statusBar;

	public JMenuItemTooltipHelper(JMenuItem menuItem, JLabel statusBar) {
		this.menuItem = menuItem;
		this.statusBar = statusBar;
		menuItem.addChangeListener(this);
	}

	public void stateChanged(ChangeEvent evt) {
		if (statusBar != null) {
			statusBar.setBackground(null);
			statusBar.setForeground(Color.BLACK);
			if (menuItem.isArmed())
				statusBar.setText(menuItem.getToolTipText());
			else
				statusBar.setText(" ");
		}
	}
}
