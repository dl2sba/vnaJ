package krause.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ResourceLoader {
	public final static byte[] getResourceAsByteArray(String name) throws IOException, NullPointerException {
		ResourceLoader p = new ResourceLoader();
		InputStream s = p.getDynamiceResourceAsStream(name);
		byte[] rc = new byte[s.available()];
		s.read(rc);
		s.close();
		return rc;
	}

	public final static InputStream getResourceAsStream(String name) throws IOException {
		ResourceLoader p = new ResourceLoader();
		return p.getDynamiceResourceAsStream(name);
	}

	private InputStream getDynamiceResourceAsStream(String resource) throws IOException, NullPointerException {
		URL url = getClass().getClassLoader().getResource(resource);
		return url.openStream();
	}
}
