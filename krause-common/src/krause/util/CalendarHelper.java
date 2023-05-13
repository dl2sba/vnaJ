package krause.util;

import java.util.Calendar;

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
public class CalendarHelper {
	/**
	 * Method setCalendarToEndOfDay.
	 * 
	 * @param theCal
	 */
	public static void setCalendarToEndOfDay(Calendar theCal) {
	    setCalendarToStartOfDay(theCal);
		theCal.set(Calendar.HOUR_OF_DAY, theCal.getActualMaximum(Calendar.HOUR_OF_DAY));
		theCal.set(Calendar.MINUTE, theCal.getActualMaximum(Calendar.MINUTE));
		theCal.set(Calendar.SECOND, theCal.getActualMaximum(Calendar.SECOND));
		theCal.set(Calendar.MILLISECOND, theCal.getActualMaximum(Calendar.MILLISECOND));
	}

	/**
	 * Method setCalendarToEndOfMonth.
	 * 
	 * @param theCal
	 */
	public static void setCalendarToEndOfMonth(Calendar theCal) {
	    setCalendarToStartOfMonth(theCal);
		theCal.set(Calendar.DAY_OF_MONTH, theCal.getActualMaximum(Calendar.DAY_OF_MONTH));
		setCalendarToEndOfDay(theCal);
	}

	/**
	 * Method setCalendarToStartDay.
	 * 
	 * @param theCal
	 */
	public static void setCalendarToStartOfDay(Calendar theCal) {
		theCal.set(Calendar.HOUR_OF_DAY, theCal.getActualMinimum(Calendar.HOUR_OF_DAY));
		theCal.set(Calendar.MINUTE, theCal.getActualMinimum(Calendar.MINUTE));
		theCal.set(Calendar.SECOND, theCal.getActualMinimum(Calendar.SECOND));
		theCal.set(Calendar.MILLISECOND, theCal.getActualMinimum(Calendar.MILLISECOND));
	}

	public static void setCalendarToStartOfWeek(Calendar theCal) {
		setCalendarToStartOfDay(theCal);
		theCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	}

	public static void setCalendarToEndOfWeek(Calendar theCal) {
		setCalendarToStartOfWeek(theCal);
		setCalendarToEndOfDay(theCal);
		theCal.add(Calendar.DAY_OF_WEEK, 6);
	}

	/**
	 * Method setCalendarToStartOfYear.
	 * 
	 * @param theCal
	 */
	public static void setCalendarToStartOfMonth(Calendar theCal) {
		theCal.set(Calendar.DAY_OF_MONTH, theCal.getActualMinimum(Calendar.DAY_OF_MONTH));
		setCalendarToStartOfDay(theCal);
	}

	/**
	 * Method setCalendarToStartYear.
	 * 
	 * @param theCal
	 */
	public static void setCalendarToStartOfYear(Calendar theCal) {
		theCal.set(Calendar.MONTH, theCal.getActualMinimum(Calendar.MONTH));
		setCalendarToStartOfMonth(theCal);
	}

	public static void setCalendarToEndOfYear(Calendar theCal) {
	    setCalendarToStartOfYear(theCal);
		theCal.set(Calendar.MONTH, theCal.getActualMaximum(Calendar.MONTH));
		setCalendarToEndOfMonth(theCal);
	}
}