package net.sf.lightair.internal.db;

import net.sf.lightair.internal.Keywords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toCollection;

public class CleanInsert implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Structure.class);

	public static List<Map<String, Object>> create(
			Map<String, String> profileProperties,
			Map<String, Map<String, Map<String, Object>>> profileStructure,
			List<Map<String, Object>> dataset) {
		String schema = profileProperties.get(DATABASE_SCHEMA);

		Objects.requireNonNull(schema, "Database schema is required.");

		List<Map<String, Object>> statements = new ArrayList<>();
		dataset.stream()
				.map(row -> row.get(TABLE))
				.distinct() // only delete each table once
				.collect(toCollection(ArrayDeque::new))
				.descendingIterator() // delete tables in reverse order
				.forEachRemaining(tableName -> {
					statements.add(createDeleteStatement(schema, (String) tableName));
				});
		for (Map<String, Object> row : dataset) {
			Map<String, Object> insert = createInsertStatement(schema, profileStructure, row);
			if (null != insert) {
				statements.add(insert);
			}
		}
		return Collections.unmodifiableList(statements);
	}

	private static String buildDeleteSql(String schema, String table) {
		return "delete from " + schema + "." + table;
	}

	private static String buildInsertSql(String schema, String tableName, Map<String, String> columns) {
		return "insert into " + schema + "." + tableName +
				"(" + String.join(",", columns.keySet()) + ")" +
				" values (" + String.join(",", Collections.nCopies(columns.size(), "?")) + ")";
	}

	private static Map<String, Object> createDeleteStatement(String schema, String tableName) {
		log.debug("Creating delete statement for {}.{}.", schema, tableName);

		Map<String, Object> statement = new LinkedHashMap<>();
		statement.put(SQL, buildDeleteSql(schema, tableName));
		statement.put(PARAMETERS, Collections.emptyList());
		return Collections.unmodifiableMap(statement);
	}

	private static Map<String, Object> createInsertStatement(
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
		statement.put(SQL, buildInsertSql(schema, tableName, columns));
		statement.put(PARAMETERS, createParameters(schema, tableName, profileStructure, columns));
		return Collections.unmodifiableMap(statement);
	}

	private static List<Map<String, Object>> createParameters(
			String schema, String tableName,
			Map<String, Map<String, Map<String, Object>>> profileStructure, Map<String, String> columns) {
		Map<String, Map<String, Object>> table = profileStructure.get(tableName);

		Objects.requireNonNull(table, "Table " + schema + "." + tableName +
				" not found in loaded database structure.");

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

		Objects.requireNonNull(column, "Column " + schema + "." + tableName + "." + columnName +
				" not found in loaded database structure.");

		log.debug("Creating insert parameter for {}.{}.{}.", schema, tableName, columnName);
		Map<String, Object> parameter = new LinkedHashMap<>();
		parameter.put(DATA_TYPE, column.get(DATA_TYPE));
		parameter.put(JDBC_DATA_TYPE, column.get(JDBC_DATA_TYPE));
		parameter.put(VALUE, columns.get(columnName));
		return Collections.unmodifiableMap(parameter);
	}
}
