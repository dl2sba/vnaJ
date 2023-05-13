package krause.util;

import java.math.BigDecimal;

/**
 * Filderwetter - framework for weather aquisistion and analysis
 * 
 * Copyright (C) 2003 Dietmar Krause, DL2SBA
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
public class NumericHelper {
    public static double getDoubleWithDefault(Double theVal, double theDef) {
        if (theVal == null) {
            return theDef;
        }
        return theVal.doubleValue();
    }

    public static double getDoubleWithDefault(Integer theVal, double theDef) {
        if (theVal == null) {
            return theDef;
        }
        return theVal.doubleValue();
    }

    public static int getIntegerWithDefault(Integer theVal, int theDef) {
        if (theVal == null) {
            return theDef;
        }
        return theVal.intValue();

    }

    public static Double convertBigDecimal2Double(BigDecimal val) {
        if (val == null) {
            return null;
        }
        return Double.valueOf(val.doubleValue());

    }
}