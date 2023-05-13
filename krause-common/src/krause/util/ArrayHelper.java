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
package krause.util;

public abstract class ArrayHelper {
	public static String intArrayToString(int[] array) {
		String rc = new String();
		;
		for (int i = 0; i < array.length; ++i) {
			rc += array[i] + " ";
		}
		return rc;
	}

	public static String byteArrayToString(byte[] array) {
		String rc = new String();
		;
		for (int i = 0; i < array.length; ++i) {
			rc += Integer.toHexString(array[i]) + " ";
		}
		return rc;
	}

	/**
	 * Method intArray2ByteArray.
	 * 
	 * @param inbuf
	 * @return byte[]
	 */
	public static byte[] intArray2ByteArray(int[] inbuf) { //
		// convert to byte array
		byte[] tbuf = new byte[inbuf.length];
		for (int i = 0; i < inbuf.length; ++i) {
			tbuf[i] = (byte) (inbuf[i] % 256);
		}
		return tbuf;
	}

	/**
	 * @param rc
	 * @return
	 */
	public static String doubleArrayToString(double[] inp) {
		String rc = new String();

		if (inp != null) {
			for (int i = 0; i < inp.length; ++i) {
				rc += Double.toString(inp[i]) + " ";
			}
		}
		return rc;
	}
}
