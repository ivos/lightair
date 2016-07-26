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
			Map<String, Object> column = new LinkedHashMap<>();
			column.put(DATA_TYPE, STRING);
			table.put(columnName, column);
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

		List<Map<String, Object>> data = Select.create(profileProperties, profileStructure, dataset);

		String expected = "[{TABLE=t1,\n" +
				"  SQL=select t1a, t1b, t1c from s1.t1,\n" +
				"  COLUMNS={t1a={DATA_TYPE=STRING}, t1b={DATA_TYPE=STRING}, t1c={DATA_TYPE=STRING}}},\n" +
				" {TABLE=t2,\n" +
				"  SQL=select t2a, t2b, t2c, t2d from s1.t2,\n" +
				"  COLUMNS={t2a={DATA_TYPE=STRING}, t2b={DATA_TYPE=STRING}, t2c={DATA_TYPE=STRING}, t2d={DATA_TYPE=STRING}}},\n" +
				" {TABLE=t5,\n" +
				"  SQL=select t5a from s1.t5,\n" +
				"  COLUMNS={t5a={DATA_TYPE=STRING}}},\n" +
				" {TABLE=t3,\n" +
				"  SQL=select t3a, t3b, t3c from s1.t3,\n" +
				"  COLUMNS={t3a={DATA_TYPE=STRING}, t3b={DATA_TYPE=STRING}, t3c={DATA_TYPE=STRING}}}]";
		assertEquals(expected, data.toString()
				.replace(" {TABLE=", "\n {TABLE=")
				.replace(", SQL=", ",\n  SQL=")
				.replace(", COLUMNS=", ",\n  COLUMNS="));
	}
}
