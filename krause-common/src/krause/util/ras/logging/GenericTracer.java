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
import java.io.Writer;
import java.util.IllegalFormatException;
import java.util.Properties;

import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;
import krause.util.GlobalSymbols;

public class GenericTracer implements Tracer {
	private boolean fieldShortClassname = false;

	private Writer fieldWriter = null;

	public final static String SHORTCLASSNAME = "shortclassname";

	//
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

	public void initialize(Properties parmProps) throws InitializationException {
		try {
			setShortClassname("true".equalsIgnoreCase((String) parmProps.get(SHORTCLASSNAME)));
		} catch (Exception ex) {
			throw new InitializationException(ex);
		}
	}

	public void setShortClassname(boolean shortClassname) {
		fieldShortClassname = shortClassname;
	}

	public boolean isShortClassname() {
		return fieldShortClassname;
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
		sbData.append(" T:").append(classname).append("::").append(theMethod).append("()");
		return sbData;
	}

	public void setWriter(Writer writer) {
		fieldWriter = writer;
	}

	public Writer getWriter() {
		return fieldWriter;
	}

	/**
	 * @see Tracer#text(Object, String, String)
	 */
	public void text(Object theCaller, String theMethod, String theFormat, Object... theMsgParms) {
		try {
			StringBuilder sbData = buildLineHeader(theCaller, theMethod);
			sbData.append(' ').append(String.format(theFormat, theMsgParms)).append(GlobalSymbols.LINE_SEPARATOR);
			//
			getWriter().write(sbData.toString());
			getWriter().flush();
		} catch (Exception ex) {
			// as desired
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.util.ras.logging.Tracer#entry(java.lang.Object, java.lang.String, java.lang.String, java.lang.Object[])
	 */
	public void entry(Object theCaller, String theMethod, String format, Object... theParms) {
		try {
			StringBuilder sbData = buildLineHeader(theCaller, theMethod);
			sbData.append("-entry ");
			sbData.append(String.format(format, theParms));
			sbData.append(GlobalSymbols.LINE_SEPARATOR);
			//
			getWriter().write(sbData.toString());
			getWriter().flush();
		} catch (IllegalFormatException | IOException ex) {
			ErrorLogHelper.exception(this, "entry", ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.util.ras.logging.Tracer#entry(java.lang.Object, java.lang.String, java.lang.String)
	 */
	public void entry(Object theCaller, String theMethod, String text) {
		try {
			StringBuilder sbData = buildLineHeader(theCaller, theMethod);
			sbData.append("-entry ");
			sbData.append(text);
			sbData.append(GlobalSymbols.LINE_SEPARATOR);
			//
			getWriter().write(sbData.toString());
			getWriter().flush();
		} catch (Exception ex) {
		}
	}

	/**
	 * @see Tracer#exitWithRC(Object, String, Object)
	 */
	public void exitWithRC(Object theCaller, String theMethod, Object rc) {
		try {
			StringBuilder sbData = buildLineHeader(theCaller, theMethod);
			sbData.append("- exit RC=[").append(rc).append("]").append(GlobalSymbols.LINE_SEPARATOR);
			//
			getWriter().write(sbData.toString());
			getWriter().flush();
		} catch (Exception ex) {
		}
	}

	/**
	 * @see Tracer#exitWithRC(Object, String, Object[])
	 */
	public void exitWithRC(Object theCaller, String theMethod, String format, Object... theParms) {
		try {
			StringBuilder sbData = buildLineHeader(theCaller, theMethod);
			sbData.append("- exit ");
			sbData.append(String.format(format, theParms));
			sbData.append(GlobalSymbols.LINE_SEPARATOR);
			//
			getWriter().write(sbData.toString());
			getWriter().flush();
		} catch (Exception ex) {
		}
	}

	/**
	 * @see Tracer#entry(Object, String)
	 */
	public void entry(Object theCaller, String theMethod) {
		try {
			StringBuilder sbData = buildLineHeader(theCaller, theMethod);
			sbData.append("-entry").append(GlobalSymbols.LINE_SEPARATOR);
			//
			//
			getWriter().write(sbData.toString());
			getWriter().flush();
		} catch (Exception ex) {
		}
	}

	/**
	 * @see Tracer#exit(Object, String)
	 */
	public void exit(Object theCaller, String theMethod) {
		try {
			StringBuilder sbData = buildLineHeader(theCaller, theMethod);
			sbData.append("-exit").append(GlobalSymbols.LINE_SEPARATOR);
			//
			getWriter().write(sbData.toString());
			getWriter().flush();
		} catch (Exception ex) {
		}
	}
}