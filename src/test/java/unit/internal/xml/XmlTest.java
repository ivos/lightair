package unit.internal.xml;

import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.Xml;
import org.junit.Test;

import java.io.File;
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
		List<Map<String, Object>> data = Xml.readFile(new File(DIR + "test.xml"));
		verifyRow(0, data, "ta", "ca", "cav1", "cb", "cbv1", "cc", "ccv1");
		verifyRow(1, data, "ta", "cb", "cbv2", "cc", "ccv2", "cd", "cdv2");
		verifyRow(2, data, "tb", "cc", "ccv3", "cd", "cdv3", "ce", "cev3");
		assertEquals("No more rows", 3, data.size());
	}
}
