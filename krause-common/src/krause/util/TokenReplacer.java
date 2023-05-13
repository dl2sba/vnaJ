package krause.util;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Properties;

/**
 * Filderwetter - framework for weather aquisistion and analysis
 * 
 * Copyright (C) 2003 Dietmar Krause, DL2SBA
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * This class is used to substitute tokens in a String with a specific value. The tokens must be separated by a delimiter. A list of
 * tokens with its corresponding substitution values must be provided.
 */
public class TokenReplacer extends HashMap<String, String> implements GlobalSymbols {

	private String delimiter = null;

	public TokenReplacer() {
		super();
	}

	/**
	 * creates a new token-replacer and reads replacement values abd tokens from properties
	 * 
	 * @param newDelimiter
	 * @param newTokenList
	 */
	public TokenReplacer(String newDelimiter, Properties props, String tokenPostfix, String valuePostfix) {
		setDelimiter(newDelimiter);
		int i = 1;
		while (i > 0) {
			String tokKey = String.valueOf(i) + tokenPostfix;
			String valKey = String.valueOf(i) + valuePostfix;
			String tok = props.getProperty(tokKey);
			String val = props.getProperty(valKey);
			if (tok != null) {
				put(tok, val);
				++i;
			} else {
				i = -1;
			}
		}
	}

	/**
	 * creates a new replacer with a already defined token list
	 * 
	 * @param newDelimeter
	 *            String
	 * @param newTokenList
	 *            HashMap
	 */
	public TokenReplacer(String newDelimiter, HashMap<String, String> newTokenList) {
		setDelimiter((newDelimiter != null) ? newDelimiter : "");
		putAll(newTokenList);
	}

	/**
	 * adds a new token to the token list
	 * 
	 * @param token
	 *            the name of the token
	 * @param value
	 *            the new value
	 */
	public void addToken(String token, String value) {
		put(token, value);
	}

	/**
	 * Method getDelimiter.
	 * 
	 * @return String
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * This method parses the string and replaces the placeholders. <br>
	 * Unknown placeholders are not replaced and kept in place including the delimiter chars.
	 *
	 * @param sOld
	 *            the string to act on
	 * @return the string with replaced tokens
	 */
	public String replace(String sOld) {
		boolean inVar = false;
		StringBuilder varName = new StringBuilder(GlobalSymbols.DEFAULT_SMALL_STRINGBUFFER_SIZE);
		StringBuilder result = new StringBuilder(2 * sOld.length());
		//
		try {
			StringReader theReader = new StringReader(sOld);
			int ci = 0;
			char delimiterChar = '$';
			// while reader has data
			while ((ci = theReader.read()) != -1) {
				// get char at current pos
				char cc = (char) ci;
				// in tokenized variable ?
				if (inVar) {
					// yes
					// delimiter char found ?
					if (delimiterChar == cc) {
						// yes
						// get variable from replacement pool
						String replacement = (String) get(varName.toString());
						// variable found ?
						if (replacement == null) {
							// no
							// add start and end delimiter again to variable name and put it in result
							result.append(delimiterChar).append(varName.toString()).append(delimiterChar);
						} else {
							// yes
							// append variable replacement value to result.
							result.append(replacement);
						}
						inVar = false;
					} else {
						// no
						// add character to name of variable
						varName.append(cc);
					}
				} else {
					// find delimiter
					int delimiterIndex = getDelimiter().indexOf(cc);
					// which delimiter ?
					if (delimiterIndex != -1) {
						// remember starting delimiter
						delimiterChar = getDelimiter().charAt(delimiterIndex);
						//
						inVar = true;
					} else {
						result.append(cc);
					}
				}
			}
		} catch (Exception npex) {
			result = new StringBuilder(sOld);
		}
		return result.toString();
	}

	/**
	 * Sets the delimiter.
	 * 
	 * @param delimiter
	 *            The delimiter to set
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getProperty(Object key) {
		return (String) get(key);
	}
}
