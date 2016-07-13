package unit.internal.xsd;

import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.Xsd;
import net.sf.seaf.test.util.TemplatingTestBase;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class XsdTest extends TemplatingTestBase implements Keywords {

	private static boolean replaceTemplates = false;

	private static final String GENERATED_DIR = "target/generated-xsd/light-air-xsd/";

	public XsdTest() {
		super(replaceTemplates, "src/test/java/unit/internal/xsd/", GENERATED_DIR);
	}

	private static void addColumn(
			Map<String, Map<String, Object>> table, String name, String dataType, boolean notNull, int size) {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("DATA_TYPE", dataType);
		data.put("NOT_NULL", Boolean.toString(notNull));
		data.put("SIZE", Integer.toString(size));
		table.put(name, data);
	}

	@Test
	public void test() throws IOException {
		Map<String, Map<String, Map<String, Map<String, Object>>>> structure = new LinkedHashMap<>();
		Map<String, Map<String, Map<String, Object>>> profile;
		Map<String, Map<String, Object>> columns;

		// default profile
		profile = new LinkedHashMap<>();

		columns = new LinkedHashMap<>();
		addColumn(columns, "CA", "STRING", false, 10);
		addColumn(columns, "CB", "STRING", false, 20);
		addColumn(columns, "CC", "STRING", false, 30);
		addColumn(columns, "CD", "STRING", false, 40);
		profile.put("TA", columns);

		columns = new LinkedHashMap<>();
		addColumn(columns, "CC", "STRING", false, 10);
		addColumn(columns, "CD", "STRING", false, 20);
		addColumn(columns, "CE", "STRING", false, 30);
		addColumn(columns, "CF", "STRING", false, 40);
		profile.put("TB", columns);

		structure.put(DEFAULT_PROFILE, profile);

		// other profile
		profile = new LinkedHashMap<>();

		columns = new LinkedHashMap<>();
		addColumn(columns, "CA", "STRING", false, 10);
		addColumn(columns, "CB", "STRING", false, 20);
		addColumn(columns, "CC", "STRING", false, 30);
		profile.put("TOTHER", columns);

		structure.put("profile1", profile);

		Map<String, Map<String, String>> properties = new HashMap<>();
		HashMap<String, String> defaultProfile = new HashMap<>();
		defaultProfile.put(XSD_DIRECTORY, GENERATED_DIR);
		properties.put(DEFAULT_PROFILE, defaultProfile);

		FileUtils.deleteQuietly(new File(GENERATED_DIR));

		Xsd.generate(properties, structure);

		performTest("dataset.xsd", "dataset.xsd");
		performTest("dataset-profile1.xsd", "dataset-profile1.xsd");
	}
}
