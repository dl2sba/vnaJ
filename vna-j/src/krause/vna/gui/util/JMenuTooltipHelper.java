package krause.vna.gui.util;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class JMenuTooltipHelper implements MenuListener {
	private JMenu menu;
	private JLabel statusBar;

	public JMenuTooltipHelper(JMenu menu, JLabel statusBar) {
		this.menu = menu;
		this.statusBar = statusBar;
		menu.addMenuListener(this);
	}

	public void menuCanceled(MenuEvent e) {
		statusBar.setText(" ");
	}

	public void menuDeselected(MenuEvent e) {
		statusBar.setText(" ");
	}

	public void menuSelected(MenuEvent e) {
		statusBar.setText(menu.getToolTipText());
	}
}
