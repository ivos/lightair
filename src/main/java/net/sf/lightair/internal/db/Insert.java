package net.sf.lightair.internal.db;

import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.auto.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Insert implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Insert.class);

	public static List<Map<String, Object>> create(
			Map<String, String> profileProperties,
			Map<String, Map<String, Map<String, Object>>> profileStructure,
			List<Map<String, Object>> dataset) {
		String schema = profileProperties.get(DATABASE_SCHEMA);

		Objects.requireNonNull(schema, "Database schema is required.");

		List<Map<String, Object>> statements = new ArrayList<>();
		for (Map<String, Object> row : dataset) {
			Map<String, Object> insert = createStatement(schema, profileStructure, row);
			if (null != insert) {
				statements.add(insert);
			}
		}
		return Collections.unmodifiableList(statements);
	}

	private static String buildSql(String schema, String tableName, Map<String, String> columns) {
		return "insert into " + schema + "." + tableName +
				"(" + String.join(",", columns.keySet()) + ")" +
				" values (" + String.join(",", Collections.nCopies(columns.size(), "?")) + ")";
	}

	private static Map<String, Object> createStatement(
			String schema,
			Map<String, Map<String, Map<String, Object>>> profileStructure,
			Map<String, Object> row) {
		@SuppressWarnings("unchecked")
		Map<String, String> columns = (Map<String, String>) row.get(COLUMNS);
		if (columns.isEmpty()) {
			return null;
		}

		String tableName = (String) row.get(TABLE);
		log.debug("Creating insert statement for {}.{}.", schema, tableName);

		Map<String, Object> statement = new LinkedHashMap<>();
		statement.put(SQL, buildSql(schema, tableName, columns));
		statement.put(PARAMETERS, createParameters(schema, tableName, profileStructure, columns));
		return Collections.unmodifiableMap(statement);
	}

	private static List<Map<String, Object>> createParameters(
			String schema, String tableName,
			Map<String, Map<String, Map<String, Object>>> profileStructure, Map<String, String> columns) {
		Map<String, Map<String, Object>> table = profileStructure.get(tableName);

		if (null == table) {
			throw new NullPointerException("Table " + Index.formatTableKey(schema, tableName) +
					" not found in loaded database structure.");
		}

		List<Map<String, Object>> parameters = new ArrayList<>();
		for (String columnName : columns.keySet()) {
			parameters.add(createParameter(schema, tableName, table, columns, columnName));
		}
		return Collections.unmodifiableList(parameters);
	}

	private static Map<String, Object> createParameter(
			String schema, String tableName,
			Map<String, Map<String, Object>> table, Map<String, String> columns, String columnName) {
		Map<String, Object> column = table.get(columnName);

		if (null == column) {
			throw new NullPointerException("Column " + Index.formatColumnKey(schema, tableName, columnName) +
					" not found in loaded database structure.");
		}

		log.debug("Creating insert parameter for {}.{}.{}.", schema, tableName, columnName);
		Map<String, Object> parameter = new LinkedHashMap<>();
		parameter.put(DATA_TYPE, column.get(DATA_TYPE));
		parameter.put(JDBC_DATA_TYPE, column.get(JDBC_DATA_TYPE));
		parameter.put(VALUE, columns.get(columnName));
		return Collections.unmodifiableMap(parameter);
	}
}
