/**
 * Copyright (C) 2009 Dietmar Krause, DL2SBA
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
package krause.util.ras.logging;

import java.io.FileWriter;
import java.util.Properties;

import krause.common.exception.InitializationException;
import krause.util.GlobalSymbols;

public class SimpleFileLogger extends GenericLogger {
    public final static String FILENAME = "filename";

    public final static String APPEND = "append";

    private boolean fieldAppend = false;

    private String fieldFilename = null;

    public void setAppend(boolean append) {
        fieldAppend = append;
    }

    public boolean isAppend() {
        return fieldAppend;
    }

    public void setFilename(String filename) {
        fieldFilename = filename;
    }

    public String getFilename() {
        return fieldFilename;
    }

    /**
     * @see Logger#initialize(Properties)
     */
    public void initialize(Properties parmProps) throws InitializationException {
        System.out.println(getClass().getName() + "::initialize() entry");
        //
        super.initialize(parmProps);
        //
        try {
            setFilename((String) parmProps.get(FILENAME));
            setAppend(((String) parmProps.get(APPEND)) != null);
            //
            System.out.println(getClass().getName() + "::initialize() filename=" + getFilename());
            System.out.println(getClass().getName() + "::initialize() append=" + isAppend());
            //
            setWriter(new FileWriter(getFilename(), isAppend()));
            System.out.println(getClass().getName() + "::initialize() writer=" + getWriter());
        } catch (Exception ex) {
            throw new InitializationException(ex);
        }
        System.out.println(getClass().getName() + "::initialize() exit");
    }

    /**
     * @see Logger#text(Object, String, String)
     */
    public void text(Object theCaller, String theMethod, String theMsg) {
        if (getWriter() != null) {
            try {
                StringBuilder sbData = buildLineHeader(theCaller, theMethod);
                sbData.append(theMsg).append(GlobalSymbols.LINE_SEPARATOR);
                //
                getWriter().write(sbData.toString());
                getWriter().flush();
            } catch (Exception ex) {
            }
        }
    }
}