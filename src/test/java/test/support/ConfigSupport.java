package test.support;

import net.sf.lightair.internal.Api;
import net.sf.lightair.internal.factory.Factory;
import net.sf.lightair.internal.properties.PropertiesProvider;

public class ConfigSupport {

	private static PropertiesProvider propertiesProvider;

	public static void init() {
		propertiesProvider = Factory.getInstance().getPropertiesProvider();
	}

	public static void replaceConfig(String dbName) {
		String propertiesFileName = "light-air-" + dbName + ".properties";
		propertiesProvider.setPropertiesFileName(propertiesFileName);
		Factory.getInstance().init();
		Api.initialize(propertiesFileName);
	}

	public static void restoreConfig() {
		propertiesProvider.setPropertiesFileName(PropertiesProvider.DEFAULT_PROPERTIES_FILE_NAME);
		Factory.getInstance().init();
		Api.initialize(PropertiesProvider.DEFAULT_PROPERTIES_FILE_NAME);
	}

}
