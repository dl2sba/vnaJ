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

public class LogHelper
{
   /**
    * Constructor for LogHelper.
    */
   public LogHelper()
   {
      super();
   }
   /**
    * Write an message to the log <br>
    * @param theCaller
    *           The calling object
    * @param theMethod
    *           The calling method
    * @param theMsg
    *           The message to log
    */
   public static void text( Object theCaller, String theMethod, String theMsg )
   {
      if( LogManager.getSingleton().isErrorLoggingEnabled() )
      {
         LogManager.getSingleton().getErrorLogger().text( theCaller, theMethod, theMsg );
      }
   }
}