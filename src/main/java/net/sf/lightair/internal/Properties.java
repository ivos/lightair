package net.sf.lightair.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Properties implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Properties.class);

	public static final String PROFILE_PREFIX = "profile.";

	public static Map<String, Map<String, String>> load(String fileName) {
		log.debug("Loading properties.");
		Map<String, Map<String, String>> properties = new LinkedHashMap<>();

		Map<String, String> defaultProperties = loadPropertiesForProfile(DEFAULT_PROFILE, fileName);
		properties.put(DEFAULT_PROFILE, defaultProperties);

		defaultProperties.keySet().stream()
				.filter(property -> property.startsWith(PROFILE_PREFIX))
				.forEach(property -> {
					String profile = property.substring(PROFILE_PREFIX.length());
					Map<String, String> profileProperties =
							loadPropertiesForProfile(profile, defaultProperties.get(property));
					properties.put(profile, profileProperties);
				});

		return Collections.unmodifiableMap(properties);
	}

	private static Map<String, String> loadPropertiesForProfile(String profile, String fileName) {
		try {
			URL resource = Properties.class.getClassLoader().getResource(fileName);
			if (null == resource) {
				throw new RuntimeException("Properties not found: " + fileName);
			}
			URLConnection urlConnection = resource.openConnection();
			urlConnection.setUseCaches(false);
			InputStream is = urlConnection.getInputStream();
			try {
				java.util.Properties properties = new java.util.Properties();
				properties.load(is);
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map) properties;
				log.debug("For profile [{}] loaded properties file {}.", profile, fileName);
				return Collections.unmodifiableMap(map);
			} finally {
				is.close();
			}
		} catch (IOException e) {
			throw new RuntimeException("Properties file unreadable: " + fileName);
		}
	}
}
