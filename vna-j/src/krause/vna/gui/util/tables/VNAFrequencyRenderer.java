package krause.vna.gui.util.tables;

import java.awt.Component;
import java.awt.Font;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import krause.vna.gui.format.VNAFormatFactory;

/**
 * @author Dietmar Krause
 * 
 */
public class VNAFrequencyRenderer extends JLabel implements TableCellRenderer {
    NumberFormat nf = null;

    Border unselectedBorder = null;

    Border selectedBorder = null;

    public VNAFrequencyRenderer() {
        setHorizontalAlignment(SwingConstants.TRAILING);
        nf = VNAFormatFactory.getFrequencyFormat();
        setFont(new Font("Dialog", Font.PLAIN, getFont().getSize()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
     *      java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
    	Long frq = (Long) value;
        setText(nf.format(frq.longValue()));
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
