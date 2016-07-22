package unit.internal.auto;

import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.auto.Index;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class IndexTest implements Keywords {

	private static final String DIR = "target/test-classes";
	private static final String FILE = DIR + "/" + Index.AUTO_INDEX_FILE;

	private static Map<String, Map<String, String>> createProperties() {
		Map<String, Map<String, String>> properties = new HashMap<>();
		Map<String, String> defaultProperties = new HashMap<>();
		defaultProperties.put(AUTO_INDEX_DIRECTORY, DIR);
		properties.put(DEFAULT_PROFILE, defaultProperties);
		return properties;
	}

	public static Map<String, Map<String, Object>> createTableStructure(String... columnNames) {
		Map<String, Map<String, Object>> table = new LinkedHashMap<>();
		for (String columnName : columnNames) {
			table.put(columnName, null);
		}
		return table;
	}

	private static Map<String, Map<String, Map<String, Map<String, Object>>>> createStructures() {
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new LinkedHashMap<>();
		Map<String, Map<String, Map<String, Object>>> profileStructure;
		// profile 1 structure
		profileStructure = new LinkedHashMap<>();
		profileStructure.put("t11", createTableStructure("t11a", "t11b"));
		profileStructure.put("t12", createTableStructure("t12a", "t12b"));
		structures.put("profile1", profileStructure);
		// profile 2 structure
		profileStructure = new LinkedHashMap<>();
		profileStructure.put("t21", createTableStructure("t21a", "t21b"));
		profileStructure.put("t22", createTableStructure("t22a", "t22b"));
		structures.put("profile2", profileStructure);
		return structures;
	}

	@Test
	public void createFile() throws IOException {
		new File(FILE).delete();

		Map<String, String> index = Index.readAndUpdate(createProperties(), createStructures());

		String expected = "{[profile1]/t11=2078,\n" +
				" [profile1]/t11.t11a=169,\n" +
				" [profile1]/t11.t11b=985,\n" +
				" [profile1]/t12=4522,\n" +
				" [profile1]/t12.t12a=494,\n" +
				" [profile1]/t12.t12b=565,\n" +
				" [profile2]/t21=6577,\n" +
				" [profile2]/t21.t21a=894,\n" +
				" [profile2]/t21.t21b=958,\n" +
				" [profile2]/t22=6760,\n" +
				" [profile2]/t22.t22a=809,\n" +
				" [profile2]/t22.t22b=729}";
		assertEquals("Index", expected, index.toString().replace(", ", ",\n "));
		assertEquals("File",
				expected.replace("{", "")
						.replace(",\n ", "\n")
						.replace("}", "\n"),
				FileUtils.readFileToString(new File(FILE)));
	}

	@Test
	public void updateFile() throws IOException {
		String existing = "[profile1]/t12=4522\n" +
				"[profile1]/t12.t12a=494\n" +
				"[profile2]/t21=6577\n" +
				"[profile2]/t21.t21b=958\n";
		FileUtils.writeStringToFile(new File(FILE), existing, StandardCharsets.ISO_8859_1);

		Map<String, String> index = Index.readAndUpdate(createProperties(), createStructures());

		String expected = "{[profile1]/t12=4522,\n" +
				" [profile1]/t12.t12a=494,\n" +
				" [profile2]/t21=6577,\n" +
				" [profile2]/t21.t21b=958,\n" +
				" [profile1]/t11=2078,\n" +
				" [profile1]/t11.t11a=169,\n" +
				" [profile1]/t11.t11b=985,\n" +
				" [profile1]/t12.t12b=565,\n" +
				" [profile2]/t21.t21a=894,\n" +
				" [profile2]/t22=6760,\n" +
				" [profile2]/t22.t22a=809,\n" +
				" [profile2]/t22.t22b=729}";
		assertEquals("Index", expected, index.toString().replace(", ", ",\n "));
		assertEquals("File",
				expected.replace("{", "")
						.replace(",\n ", "\n")
						.replace("}", "\n"),
				FileUtils.readFileToString(new File(FILE)));
	}

	@Test
	public void tableOverflow() {
		new File(FILE).delete();

		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new HashMap<>();
		Map<String, Map<String, Map<String, Object>>> profileStructure = new LinkedHashMap<>();
		for (int i = 0; i <= 10_000; i++) {
			profileStructure.put("t" + i, null);
		}
		structures.put("profile1", profileStructure);

		try {
			Index.readAndUpdate(createProperties(), structures);
		} catch (Exception e) {
			String expected = "Profile [profile1] contains more than 10.000 tables," +
					" which is the maximum allowed limit (required by @auto functionality).";
			assertEquals(expected, e.getMessage());
		}
	}

	@Test
	public void columnOverflow() {
		new File(FILE).delete();

		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new HashMap<>();
		Map<String, Map<String, Map<String, Object>>> profileStructure = new LinkedHashMap<>();
		Map<String, Map<String, Object>> table = new LinkedHashMap<>();
		for (int i = 0; i <= 1_000; i++) {
			table.put("c" + i, null);
		}
		profileStructure.put("t1", table);
		structures.put("profile1", profileStructure);

		try {
			Index.readAndUpdate(createProperties(), structures);
		} catch (Exception e) {
			String expected = "Table [profile1]/t1 contains more than 1.000 columns," +
					" which is the maximum allowed limit (required by @auto functionality).";
			assertEquals(expected, e.getMessage());
		}
	}
}
