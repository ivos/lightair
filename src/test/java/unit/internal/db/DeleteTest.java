package unit.internal.db;

import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.db.Delete;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DeleteTest implements Keywords {

	private Map<String, Object> createRow(String table) {
		Map<String, Object> row = new LinkedHashMap<>();
		row.put(TABLE, table);
		return row;
	}

	@Test
	public void test() {
		Map<String, String> profileProperties = new HashMap<>();
		profileProperties.put(DATABASE_SCHEMA, "s1");

		Map<String, Map<String, Map<String, Object>>> profileStructure = new HashMap<>();

		List<Map<String, Object>> dataset = Arrays.asList(
				createRow("t1"),
				createRow("t2"),
				createRow("t3"),
				createRow("t2")
		);
		List<Map<String, Object>> data = Delete.create(profileProperties, profileStructure, dataset);
		String expected = "[{SQL=delete from s1.t3, PARAMETERS=[]},\n" +
				" {SQL=delete from s1.t2, PARAMETERS=[]},\n" +
				" {SQL=delete from s1.t1, PARAMETERS=[]}]";
		assertEquals(expected, data.toString().replace("}, ", "},\n "));
	}
}
