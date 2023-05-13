package krause.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import krause.util.ras.logging.ErrorLogHelper;

/**
 * krause.util.StreamGobbler <br>
 * Insert type comment here ********************************************************************************** Copyright (C) 2003
 * Dietmar Krause, DL2SBA
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
public class StreamStringBufferCollector extends Thread {
	InputStream is;
	String codePage;
	StringWriter os;

	/**
	 * @param is
	 * @param type
	 * @param redirect
	 */
	public StreamStringBufferCollector(InputStream is, String codePage) {
		this.is = is;
		this.codePage = codePage;
		os = new StringWriter(4096);
		setName("StreamCollector");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is, codePage);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				os.write(line);
				os.write(GlobalSymbols.LINE_SEPARATOR);
			}
			if (os != null)
				os.flush();
		} catch (Exception ioe) {
			ErrorLogHelper.text(this, "run", ioe.toString());
		}
	}

	public String getBuffer() {
		return os.toString();
	}
}
