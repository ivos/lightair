package net.sf.lightair.internal.properties;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import net.sf.lightair.exception.MissingPropertyException;
import net.sf.lightair.exception.PropertiesNotFoundException;
import net.sf.lightair.exception.PropertiesUnreadableException;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides Light air properties.
 */
public class PropertiesProvider {

	private final Logger log = LoggerFactory
			.getLogger(PropertiesProvider.class);

	private final Properties properties = new Properties();

	/**
	 * Initialize.
	 * <p>
	 * Loads properties from the .properties file.
	 */
	public void init() {
		log.debug("Initializing properties.");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
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
		stopWatch.stop();
		log.debug("Initialized properties in {} ms.", stopWatch.getTime());
	}

	public static final String DEFAULT_PROPERTIES_FILE_NAME = "light-air.properties";
	private String propertiesFileName = DEFAULT_PROPERTIES_FILE_NAME;

	public void setPropertiesFileName(String propertiesFileName) {
		this.propertiesFileName = propertiesFileName;
	}

	/**
	 * Get mandatory property value from properties file.
	 * <p>
	 * Value is trimmed.
	 * 
	 * @param key
	 *            Property key
	 * @return Trimmed property value
	 * @throws MissingPropertyException
	 *             when no such property is defined.
	 */
	public String getProperty(String key) {
		String rawValue = properties.getProperty(key);
		if (null == rawValue) {
			throw new MissingPropertyException(key);
		}
		String trimmedValue = rawValue.trim();
		log.debug("Providing property [{}] as [{}].", key, trimmedValue);
		return trimmedValue;
	}

	/**
	 * Get optional property value from properties file as long.
	 * <p>
	 * Property value is trimmed and converted to long.
	 * 
	 * @param key
	 *            Property key
	 * @param defaultValue
	 *            Default value to return when the property is not defined
	 * @return Property value converted to long or the default value when the
	 *         property is not defined
	 */
	public long getProperty(String key, long defaultValue) {
		String rawValue = properties.getProperty(key);
		long longValue;
		if (null == rawValue) {
			longValue = defaultValue;
		} else {
			String trimmedValue = rawValue.trim();
			longValue = Long.valueOf(trimmedValue).longValue();
		}
		log.debug("Providing property [{}] as [{}].", key, longValue);
		return longValue;
	}

	/**
	 * Return all property names with prefix "dbunit.features.".
	 * 
	 * @return The property names
	 */
	public Set<String> getDbUnitFeatureNames() {
		return getPropertyKeysWithPrefix("dbunit.features.");
	}

	/**
	 * Return all property names with prefix "dbunit.properties.".
	 * 
	 * @return The property names
	 */
	public Set<String> getDbUnitPropertyNames() {
		return getPropertyKeysWithPrefix("dbunit.properties.");
	}

	private Set<String> getPropertyKeysWithPrefix(String prefix) {
		Set<String> names = new HashSet<String>();
		Enumeration<Object> keys = properties.keys();
		for (String key; keys.hasMoreElements();) {
			key = (String) keys.nextElement();
			if (key.startsWith(prefix)) {
				names.add(key);
			}
		}
		return names;
	}

	protected Properties getProperties() {
		return properties;
	}

}
