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
			Map<String, Map<String, Object>> table, String name, String dataType, boolean notNull) {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("DATA_TYPE", dataType);
		data.put("NOT_NULL", notNull);
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
		addColumn(columns, "CA", STRING, false);
		addColumn(columns, "CB", STRING, false);
		addColumn(columns, "CC", STRING, false);
		addColumn(columns, "CD", STRING, false);
		profile.put("TA", columns);

		columns = new LinkedHashMap<>();
		addColumn(columns, "CC", STRING, false);
		addColumn(columns, "CD", STRING, false);
		addColumn(columns, "CE", STRING, false);
		addColumn(columns, "CF", STRING, false);
		profile.put("TB", columns);

		columns = new LinkedHashMap<>();
		addColumn(columns, "BOOLEAN_NULLABLE_TYPE", BOOLEAN, false);
		addColumn(columns, "BOOLEAN_TYPE", BOOLEAN, true);
		addColumn(columns, "BYTE_NULLABLE_TYPE", BYTE, false);
		addColumn(columns, "BYTE_TYPE", BYTE, true);
		addColumn(columns, "SHORT_NULLABLE_TYPE", SHORT, false);
		addColumn(columns, "SHORT_TYPE", SHORT, true);
		addColumn(columns, "INTEGER_NULLABLE_TYPE", INTEGER, false);
		addColumn(columns, "INTEGER_TYPE", INTEGER, true);
		addColumn(columns, "LONG_NULLABLE_TYPE", LONG, false);
		addColumn(columns, "LONG_TYPE", LONG, true);
		addColumn(columns, "FLOAT_NULLABLE_TYPE", FLOAT, false);
		addColumn(columns, "FLOAT_TYPE", FLOAT, true);
		addColumn(columns, "DOUBLE_NULLABLE_TYPE", DOUBLE, false);
		addColumn(columns, "DOUBLE_TYPE", DOUBLE, true);
		addColumn(columns, "BIGDECIMAL_NULLABLE_TYPE", BIGDECIMAL, false);
		addColumn(columns, "BIGDECIMAL_TYPE", BIGDECIMAL, true);
		addColumn(columns, "DATE_NULLABLE_TYPE", DATE, false);
		addColumn(columns, "DATE_TYPE", DATE, true);
		addColumn(columns, "TIME_NULLABLE_TYPE", TIME, false);
		addColumn(columns, "TIME_TYPE", TIME, true);
		addColumn(columns, "TIMESTAMP_NULLABLE_TYPE", TIMESTAMP, false);
		addColumn(columns, "TIMESTAMP_TYPE", TIMESTAMP, true);
		addColumn(columns, "STRING_NULLABLE_TYPE", STRING, false);
		addColumn(columns, "STRING_TYPE", STRING, true);
		addColumn(columns, "FIXED_STRING_NULLABLE_TYPE", FIXED_STRING, false);
		addColumn(columns, "FIXED_STRING_TYPE", FIXED_STRING, true);
		addColumn(columns, "NSTRING_NULLABLE_TYPE", NSTRING, false);
		addColumn(columns, "NSTRING_TYPE", NSTRING, true);
		addColumn(columns, "FIXED_NSTRING_NULLABLE_TYPE", FIXED_NSTRING, false);
		addColumn(columns, "FIXED_NSTRING_TYPE", FIXED_NSTRING, true);
		addColumn(columns, "BYTES_NULLABLE_TYPE", BYTES, false);
		addColumn(columns, "BYTES_TYPE", BYTES, true);
		addColumn(columns, "CLOB_NULLABLE_TYPE", CLOB, false);
		addColumn(columns, "CLOB_TYPE", CLOB, true);
		addColumn(columns, "NCLOB_NULLABLE_TYPE", NCLOB, false);
		addColumn(columns, "NCLOB_TYPE", NCLOB, true);
		addColumn(columns, "BLOB_NULLABLE_TYPE", BLOB, false);
		addColumn(columns, "BLOB_TYPE", BLOB, true);
		addColumn(columns, "UUID_NULLABLE_TYPE", UUID, false);
		addColumn(columns, "UUID_TYPE", UUID, true);
		addColumn(columns, "JSON_NULLABLE_TYPE", JSON, false);
		addColumn(columns, "JSON_TYPE", JSON, true);
		addColumn(columns, "JSONB_NULLABLE_TYPE", JSONB, false);
		addColumn(columns, "JSONB_TYPE", JSONB, true);
		addColumn(columns, "ARRAY_STRING_NULLABLE_TYPE", ARRAY_STRING, false);
		addColumn(columns, "ARRAY_STRING_TYPE", ARRAY_STRING, true);
		addColumn(columns, "ARRAY_INTEGER_NULLABLE_TYPE", ARRAY_INTEGER, false);
		addColumn(columns, "ARRAY_INTEGER_TYPE", ARRAY_INTEGER, true);
		addColumn(columns, "ARRAY_LONG_NULLABLE_TYPE", ARRAY_LONG, false);
		addColumn(columns, "ARRAY_LONG_TYPE", ARRAY_LONG, true);
		profile.put("DATA_TYPES", columns);

		structure.put(DEFAULT_PROFILE, profile);

		// other profile
		profile = new LinkedHashMap<>();

		columns = new LinkedHashMap<>();
		addColumn(columns, "CA", STRING, false);
		addColumn(columns, "CB", STRING, false);
		addColumn(columns, "CC", STRING, false);
		profile.put("TOTHER", columns);

		structure.put("profile1", profile);

		Map<String, Map<String, String>> properties = new HashMap<>();
		HashMap<String, String> defaultProfile = new HashMap<>();
		defaultProfile.put(XSD_DIRECTORY, GENERATED_DIR);
		properties.put(DEFAULT_PROFILE, defaultProfile);

		FileUtils.deleteQuietly(new File(GENERATED_DIR));

		Xsd.generate(properties, structure);

		performTest("../../../../../main/resources/light-air-types.xsd", "light-air-types.xsd");
		performTest("dataset.xsd", "dataset.xsd");
		performTest("dataset-profile1.xsd", "dataset-profile1.xsd");
	}
}
