package test.support;

import net.sf.lightair.internal.factory.Factory;
import net.sf.lightair.internal.properties.PropertiesProvider;

public class ConfigSupport {

	private static PropertiesProvider propertiesProvider;

	public static void init() {
		propertiesProvider = Factory.getInstance().getPropertiesProvider();
	}

	public static void replaceConfig(String dbName) {
		propertiesProvider.setPropertiesFileName("light-air-" + dbName
				+ ".properties");
		Factory.getInstance().init();
	}

	public static void restoreConfig() {
		propertiesProvider
				.setPropertiesFileName(PropertiesProvider.DEFAULT_PROPERTIES_FILE_NAME);
		Factory.getInstance().init();
	}

}
