package krause.vna.resources.help;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConvEncoding {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), StandardCharsets.UTF_8))) {

			Properties properties = new Properties();

			String line;
			while ((line = br.readLine()) != null) {
				int i = line.indexOf('=');
				if (i != -1) {
					String key = line.substring(0, i);
					String value = line.substring(i + 1);
					properties.put(key, value);
				}
			}
			File file = new File(args[1]);
			FileOutputStream fileOut = new FileOutputStream(file);
			properties.store(fileOut, ConvEncoding.class.getCanonicalName());
			fileOut.close();
		}
	}

}
