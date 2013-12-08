package net.sf.lightair.internal.properties;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.sf.lightair.exception.MissingPropertyException;
import net.sf.lightair.exception.ProfileNotDefinedException;
import net.sf.lightair.exception.PropertiesNotFoundException;
import net.sf.lightair.exception.PropertiesUnreadableException;
import net.sf.lightair.internal.util.Profiles;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides Light air properties.
 */
public class PropertiesProvider {

	private final Logger log = LoggerFactory
			.getLogger(PropertiesProvider.class);

	private final Map<String, Properties> properties = new HashMap<String, Properties>();

	public PropertiesProvider() {
		properties.put(Profiles.DEFAULT_PROFILE, new Properties());
	}

	/**
	 * Initialize.
	 * <p>
	 * Loads properties from the .properties file.
	 */
	public void init() {
		final String resolvedPropertiesFileName = getPropertiesFileName();
		log.debug("Initializing properties from {}.",
				resolvedPropertiesFileName);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		loadPropertiesForProfile(Profiles.DEFAULT_PROFILE,
				resolvedPropertiesFileName);
		loadPropertiesForProfiles();

		stopWatch.stop();
		log.debug("Initialized properties in {} ms.", stopWatch.getTime());
	}

	protected void loadPropertiesForProfile(String profile,
			String propertiesFileName) {
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
				getProfileProperties(profile).load(is);
			} finally {
				is.close();
			}
		} catch (IOException e) {
			throw new PropertiesUnreadableException(propertiesFileName);
		}
	}

	protected void loadPropertiesForProfiles() {
		final String profilePrefix = "profile.";
		final Set<String> profiles = getPropertyKeysWithPrefix(
				Profiles.DEFAULT_PROFILE, profilePrefix);
		for (String key : profiles) {
			String profileName = key.substring(profilePrefix.length());
			String profilePropertiesFileName = getProperty(
					Profiles.DEFAULT_PROFILE, key);
			properties.put(profileName, new Properties());
			loadPropertiesForProfile(profileName, profilePropertiesFileName);
		}
	}

	public static final String DEFAULT_PROPERTIES_FILE_NAME = "light-air.properties";
	private String propertiesFileName = DEFAULT_PROPERTIES_FILE_NAME;

	public void setPropertiesFileName(String propertiesFileName) {
		this.propertiesFileName = propertiesFileName;
	}

	/**
	 * Resolve properties file name.
	 * <p>
	 * If a system property of name <code>light.air.properties</code> is set,
	 * get the name of the properties file from this system property.
	 * <p>
	 * Otherwise, default to <code>light-air.properties</code>.
	 * 
	 * @return
	 */
	public String getPropertiesFileName() {
		final String propertiesOverride = System
				.getProperty(PROPERTIES_PROPERTY_NAME);
		if (null != propertiesOverride) {
			return propertiesOverride;
		}
		return propertiesFileName;
	}

	public static final String PROPERTIES_PROPERTY_NAME = "light.air.properties";

	protected Properties getProfileProperties(String profile) {
		final Properties profileProperties = properties.get(Profiles
				.getProfile(profile));
		if (null == profileProperties) {
			throw new ProfileNotDefinedException(profile);
		}
		return profileProperties;
	}

	/**
	 * Get mandatory property value from properties file.
	 * <p>
	 * Value is trimmed.
	 * 
	 * @param profile
	 *            Name of profile, null or empty string for default profile
	 * @param key
	 *            Property key
	 * @return Trimmed property value
	 * @throws MissingPropertyException
	 *             when no such property is defined.
	 */
	public String getProperty(String profile, String key) {
		String rawValue = getProfileProperties(profile).getProperty(key);
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
	 * @param profile
	 *            Name of profile, null or empty string for default profile
	 * @param key
	 *            Property key
	 * @param defaultValue
	 *            Default value to return when the property is not defined
	 * @return Property value converted to long or the default value when the
	 *         property is not defined
	 */
	public long getProperty(String profile, String key, long defaultValue) {
		String rawValue = getProfileProperties(profile).getProperty(key);
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
	 * @param profile
	 *            Name of profile, null or empty string for default profile
	 * @return The property names
	 */
	public Set<String> getDbUnitFeatureNames(String profile) {
		return getPropertyKeysWithPrefix(profile, "dbunit.features.");
	}

	/**
	 * Return all property names with prefix "dbunit.properties.".
	 * 
	 * @param profile
	 *            Name of profile, null or empty string for default profile
	 * @return The property names
	 */
	public Set<String> getDbUnitPropertyNames(String profile) {
		return getPropertyKeysWithPrefix(profile, "dbunit.properties.");
	}

	protected Set<String> getPropertyKeysWithPrefix(String profile,
			String prefix) {
		Set<String> names = new HashSet<String>();
		Enumeration<Object> keys = getProfileProperties(profile).keys();
		for (String key; keys.hasMoreElements();) {
			key = (String) keys.nextElement();
			if (key.startsWith(prefix)) {
				names.add(key);
			}
		}
		return names;
	}

	public Set<String> getProfileNames() {
		return properties.keySet();
	}

}
