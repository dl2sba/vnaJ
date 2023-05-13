/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: SortedProperties.java
 *  Part of:   vna-j
 */

package krause.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

/**
 * @author Dietmar
 * 
 */
public class SortedProperties extends Properties {

	@SuppressWarnings({
			"unchecked",
			"rawtypes"
	})
	public synchronized Enumeration keys() {
		Enumeration keysEnum = super.keys();
		Vector keyList = new Vector();
		while (keysEnum.hasMoreElements()) {
			keyList.add(keysEnum.nextElement());
		}
		Collections.sort(keyList);
		return keyList.elements();
	}

}
