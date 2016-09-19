package net.sf.lightair.internal.db;

import net.sf.lightair.internal.Keywords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Select implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Select.class);

	public static List<Map<String, Object>> create(
			Map<String, String> profileProperties,
			Map<String, Map<String, Map<String, Object>>> profileStructure,
			List<Map<String, Object>> expectedDataset) {
		String schema = profileProperties.get(DATABASE_SCHEMA);

		Objects.requireNonNull(schema, "Database schema is required.");

		List<Map<String, Object>> statements = new ArrayList<>();
		expectedDataset.stream()
				.map(row -> (String) row.get(TABLE))
				.distinct() // only select from each table once
				.forEach(tableName ->
						statements.add(createStatement(profileStructure, schema, tableName)));
		return Collections.unmodifiableList(statements);
	}

	private static Map<String, Object> createStatement(
			Map<String, Map<String, Map<String, Object>>> profileStructure,
			String schema, String tableName) {
		Map<String, Map<String, Object>> columns = profileStructure.get(tableName);
		log.debug("Creating select statement for {}.{}.", schema, tableName);

		Map<String, Object> statement = new LinkedHashMap<>();
		statement.put(TABLE, tableName);
		statement.put(SQL, buildSql(schema, tableName, columns.keySet()));
		statement.put(COLUMNS, columns);
		return Collections.unmodifiableMap(statement);
	}

	private static String buildSql(String schema, String tableName, Set<String> columns) {
		return "select " + String.join(", ", columns) + " from " + schema + "." + tableName;
	}
}
