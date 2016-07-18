package unit.internal.xml;

import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.Xml;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class XmlTest implements Keywords {

	private final String DIR = "target/test-classes/" + getClass().getPackage().getName().replace('.', '/') + '/';

	private void verifyRow(int i, List<Map<String, Object>> data, String table, String... values) {
		assertNotNull("Row exists " + i, data.get(i));
		assertEquals("Row table " + i, table, data.get(i).get(TABLE));
		Map columns = (Map) data.get(i).get(COLUMNS);
		for (int j = 0; j < values.length - 1; j = j + 2) {
			String column = values[j];
			assertTrue("Row " + i + " has column " + column, columns.containsKey(column));
			assertEquals("Row " + i + ", column " + column + " value", values[j + 1], columns.get(column));
		}
		assertEquals("Row " + i + ", column count", values.length / 2, columns.size());
	}

	@Test
	public void test() {
		Map<String, List<String>> fileNames = new LinkedHashMap<>();
		fileNames.put("profile1", Arrays.asList(DIR + "p1.xml"));
		fileNames.put("profile2", Arrays.asList(DIR + "p2-1.xml", DIR + "p2-2.xml", DIR + "p2-3.xml"));
		fileNames.put("profile3", Arrays.asList(DIR + "p3.xml"));

		Map<String, List<Map<String, Object>>> datasets = Xml.read(fileNames);

		List<Map<String, Object>> profileDataset;

		profileDataset = datasets.get("profile1");
		verifyRow(0, profileDataset, "p1ta", "p1ca", "p1cav1", "p1cb", "p1cbv1", "p1cc", "p1ccv1");
		assertEquals("No more rows", 1, profileDataset.size());

		profileDataset = datasets.get("profile2");
		verifyRow(0, profileDataset, "ta", "ca", "cav1", "cb", "cbv1", "cc", "ccv1");
		verifyRow(1, profileDataset, "ta", "cb", "cbv2", "cc", "ccv2", "cd", "cdv2");
		verifyRow(2, profileDataset, "tb", "cc", "ccv3", "cd", "cdv3", "ce", "cev3");
		verifyRow(3, profileDataset, "tb", "cd", "cdv4", "ce", "cev4", "cf", "cfv4");
		verifyRow(4, profileDataset, "tc", "ce", "cev5", "cf", "cfv5", "cg", "cgv5");
		assertEquals("No more rows", 5, profileDataset.size());

		profileDataset = datasets.get("profile3");
		verifyRow(0, profileDataset, "p3ta", "p3ca", "p3cav1", "p3cb", "p3cbv1", "p3cc", "p3ccv1");
		assertEquals("No more rows", 1, profileDataset.size());

		assertEquals("No more profiles", 3, datasets.size());
	}
}
