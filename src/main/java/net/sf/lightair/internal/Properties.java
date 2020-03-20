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

public class Properties implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Properties.class);

	public static final String PROFILE_PREFIX = "profile.";

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
			InputStream is = new FileInputStream(file);
			try {
				java.util.Properties properties = new java.util.Properties();
				properties.load(is);
				@SuppressWarnings({"unchecked", "rawtypes"})
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

	public static long getLimit(Map<String, String> profileProperties) {
		String limit = profileProperties.get(TIME_DIFFERENCE_LIMIT_MILLIS);
		if (null == limit) {
			return 0;
		}
		return Long.valueOf(limit);
	}
}
