package krause.vna.gui.util.tables;

import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import krause.vna.device.IVNADriver;
import krause.vna.device.VNADriverFactory;

/**
 * @author Dietmar Krause
 * 
 */
public class VNAAnalyserTypeRenderer extends JLabel implements TableCellRenderer {
	private VNADriverFactory factory = VNADriverFactory.getSingleton();

	private Border unselectedBorder = null;

	private Border selectedBorder = null;

	public VNAAnalyserTypeRenderer() {
		setHorizontalAlignment(SwingConstants.LEADING);
		setFont(new Font("Dialog", Font.PLAIN, getFont().getSize()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax .swing.JTable, java.lang.Object, boolean,
	 * boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		String driverType = (String) value;
		IVNADriver drv = factory.getDriverForType(driverType);
		if (drv != null) {
			setText(drv.getDeviceInfoBlock().getShortName());
		}
		//
		if (isSelected) {
			if (selectedBorder == null) {
				selectedBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, table.getSelectionBackground());
			}
			setBorder(selectedBorder);
		} else {
			if (unselectedBorder == null) {
				unselectedBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, table.getBackground());
			}
			setBorder(unselectedBorder);
		}
		return this;
	}
}
