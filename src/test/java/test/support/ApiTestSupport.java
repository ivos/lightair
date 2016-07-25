package test.support;

import net.sf.lightair.internal.Api;
import net.sf.lightair.internal.auto.Index;

import java.io.File;

public class ApiTestSupport {

	public static void reInitialize() {
		new File("target/test-classes/" + Index.AUTO_INDEX_FILE).delete();
		Api.reInitialize();
	}
}
