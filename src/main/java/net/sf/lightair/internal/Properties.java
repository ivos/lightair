package net.sf.lightair.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Load <code>light-air.properties</code> files.
 */
public class Properties implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Properties.class);

	public static final String PROFILE_PREFIX = "profile.";

	public static final Map<String, String> envOverrides = new LinkedHashMap<String, String>() {{
		put(ENV_DATABASE_DRIVER_CLASS_NAME, DATABASE_DRIVER_CLASS_NAME);
		put(ENV_DATABASE_CONNECTION_URL, DATABASE_CONNECTION_URL);
		put(ENV_DATABASE_USER_NAME, DATABASE_USER_NAME);
		put(ENV_DATABASE_PASSWORD, DATABASE_PASSWORD);
		put(ENV_DATABASE_SCHEMA, DATABASE_SCHEMA);
		put(ENV_TIME_DIFFERENCE_LIMIT_MILLIS, TIME_DIFFERENCE_LIMIT_MILLIS);
	}};

	public static Map<String, Map<String, String>> load(String fileName) {
		log.debug("Loading properties.");
		Map<String, Map<String, String>> properties = new LinkedHashMap<>();

		Map<String, String> defaultProperties = loadPropertiesForProfile(DEFAULT_PROFILE, null, fileName);
		properties.put(DEFAULT_PROFILE, defaultProperties);

		defaultProperties.keySet().stream()
				.filter(property -> property.startsWith(PROFILE_PREFIX))
				.forEach(property -> {
					String profile = property.substring(PROFILE_PREFIX.length());
					Map<String, String> profileProperties =
							loadPropertiesForProfile(profile, fileName, defaultProperties.get(property));
					properties.put(profile, profileProperties);
				});

		return Collections.unmodifiableMap(properties);
	}

	private static Map<String, String> loadPropertiesForProfile(
			String profile, String defaultProfileFileName, String fileName) {
		try {
			File baseDir = null;
			if (null != defaultProfileFileName) {
				baseDir = new File(defaultProfileFileName).getAbsoluteFile().getParentFile();
			}
			File file = new File(baseDir, fileName);
			if (!file.exists()) {
				throw new RuntimeException("Properties file not found: " + fileName +
						"\nShould be relative to: " + new File("").getAbsolutePath());
			}
			try (InputStream is = new FileInputStream(file)) {
				java.util.Properties properties = new java.util.Properties();
				properties.load(is);
				log.debug("For profile [{}] loaded properties file {}.", profile, fileName);
				@SuppressWarnings({"unchecked", "rawtypes"})
				Map<String, String> map = overrideProfileProperties(profile, (Map) properties);
				return Collections.unmodifiableMap(map);
			}
		} catch (IOException e) {
			throw new RuntimeException("Properties file unreadable: " + fileName);
		}
	}

	private static Map<String, String> overrideProfileProperties(String profile, Map<String, String> profileProperties) {
		Map<String, String> result = new LinkedHashMap<>(profileProperties);
		envOverrides.keySet().forEach(envName -> {
			overrideProperty(profile, result, envName, envOverrides.get(envName));
		});
		return Collections.unmodifiableMap(result);
	}

	private static void overrideProperty(
			String profile, Map<String, String> profileProperties, String envName, String propertyName) {
		String value = System.getenv(envName);
		if (value != null) {
			log.debug("For profile [{}] overriding property \"{}\" using environment variable {}" +
							" from value [{}] to value [{}].",
					profile, propertyName, envName, profileProperties.get(propertyName), value);
			profileProperties.put(propertyName, value);
		}
	}

	public static long getLimit(Map<String, String> profileProperties) {
		String limit = profileProperties.get(TIME_DIFFERENCE_LIMIT_MILLIS);
		if (null == limit) {
			return 0;
		}
		return Long.parseLong(limit);
	}
}
