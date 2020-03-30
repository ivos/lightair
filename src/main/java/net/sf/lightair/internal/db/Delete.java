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

/**
 * Create <code>delete</code> SQL statements from a dataset.
 */
public class Delete implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Delete.class);

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
					statements.add(createStatement(schema, (String) tableName));
				});
		return Collections.unmodifiableList(statements);
	}

	private static String buildSql(String schema, String table) {
		return "delete from " + schema + "." + table;
	}

	private static Map<String, Object> createStatement(String schema, String tableName) {
		log.debug("Creating delete statement for {}.{}.", schema, tableName);

		Map<String, Object> statement = new LinkedHashMap<>();
		statement.put(SQL, buildSql(schema, tableName));
		statement.put(PARAMETERS, Collections.emptyList());
		return Collections.unmodifiableMap(statement);
	}
}
