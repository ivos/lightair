package unit.internal.db;

import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.db.Select;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SelectTest implements Keywords {

	public static Map<String, Object> createRow(String table, String... columnNames) {
		Map<String, Object> row = new LinkedHashMap<>();
		row.put(TABLE, table);
		Map<String, String> columns = new LinkedHashMap<>();
		for (String columnName : columnNames) {
			columns.put(columnName, "value1");
		}
		row.put(COLUMNS, columns);
		return row;
	}

	public static Map<String, Map<String, Object>> createTableStructure(String... columnNames) {
		Map<String, Map<String, Object>> table = new LinkedHashMap<>();
		for (String columnName : columnNames) {
			table.put(columnName, null);
		}
		return table;
	}

	@Test
	public void test() {
		Map<String, String> profileProperties = new HashMap<>();
		profileProperties.put(DATABASE_SCHEMA, "s1");

		Map<String, Map<String, Map<String, Object>>> profileStructure = new HashMap<>();
		profileStructure.put("t1", createTableStructure("t1a", "t1b", "t1c"));
		profileStructure.put("t2", createTableStructure("t2a", "t2b", "t2c", "t2d"));
		profileStructure.put("t3", createTableStructure("t3a", "t3b", "t3c"));
		profileStructure.put("t4", createTableStructure("t4a"));
		profileStructure.put("t5", createTableStructure("t5a"));

		List<Map<String, Object>> dataset = Arrays.asList(
				createRow("t1", "c1", "c2", "c3"),
				createRow("t2"),
				createRow("t5"),
				createRow("t3"),
				createRow("t2")
		);

		List<String> data = Select.create(profileProperties, profileStructure, dataset);

		String expected = "[select t1a,t1b,t1c from s1.t1,\n" +
				" select t2a,t2b,t2c,t2d from s1.t2,\n" +
				" select t5a from s1.t5,\n" +
				" select t3a,t3b,t3c from s1.t3]";
		assertEquals(expected, data.toString().replace(", select ", ",\n select "));
	}
}
