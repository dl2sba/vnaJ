package krause.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import krause.common.exception.ProcessingException;

/**
 * krause.util.URIHelper
 * **********************************************************************************
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
public class URIHelper {
    public final static String SHOW_URI_RESOLVE_INFO = "showURIResolverInfo";

    public static URL getResource(String relativeFileName) throws ProcessingException {
        boolean showLoadInfo = "yes".equalsIgnoreCase(System.getProperty(SHOW_URI_RESOLVE_INFO, "no"));
        if (showLoadInfo) {
            System.out.println("Determining URL for resource: [" + relativeFileName + "]");
        }
        URL url = null;
        try {
            // first check if file name is really relative
            File file = new File(relativeFileName);
            if (file.isAbsolute()) {
                url = new URL("file", null, relativeFileName);
                if (showLoadInfo) {
                    System.out.println("Provided resource name is absolute file location, use file URL");
                }
            } else {
                if (showLoadInfo) {
                    System.out.println("Provided resource name is relative location");
                }
                // second try to get resource via the classloader
                url = URIHelper.class.getClassLoader().getResource(relativeFileName);
                if (showLoadInfo) {
                    if (url == null) {
                        System.out.println("Provided resource is not found via the classloader");
                    } else {
                        System.out.println("Provided resource can be loaded using the classloader");
                    }
                }
                if (url == null) {
                    // not found via class loader, use context to INI-file
                    url = new URL(relativeFileName);
                }
            }
        } catch (IOException ioex) {
            // throw runtime exception to indicate missing resource
            throw new ProcessingException("Resource [" + relativeFileName + "] could not be found. Exception is ["
                    + ioex + "]");
        }
        if (showLoadInfo) {
            System.out.println("Returning URL: " + url);
        }
        return url;
    }

    /**
     * Returns an open stream to the file identified by the provided file name.<br>
     * See {@link #getResource} for further details.
     * 
     * @param relativeFileName
     *            the resource name
     * 
     * @return the open input stream, never <code>null</code>
     * 
     * @exception ResourceNotFoundException
     *                if the resource was not found and the stream couldn't be
     *                opened
     */
    public static InputStream getResourceAsStream(String relativeFileName) throws ProcessingException {
        URL url = getResource(relativeFileName);
        try {
            return url.openStream();
        } catch (IOException ioex) {
            // throw runtime exception to indicate missing resource
            throw new ProcessingException("Resource [" + relativeFileName + "] could not be loaded: " + ioex);
        }
    }

    /**
     * @param url
     *            the url to check for existence
     * 
     * @return <code>true</code> if this url exists
     */
    public static boolean isUrlExistent(URL url) {
        // open stream to resource, if this fails, Url is not existent
        try {
            InputStream in = url.openStream();
            // close stream at once, we don't need it here
            in.close();
        } catch (IOException ioex) {
            // OK, Url doesn't seem to be valid
            return false;
        }
        return true;
    }
}
