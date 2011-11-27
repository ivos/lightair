package net.sf.lightair.internal.properties;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import net.sf.lightair.exception.MissingPropertyException;
import net.sf.lightair.exception.PropertiesNotFoundException;
import net.sf.lightair.exception.PropertiesUnreadableException;

/**
 * Provides Light air properties.
 */
public class PropertiesProvider {

	private final Properties properties = new Properties();

	/**
	 * Initialize.
	 * <p>
	 * Loads properties from the .properties file.
	 */
	public void init() {
		try {
			URL resource = getClass().getClassLoader().getResource(
					propertiesFileName);
			if (null == resource) {
				throw new PropertiesNotFoundException(propertiesFileName);
			}
			URLConnection urlConnection = resource.openConnection();
			urlConnection.setUseCaches(false);
			InputStream is = urlConnection.getInputStream();
			try {
				properties.load(is);
			} finally {
				is.close();
			}
		} catch (IOException e) {
			throw new PropertiesUnreadableException(propertiesFileName);
		}
	}

	public static final String DEFAULT_PROPERTIES_FILE_NAME = "light-air.properties";
	private String propertiesFileName = DEFAULT_PROPERTIES_FILE_NAME;

	public void setPropertiesFileName(String propertiesFileName) {
		this.propertiesFileName = propertiesFileName;
	}

	/**
	 * Get property value from properties file.
	 * <p>
	 * Value is trimmed.
	 * 
	 * @param key
	 *            Property key, excluding the "light-air." prefix
	 * @return Trimmed property value
	 */
	public String getProperty(String key) {
		String rawValue = properties.getProperty(key);
		if (null == rawValue) {
			throw new MissingPropertyException(key);
		}
		return rawValue.trim();
	}

}
