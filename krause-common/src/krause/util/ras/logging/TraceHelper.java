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

public class TraceHelper {
	/**
	 * 
	 * 
	 */
	public TraceHelper() {
		super();
	}

	/**
	 * 
	 * @param theCaller
	 * @param theMethod
	 * @param theMsg
	 */
	public static void text(Object theCaller, String theMethod, String theFormat, Object ... theParms) {
		if (LogManager.getSingleton().isTracingEnabled()) {
			LogManager.getSingleton().getTracer().text(theCaller, theMethod, theFormat, theParms);
		}
	}

	/**
	 * 
	 * @param theCaller
	 * @param theMethod
	 */
	public static void entry(Object theCaller, String theMethod) {
		if (LogManager.getSingleton().isTracingEnabled()) {
			LogManager.getSingleton().getTracer().entry(theCaller, theMethod);
		}
	}

	/**
	 * 
	 * @param theCaller
	 * @param theMethod
	 */
	public static void exit(Object theCaller, String theMethod) {
		if (LogManager.getSingleton().isTracingEnabled()) {
			LogManager.getSingleton().getTracer().exit(theCaller, theMethod);
		}
	}

	/**
	 * 
	 * @param theCaller
	 * @param theMethod
	 * @param theParms
	 */
	public static void entry(Object theCaller, String theMethod, String theFormat, Object ... theParms) {
		if (LogManager.getSingleton().isTracingEnabled()) {
			LogManager.getSingleton().getTracer().entry(theCaller, theMethod, theFormat, theParms);
		}
	}

	/**
	 * 
	 * @param theCaller
	 * @param theMethod
	 * @param text
	 */
	public static void entry(Object theCaller, String theMethod, String text) {
		if (LogManager.getSingleton().isTracingEnabled()) {
			LogManager.getSingleton().getTracer().entry(theCaller, theMethod, text);
		}
	}

	/**
	 * Method exitWithRC.
	 * 
	 * @param theCaller
	 * @param theMethod
	 * @param rc
	 */
	public static void exitWithRC(Object theCaller, String theMethod, Object rc) {
		if (LogManager.getSingleton().isTracingEnabled()) {
			LogManager.getSingleton().getTracer().exitWithRC(theCaller, theMethod, rc);
		}
	}

	/**
	 * Method exitWithRC.
	 * 
	 * @param theCaller
	 * @param theMethod
	 * @param rc
	 */
	public static void exitWithRC(Object theCaller, String theMethod, String theFormat, Object ... rc) {
		if (LogManager.getSingleton().isTracingEnabled()) {
			LogManager.getSingleton().getTracer().exitWithRC(theCaller, theMethod, theFormat, rc);
		}
	}
}