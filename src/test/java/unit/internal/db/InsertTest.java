package unit.internal.db;

import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.db.Insert;
import org.junit.Test;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InsertTest implements Keywords {

	public static Map<String, Object> createRow(String table, String... data) {
		assertTrue("Data in pairs", data.length % 2 == 0);
		Map<String, Object> row = new LinkedHashMap<>();
		row.put(TABLE, table);
		Map<String, String> columns = new LinkedHashMap<>();
		for (int i = 0; i < data.length; i = i + 2) {
			columns.put(data[i], data[i + 1]);
		}
		row.put(COLUMNS, columns);
		return Collections.unmodifiableMap(row);
	}

	public static Map<String, Map<String, Object>> createTableStructure(Object... data) {
		assertTrue("Data in triples", data.length % 3 == 0);
		Map<String, Map<String, Object>> table = new HashMap<>();
		for (int i = 0; i < data.length; i = i + 3) {
			Map<String, Object> column = new HashMap<>();
			Object dataType = data[i + 1];
			Object jdbcDataType = data[i + 2];
			column.put(DATA_TYPE, dataType);
			column.put(JDBC_DATA_TYPE, jdbcDataType);
			table.put((String) data[i], column);
		}
		return Collections.unmodifiableMap(table);
	}

	@Test
	public void test() {
		Map<String, String> profileProperties = new HashMap<>();
		profileProperties.put(DATABASE_SCHEMA, "s1");

		Map<String, Map<String, Map<String, Object>>> profileStructure = new HashMap<>();
		profileStructure.put("t1",
				createTableStructure(
						"ta", INTEGER, Types.INTEGER,
						"tb", STRING, Types.VARCHAR,
						"tc", STRING, Types.VARCHAR
				));
		profileStructure.put("t2",
				createTableStructure(
						"ta", INTEGER, Types.INTEGER,
						"tc", STRING, Types.VARCHAR,
						"td", STRING, Types.VARCHAR,
						"te", STRING, Types.VARCHAR,
						"tf", STRING, Types.VARCHAR
				));

		List<Map<String, Object>> dataset = Arrays.asList(
				createRow("t1", "ta", "1", "tb", "bv1", "tc", "cv1"),
				createRow("t2", "ta", "2", "tc", "cv2", "td", "dv2"),
				createRow("t2", "ta", "3", "td", "dv3", "te", "ce3", "tf", "cf3")
		);

		List<Map<String, Object>> data = Insert.create(profileProperties, profileStructure, dataset);

		String expected = "[" +
				"{SQL=insert into s1.t1(ta,tb,tc) values (?,?,?), PARAMETERS=[{DATA_TYPE=INTEGER, JDBC_DATA_TYPE=4, VALUE=1},\n" +
				" {DATA_TYPE=STRING, JDBC_DATA_TYPE=12, VALUE=bv1},\n" +
				" {DATA_TYPE=STRING, JDBC_DATA_TYPE=12, VALUE=cv1}]},\n" +
				" {SQL=insert into s1.t2(ta,tc,td) values (?,?,?), PARAMETERS=[{DATA_TYPE=INTEGER, JDBC_DATA_TYPE=4, VALUE=2},\n" +
				" {DATA_TYPE=STRING, JDBC_DATA_TYPE=12, VALUE=cv2},\n" +
				" {DATA_TYPE=STRING, JDBC_DATA_TYPE=12, VALUE=dv2}]},\n" +
				" {SQL=insert into s1.t2(ta,td,te,tf) values (?,?,?,?), PARAMETERS=[{DATA_TYPE=INTEGER, JDBC_DATA_TYPE=4, VALUE=3},\n" +
				" {DATA_TYPE=STRING, JDBC_DATA_TYPE=12, VALUE=dv3},\n" +
				" {DATA_TYPE=STRING, JDBC_DATA_TYPE=12, VALUE=ce3},\n" +
				" {DATA_TYPE=STRING, JDBC_DATA_TYPE=12, VALUE=cf3}]}]";
		assertEquals(expected, data.toString().replace("}, ", "},\n "));
	}
}
