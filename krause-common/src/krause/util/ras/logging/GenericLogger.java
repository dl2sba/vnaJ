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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Properties;

import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;
import krause.util.GlobalSymbols;

public class GenericLogger implements Logger {
	private boolean fieldShortClassname = false;

	private Writer fieldWriter = null;

	public final static String SHORTCLASSNAME = "shortclassname";

	//
	public boolean isShortClassname() {
		return fieldShortClassname;
	}

	public void setShortClassname(boolean shortClassname) {
		fieldShortClassname = shortClassname;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.util.log.Logger#destroy()
	 */
	public void destroy() throws ProcessingException {
		try {
			if (getWriter() != null) {
				getWriter().flush();
				getWriter().close();
			}
		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.util.log.Logger#initialize(java.util.Properties)
	 */
	public void initialize(Properties parmProps) throws InitializationException {
		try {
			setShortClassname("true".equalsIgnoreCase((String) parmProps.get(SHORTCLASSNAME)));
		} catch (Exception ex) {
			throw new InitializationException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.util.ras.logging.Logger#text(java.lang.Object,
	 * java.lang.String, java.lang.String, java.lang.Object[])
	 */
	public void text(Object theCaller, String theMethod, String theMsg, Object... theParms) {
		StringBuilder sbData = new StringBuilder();
		sbData.append(buildLineHeader(theCaller, theMethod)).append(' ').append(String.format(theMsg, theParms)).append(GlobalSymbols.LINE_SEPARATOR);
		//
		try {
			getWriter().write(sbData.toString());
			getWriter().flush();
		} catch (IOException e) {
		}
	}

	/**
	 * Method buildLineHeader.
	 * 
	 * @param theCaller
	 * @param theMethod
	 * @return StringBuffer
	 */
	protected StringBuilder buildLineHeader(Object theCaller, String theMethod) {
		StringBuilder sbData = new StringBuilder(GlobalSymbols.DEFAULT_SMALL_STRINGBUFFER_SIZE);
		sbData.append(java.text.DateFormat.getDateTimeInstance().format(new java.util.Date()));
		String classname = theCaller.getClass().getName();
		if (isShortClassname()) {
			int i = classname.lastIndexOf('.');
			if (i != -1) {
				classname = classname.substring(i + 1);
			}
		}
		sbData.append(" L:").append(classname).append("::").append(theMethod).append("() ");
		return sbData;
	}

	public void setWriter(Writer writer) {
		fieldWriter = writer;
	}

	public Writer getWriter() {
		return fieldWriter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.util.log.Logger#text(java.lang.Object, java.lang.String,
	 * java.lang.Exception)
	 */
	public void text(Object theCaller, String theMethod, Exception theExc) {
		StringBuffer sbData = new StringBuffer();
		sbData.append(buildLineHeader(theCaller, theMethod));
		sbData.append(theExc.toString()).append("\n");
		//
		try {
			getWriter().write(sbData.toString());
			theExc.printStackTrace(new PrintWriter(getWriter()));
			getWriter().flush();
		} catch (IOException e) {
		}
	}
}