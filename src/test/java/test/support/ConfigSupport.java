package test.support;

import net.sf.lightair.internal.Api;
import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.auto.Index;
import net.sf.lightair.internal.factory.Factory;
import net.sf.lightair.internal.properties.PropertiesProvider;

import java.io.File;

public class ConfigSupport implements Keywords {

	private static PropertiesProvider propertiesProvider;

	public static void init() {
		propertiesProvider = Factory.getInstance().getPropertiesProvider();
	}

	public static void replaceConfig(String dbName) {
		String propertiesFileName = "target/test-classes/light-air-" + dbName + ".properties";
		propertiesProvider.setPropertiesFileName(propertiesFileName);
		new File("target/test-classes/" + Index.AUTO_INDEX_FILE).delete();
		Api.initialize(propertiesFileName);
	}

	public static void restoreConfig() {
		propertiesProvider.setPropertiesFileName(PropertiesProvider.DEFAULT_PROPERTIES_FILE_NAME);
		new File("target/test-classes/" + Index.AUTO_INDEX_FILE).delete();
		Api.initialize(DEFAULT_PROPERTIES_FILE_NAME);
	}

}
