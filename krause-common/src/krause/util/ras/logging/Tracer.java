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

import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;

public interface Tracer {
	/**
	 * Destroys the initialized LogManager
	 * 
	 * @throws ProcessingException
	 *             If something fails
	 */
	void destroy() throws ProcessingException;

	/**
	 * Method entry.
	 * 
	 * @param theCaller
	 * @param theMethod
	 */
	public void entry(Object theCaller, String theMethod);

	/**
	 * Method exit.
	 * 
	 * @param theCaller
	 * @param theMethod
	 */
	public void exit(Object theCaller, String theMethod);

	/**
	 * Initialize the Tracer with the given properties <br>
	 * 
	 * @param parmProps
	 *            The properties. Its up to de implementing class to retrieve
	 *            the needed values
	 * @throws InitializationException
	 *             If initialization fails
	 */
	void initialize(java.util.Properties parmProps) throws InitializationException;

	/**
	 * Method text.
	 * 
	 * @param theCaller
	 * @param theMethod
	 * @param theMsg
	 */
	public void text(Object theCaller, String theMethod, String theFormat, Object ... theParms);

	/**
	 * Method entry.
	 * 
	 * @param theCaller
	 * @param theMethod
	 * @param theParms
	 */
	public void entry(Object theCaller, String theMethod, String format, Object... theParms);

	
	/**
	 * 
	 * @param theCaller
	 * @param theMethod
	 * @param text
	 */
	public void entry(Object theCaller, String theMethod, String text);

	/**
	 * Method exitWithRC.
	 * 
	 * @param theCaller
	 * @param theMethod
	 * @param rc
	 */
	public void exitWithRC(Object theCaller, String theMethod, Object rc);

	/**
	 * Method exitWithRC.
	 * 
	 * @param theCaller
	 * @param theMethod
	 * @param rc
	 */
	public void exitWithRC(Object theCaller, String theMethod, String format, Object... rc);
}