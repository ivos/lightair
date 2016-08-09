package net.sf.lightair.internal.db;

import net.sf.lightair.internal.Keywords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Structure implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Structure.class);

	public static Map<String, Map<String, Map<String, Map<String, Object>>>> loadAll(
			Map<String, Map<String, String>> properties,
			Map<String, Connection> connections) {
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new LinkedHashMap<>();
		for (String profile : properties.keySet()) {
			structures.put(profile,
					loadProfile(profile, properties.get(profile), connections.get(profile)));
		}
		return Collections.unmodifiableMap(structures);
	}

	private static String convert(String name) {
		return name.toLowerCase();
	}

	private static Map<String, Map<String, Map<String, Object>>> loadProfile(
			String profile, Map<String, String> profileProperties, Connection connection) {
		String dialect = profileProperties.get(DATABASE_DIALECT);
		String schema = profileProperties.get(DATABASE_SCHEMA);

		Objects.requireNonNull(dialect, "Database dialect is required.");

		log.debug("Loading structure for profile [{}].", profile);

		Map<String, Map<String, Map<String, Object>>> data = new LinkedHashMap<>();
		List<String> tables = loadTables(connection, schema);
		for (String table : tables) {
			Map<String, Map<String, Object>> columns = loadTableColumns(connection, schema, table);
			data.put(convert(table), columns);
		}
		return Collections.unmodifiableMap(data);
	}

	private static List<String> loadTables(Connection connection, String schema) {
		log.debug("Loading structure for schema {}.", schema);
		List<String> data = new ArrayList<>();
		try {
			DatabaseMetaData meta = connection.getMetaData();
			try (ResultSet rs = meta.getTables(connection.getCatalog(), schema, null, null)) {
				while (rs.next()) {
					data.add(rs.getString(3));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Cannot load database metadata.", e);
		}
		return Collections.unmodifiableList(data);
	}

	private static Map<String, Map<String, Object>> loadTableColumns(
			Connection connection, String schema, String table) {
		log.debug("Loading structure for table {}/{}.", schema, table);
		Map<String, Map<String, Object>> data = new LinkedHashMap<>();
		try {
			DatabaseMetaData meta = connection.getMetaData();
			try (ResultSet rs = meta.getColumns(connection.getCatalog(), schema, table, null)) {
				while (rs.next()) {
					String columnName = convert(rs.getString(4));
					Map<String, Object> column = new LinkedHashMap<>();
					String dataType = resolveDataType(rs.getInt(5), rs.getString(6));
					column.put(DATA_TYPE, dataType);
					column.put(JDBC_DATA_TYPE, rs.getInt(5));
					column.put(NOT_NULL, 0 == rs.getInt(11));
					column.put(SIZE, rs.getInt(7));
					column.put(DECIMAL_DIGITS, rs.getInt(9));
					data.put(columnName, column);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Cannot load database metadata.", e);
		}
		return Collections.unmodifiableMap(data);
	}

	private static String resolveDataType(int sqlDataType, String sqlTypeName) {
		// generic:
		switch (sqlDataType) {
			case Types.BIT:
			case Types.BOOLEAN:
				return BOOLEAN;
			case Types.TINYINT:
				return BYTE;
			case Types.SMALLINT:
				return SHORT;
			case Types.INTEGER:
				return INTEGER;
			case Types.BIGINT:
				return LONG;
			case Types.REAL:
			case Types.FLOAT:
				return FLOAT;
			case Types.DOUBLE:
				return DOUBLE;
			case Types.NUMERIC:
			case Types.DECIMAL:
				return BIGDECIMAL;
			case Types.DATE:
				return DATE;
			case Types.TIME:
			case Types.TIME_WITH_TIMEZONE:
				return TIME;
			case Types.TIMESTAMP:
			case Types.TIMESTAMP_WITH_TIMEZONE:
				return TIMESTAMP;
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				return STRING;
			case Types.NCHAR:
			case Types.NVARCHAR:
			case Types.LONGNVARCHAR:
				return NSTRING;
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
				return BYTES;
			case Types.CLOB:
				return CLOB;
			case Types.NCLOB:
				return NCLOB;
			case Types.BLOB:
				return BLOB;
		}

		// Oracle:
		if (Types.OTHER == sqlDataType) {
			switch (sqlTypeName) {
				case "ROWID":
					return LONG;
				case "NCLOB":
					return NCLOB;
				case "NCHAR":
				case "NVARCHAR2":
					return NSTRING;
			}
		}
		if (101 == sqlDataType && "BINARY_DOUBLE".equals(sqlTypeName)) {
			return DOUBLE;
		}
		if (100 == sqlDataType && "BINARY_FLOAT".equals(sqlTypeName)) {
			return FLOAT;
		}

		// fallback to String:
		log.error("Unknown type {} (with name {}), trying to resolve it as STRING.", sqlDataType, sqlTypeName);
		return STRING;
	}
}
