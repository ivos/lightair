package net.sf.lightair.support.properties;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Properties;

import net.sf.lightair.exception.PropertiesNotFoundException;

/**
 * Provides Light air properties.
 */
public class PropertiesProvider {

	/**
	 * Name of Light air properties file.
	 */
	public static final String PROPERTIES_FILE_NAME = "light-air.properties";

	/**
	 * Prefix of all Light air properties.
	 */
	private static final String PROPERTY_PREFIX = "light-air.";

	private final Properties properties;

	/**
	 * Default constructor.
	 */
	public PropertiesProvider() {
		properties = new Properties();
		loadProperties();
	}

	private void loadProperties() {
		try {
			URLConnection urlConnection = getClass().getClassLoader()
					.getResource(PROPERTIES_FILE_NAME).openConnection();
			urlConnection.setUseCaches(false);
			InputStream is = urlConnection.getInputStream();
			properties.load(is);
			is.close();
		} catch (IOException e) {
			throw new PropertiesNotFoundException(e);
		}
	}

	/**
	 * Get property value from Light air properties file.
	 * <p>
	 * Key gets pre-pended with "light-air.". Value is trimmed.
	 * 
	 * @param key
	 *            Property key, excluding the "light-air." prefix
	 * @return Trimmed property value
	 */
	public String getProperty(String key) {
		return properties.getProperty(PROPERTY_PREFIX + key).trim();
	}

}
