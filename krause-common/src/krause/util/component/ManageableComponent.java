package krause.util.component;

import java.util.Properties;

import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;

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
 * 
 * A universal interface definition for managers. <br>
 * A manager must implement at least these methods
 */
public interface ManageableComponent {
	/**
	 * Deinitialize all managed objects
	 * 
	 * @throws ProcessingException
	 *             If something fails
	 *  
	 */
	public void destroy();

	/**
	 * Initialize the manager with the given properties <br>
	 * 
	 * @throws InitializationException
	 *             If initialization fails
	 * @param theProps
	 *            The properties used for initialization. Content depends on
	 *            individual implementation
	 */
	public void initialize(Properties theProps) throws InitializationException;

	/**
	 * Initialize the manager with the minimum data needed for operation <br>
	 * Generally used to instanciate default implementations for the manager
	 * <br>
	 * In case of a logmanager, a default implemtation with no action in it <br>
	 * 
	 * @throws InitializationException
	 *             If something fails
	 */
	public void initializeDefault() throws InitializationException;

	/**
	 * Method getVersion.
	 * 
	 * @return String
	 */
	public String getVersion();
}