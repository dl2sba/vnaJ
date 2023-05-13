/**
 * Copyright (C) 2007 Dietmar Krause, DL2SBA
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package krause.vna.gui.util;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * @author Dietmar Krause
 * 
 */
public class VNAGridBagConstraints extends GridBagConstraints {
    private static Insets DEFAULT_INSETS = new Insets(2, 2, 2, 2);

    /**
     * @param gridx
     * @param gridy
     * @param gridwidth
     * @param gridheight
     * @param weightx
     * @param weighty
     */
    public VNAGridBagConstraints(int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty) {
        super(gridx, gridy, gridwidth, gridheight, weightx, weighty, FIRST_LINE_START, NONE, DEFAULT_INSETS, 0, 0);
    }

}
