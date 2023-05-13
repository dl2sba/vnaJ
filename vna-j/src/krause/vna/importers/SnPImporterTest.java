/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: SnPImporterTest.java
 *  Part of:   vna-j
 */

package krause.vna.importers;

import junit.framework.TestCase;
import krause.common.exception.ProcessingException;

/**
 * @author Dietmar
 * 
 */
public class SnPImporterTest extends TestCase {

	public void test1() {
		SnPImporter importer = new SnPImporter();
		try {
			SnPInfoBlock ib = importer.readFile("C:/Users/Dietmar/vnaJ.3.4/reference/dlawik_T37-2_10zw-nanoVNA.s1p", "US-ASCII");
			System.out.println(ib);
			
		} catch (ProcessingException e) {
			e.printStackTrace();
		}
	}
}
