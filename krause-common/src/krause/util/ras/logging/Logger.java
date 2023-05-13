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

import java.io.Writer;

import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;

public interface Logger {
	/**
	 * Destroys the initialized LogManager
	 * 
	 * @throws IVMProcessingException
	 *             If something fails
	 */
	void destroy() throws ProcessingException;

	/**
	 * Initialize the LogManager with the given properties <br>
	 * 
	 * @param parmProps
	 *            The properties. Its up to de implementing class to retrieve
	 *            the needed values
	 * @throws IVMInitializationException
	 *             If initialization fails
	 */
	void initialize(java.util.Properties parmProps) throws InitializationException;

	/**
	 * Write an message to the log <br>
	 * 
	 * @param theCaller
	 *            The calling object
	 * @param theMethod
	 *            The calling method
	 * @param theFormat
	 *            The message to log
	 */
	public void text(Object theCaller, String theMethod, String theFormat, Object... theParms);

	/**
	 * Write an exceptin to the log <br>
	 * 
	 * @param theCaller
	 *            The calling object
	 * @param theMethod
	 *            The calling method
	 * @param theExc
	 *            The exception to log
	 */
	public void text(Object theCaller, String theMethod, Exception theExc);

	/**
	*  
	*/
	public Writer getWriter();
}