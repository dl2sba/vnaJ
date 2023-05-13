package krause.util;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import krause.util.ras.logging.ErrorLogHelper;
/**
 * krause.util.StreamGobbler
 * <br>
 * Insert type comment here
 * ********************************************************************************** 
 * Copyright (C) 2003 Dietmar Krause, DL2SBA
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
public class StreamCollector extends Thread {
	InputStream is;
	String codePage;
	StringBuffer sb = null;
	/**
	 * @param is
	 * @param type
	 * @param redirect
	 */
	public StreamCollector(InputStream is, String codePage) {
		this.is = is;
		sb = new StringBuffer(5000);
		this.codePage = codePage;
		setName("StreamCollector");
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is, codePage);
			BufferedReader br = new BufferedReader(isr);
			for (;;) {
				int c = br.read();
				if (isInterrupted()) {
					break;
				}
				if (c != -1) {
					sb.append((char) c);
				} else {
					sleep(100);
				}
			}
		} catch (Exception ioe) {
			ErrorLogHelper.text(this, "run", ioe.toString());
		}
	}
	public String getBuffer() {
		return sb.toString();
	}
}
