package krause.util;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
/**
 * krause.util.FileCopier
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
public class FileCopier {
	public static InputStream getInputStream(String fileName) throws IOException {
		InputStream input;
		if (fileName.startsWith("http:")) {
			URL url = new URL(fileName);
			URLConnection connection = url.openConnection();
			input = connection.getInputStream();
		} else {
			input = new FileInputStream(fileName);
		}
		return input;
	}
	public static OutputStream getOutputStream(String fileName) throws IOException {
		return new FileOutputStream(fileName);
	}
	public static int copy(InputStream in, OutputStream out) throws IOException {
		int bytesCopied = 0;
		byte[] buffer = new byte[4096];
		int bytes;
		try {
			while ((bytes = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytes);
				bytesCopied += bytes;
			}
		} finally {
			in.close();
			out.close();
		}
		return bytesCopied;
	}
}
