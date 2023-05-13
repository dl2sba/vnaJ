package krause.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class PropertiesLoader {
    public final static InputStream getResourceAsStream(String name) throws IOException {
        PropertiesLoader p = new PropertiesLoader();
        return p.getDynamiceResourceAsStream(name);
    }

    public final static Properties getPropertiesFromFile(String propertiesFile) throws IOException {
        Properties props = new Properties();
        InputStream is = null;
        try {
            is = getResourceAsStream(propertiesFile);
            props.load(is);
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
        }
        return props;
    }

    private InputStream getDynamiceResourceAsStream(String resource) throws IOException {
        URL url = getClass().getClassLoader().getResource(resource);
        return url.openStream();
    }

}