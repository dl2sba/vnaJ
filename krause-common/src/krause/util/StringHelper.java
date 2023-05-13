/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.util;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;

public abstract class StringHelper {
	public static String double2String(double val) {
		return NumberFormat.getInstance().format(val);
	}

	public static String int2String(int val) {
		return NumberFormat.getInstance().format(val);
	}

	public static String trimLength(String input, int maxlen) {
		if (input != null) {
			if (input.length() > maxlen) {
				return input.substring(0, maxlen);
			}
		}
		return input;
	}

	public static String array2String(String[] strings, String delimiter) {
		StringBuffer sb = new StringBuffer();
		int max = strings.length - 1;

		for (int i = 0; i <= max; ++i) {
			sb.append(strings[i]);
			// if (i != max)
			sb.append(delimiter);
		}
		return sb.toString();
	}

	static final byte[] TBL_HEX = { (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'a',
			(byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f' };

	/**
	 * converts the byte array into a hex string 
	 * @param input
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String byteArrayToString1(byte[] input) throws UnsupportedEncodingException {
		byte[] hex = new byte[3 * input.length];
		int index = 0;

		for (byte b : input) {
			int v = b & 0xFF;
			hex[index++] = TBL_HEX[v >>> 4];
			hex[index++] = TBL_HEX[v & 0xF];
			hex[index++] = ' ';
		}
		return new String(hex, "ASCII");
	}

	/**
	 * convert a byte array into string
	 * 
	 * @param input
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String byteArrayToString2(byte[] input) throws UnsupportedEncodingException {
		byte[] hex = new byte[3 * input.length];
		int index = 0;

		for (byte b : input) {
			hex[index++] = b;
			hex[index++] = ' ';
			hex[index++] = ' ';
		}
		return new String(hex, "ASCII");
	}
}