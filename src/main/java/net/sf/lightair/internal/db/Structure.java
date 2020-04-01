package net.sf.lightair.internal.db;

import net.sf.lightair.internal.Keywords;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Load database structure.
 */
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
		String schema = profileProperties.get(DATABASE_SCHEMA);

		log.debug("Loading structure for profile [{}].", profile);
		Map<String, Map<String, Map<String, Object>>> data = new LinkedHashMap<>();
		try {
			DatabaseMetaData meta = connection.getMetaData();
			try (ResultSet rs = meta.getColumns(connection.getCatalog(), schema, null, null)) {
				String currentTableName = null;
				Map<String, Map<String, Object>> table = null;
				while (rs.next()) {
					String tableName = convert(rs.getString(3));
					if (!tableName.equals(currentTableName)) {
						if (null != table) {
							data.put(currentTableName, table);
						}
						table = new LinkedHashMap<>();
						currentTableName = tableName;
						log.debug("Loading table {} in profile [{}].", tableName, profile);
					}
					String columnName = convert(rs.getString(4));
					Map<String, Object> column = createColumn(rs);
					log.trace("Resolved column [{}].[{}] as: {}", tableName, columnName, column);
					table.put(columnName, column);
				}
				if (null != currentTableName) {
					data.put(currentTableName, table);
				}
			}
			log.debug("Loaded {} tables in profile [{}].", data.size(), profile);
			if (0 == data.size()) {
				log.warn("NO TABLES LOADED in profile [{}]! URL: [{}], user name [{}], schema [{}].",
						profile, meta.getURL(), meta.getUserName(), schema);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Cannot load database metadata.", e);
		}
		return Collections.unmodifiableMap(data);
	}

	private static Map<String, Object> createColumn(ResultSet rs) throws SQLException {
		Map<String, Object> column = new LinkedHashMap<>();

		int sqlDataType = rs.getInt(5);
		int size = rs.getInt(7);
		String sqlTypeName = StringUtils.upperCase(rs.getString(6));

//		log.trace("Resolving, DATA_TYPE: {}, TYPE_NAME: [{}], SIZE: [{}], COLUMN_DEF: [{}],"
//						+ " SQL_DATA_TYPE: [{}], SOURCE_DATA_TYPE: [{}]",
//				sqlDataType, // DATA_TYPE
//				sqlTypeName, // TYPE_NAME
//				size, // SIZE
//				rs.getString(13), // COLUMN_DEF
//				rs.getString(14), // SQL_DATA_TYPE
//				rs.getString(22) // SOURCE_DATA_TYPE
//		);

		String dataType = resolveDataType(sqlDataType, sqlTypeName);
		column.put(DATA_TYPE, dataType);
		column.put(JDBC_DATA_TYPE, sqlDataType);
		column.put(NOT_NULL, 0 == rs.getInt(11));
		column.put(SIZE, size);
		column.put(DECIMAL_DIGITS, rs.getInt(9));
		return Collections.unmodifiableMap(column);
	}

	private static String resolveDataType(int sqlDataType, String sqlTypeName) {
		if ("UUID".equals(sqlTypeName)) {
			return UUID; // Postgres, H2, HSQL
		}
		if ("JSON".equals(sqlTypeName)) {
			return JSON; // Postgres
		}
		if ("JSONB".equals(sqlTypeName)) {
			return JSONB; // Postgres
		}

		if (Types.TIMESTAMP == sqlDataType && "DATE".equals(sqlTypeName)) {
			return DATE; // Oracle
		}

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
				return FIXED_STRING;
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				return STRING;
			case Types.NCHAR:
				return FIXED_NSTRING;
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

		if (Types.ARRAY == sqlDataType) {
			switch (sqlTypeName) {
				case "ARRAY": // H2
				case "_TEXT": // Postgres
				case "_VARCHAR": // Postgres
					return ARRAY_STRING;
				case "_INT4": // Postgres
					return ARRAY_INTEGER;
				case "_INT8": // Postgres
					return ARRAY_LONG;
			}
		}
		if (Types.OTHER == sqlDataType) {
			switch (sqlTypeName) {
				case "ROWID": // Oracle
					return LONG;
				case "NCLOB": // Oracle
					return NCLOB;
				case "NCHAR": // Oracle
				case "NVARCHAR2": // Oracle
					return NSTRING;
			}
		}
		if (101 == sqlDataType && "BINARY_DOUBLE".equals(sqlTypeName)) { // Oracle
			return DOUBLE;
		}
		if (100 == sqlDataType && "BINARY_FLOAT".equals(sqlTypeName)) { // Oracle
			return FLOAT;
		}

		// fallback to String:
		log.error("Unknown type {} (with name {}), trying to resolve it as STRING.", sqlDataType, sqlTypeName);
		return STRING;
	}
}
