package net.sf.lightair.internal.functional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.lightair.exception.PropertiesNotFoundException;
import net.sf.lightair.exception.PropertiesUnreadableException;
import net.sf.lightair.internal.factory.Factory;
import net.sf.lightair.internal.properties.PropertiesProvider;
import net.sf.lightair.internal.util.Profiles;

public class FileSystemPropertiesProvider extends PropertiesProvider {

	private final File lightAirProperties;

	private final Logger log = LoggerFactory.getLogger(Factory.class);

	public FileSystemPropertiesProvider(File lightAirProperties) {
		this.lightAirProperties = lightAirProperties;
	}

	public void initFromFileSystem() {
		log.debug("Loading main Light Air properties file...");
		loadPropertiesForProfileFromFileSystem(Profiles.DEFAULT_PROFILE, lightAirProperties);

		log.debug("Loading profiles properties...");
		loadPropertiesForProfilesFromFileSystem(lightAirProperties.getAbsoluteFile().getParentFile());
	}

	private void loadPropertiesForProfileFromFileSystem(String profile, File propertiesFile) {
		try {
			log.debug("Loading properties file for profile [" + profile + "] from path "
					+ propertiesFile.getCanonicalPath());
			getProfileProperties(profile).load(new FileInputStream(propertiesFile));
		} catch (FileNotFoundException e) {
			throw new PropertiesNotFoundException(propertiesFile.getName());
		} catch (IOException e) {
			throw new PropertiesUnreadableException(propertiesFile.getName());
		}
	}

	private void loadPropertiesForProfilesFromFileSystem(File baseDir) {
		final String profilePrefix = "profile.";
		final Set<String> profiles = getPropertyKeysWithPrefix(Profiles.DEFAULT_PROFILE, profilePrefix);
		for (String key : profiles) {
			String profileName = key.substring(profilePrefix.length());
			String profilePropertiesFileName = getProperty(Profiles.DEFAULT_PROFILE, key);
			properties.put(profileName, new Properties());
			loadPropertiesForProfileFromFileSystem(profileName, new File(baseDir, profilePropertiesFileName));
		}
	}

}
