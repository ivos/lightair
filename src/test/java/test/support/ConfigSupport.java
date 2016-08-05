package test.support;

import net.sf.lightair.internal.Api;
import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.auto.Index;

import java.io.File;

public class ConfigSupport implements Keywords {

	public static void replaceConfig(String dbName) {
		new File("target/test-classes/" + Index.AUTO_INDEX_FILE).delete();
		String propertiesFileName = "target/test-classes/light-air-" + dbName + ".properties";
		Api.initialize(propertiesFileName);
	}

	public static void restoreConfig() {
		new File("target/test-classes/" + Index.AUTO_INDEX_FILE).delete();
		Api.initialize(DEFAULT_PROPERTIES_FILE_NAME);
	}
}
