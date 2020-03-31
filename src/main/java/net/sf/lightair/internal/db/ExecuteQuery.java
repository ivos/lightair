package net.sf.lightair.internal.db;

import net.sf.lightair.internal.Keywords;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Execute <code>select</code> SQL statements, producing a dataset.
 */
public class ExecuteQuery implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(ExecuteQuery.class);

	public static Map<String, List<Map<String, Object>>> run(
			Connection connection, List<Map<String, Object>> statements) {
		Map<String, List<Map<String, Object>>> dataset = new LinkedHashMap<>();
		for (Map<String, Object> statement : statements) {
			String tableName = (String) statement.get(TABLE);
			String sql = (String) statement.get(SQL);
			@SuppressWarnings("unchecked")
			Map<String, Map<String, Object>> columns = (Map<String, Map<String, Object>>) statement.get(COLUMNS);
			dataset.put(tableName, queryStatement(connection, sql, columns));
		}
		return Collections.unmodifiableMap(dataset);
	}

	private static List<Map<String, Object>> queryStatement(
			Connection connection, String sql, Map<String, Map<String, Object>> columns) {
		List<Map<String, Object>> rows = new ArrayList<>();
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					rows.add(queryRow(columns, rs));
				}
			}
			log.debug("Loaded {} rows executing sql: {}", rows.size(), sql);
		} catch (SQLException e) {
			throw new RuntimeException("Error executing DB query.", e);
		}
		return Collections.unmodifiableList(rows);
	}

	private static Map<String, Object> queryRow(
			Map<String, Map<String, Object>> columns, ResultSet rs) throws SQLException {
		Map<String, Object> row = new LinkedHashMap<>();
		for (String columnName : columns.keySet()) {
			Map<String, Object> column = columns.get(columnName);
			String type = (String) column.get(DATA_TYPE);
			int sqlDataType = (int) column.get(JDBC_DATA_TYPE);
			Object value = getValue(rs, columnName, type, sqlDataType);
//			log.trace("Resolved value as type {}, value: [{}]", value == null ? null : value.getClass(), value);
			if (rs.wasNull()) {
				value = null;
			}
			row.put(columnName, value);
		}
		return Collections.unmodifiableMap(row);
	}

	private static Object getValue(ResultSet rs, String columnName, String type, int sqlDataType) throws SQLException {
		log.trace("Getting value for column {} with type {} ({}).", columnName, type, sqlDataType);
		switch (type) {
			case BOOLEAN:
				return rs.getBoolean(columnName);
			case BYTE:
				return rs.getByte(columnName);
			case SHORT:
				return rs.getShort(columnName);
			case INTEGER:
				return rs.getInt(columnName);
			case LONG:
				return rs.getLong(columnName);
			case FLOAT:
				return rs.getFloat(columnName);
			case DOUBLE:
				return rs.getDouble(columnName);
			case BIGDECIMAL:
				return rs.getBigDecimal(columnName);
			case DATE:
				return rs.getDate(columnName);
			case TIME:
				return rs.getTime(columnName);
			case TIMESTAMP:
				return rs.getTimestamp(columnName);
			case STRING:
			case FIXED_STRING:
			case UUID:
			case JSON:
			case JSONB:
				return rs.getString(columnName);
			case NSTRING:
			case FIXED_NSTRING:
				return rs.getNString(columnName);
			case BYTES:
				return rs.getBytes(columnName);
			case CLOB:
				return readClob(columnName, type, rs.getClob(columnName));
			case NCLOB:
				return readClob(columnName, type, rs.getNClob(columnName));
			case BLOB:
				return readBlob(columnName, type, rs.getBlob(columnName));
			case ARRAY_STRING:
				return readArray(rs, columnName, new String[0], String::valueOf);
			case ARRAY_INTEGER:
				return readArray(rs, columnName, new Integer[0],
						object -> object instanceof Integer ? object : Integer.parseInt(String.valueOf(object)));
			case ARRAY_LONG:
				return readArray(rs, columnName, new Long[0],
						object -> object instanceof Long ? object : Long.parseLong(String.valueOf(object)));
		}
		log.error("Unknown type {} on column {}, trying to get it as Object.", type, columnName);
		return rs.getObject(columnName);
	}

	private static String readClob(String columnName, String type, Clob clob) throws SQLException {
		if (null == clob) {
			return null;
		}
		Reader reader = clob.getCharacterStream();
		if (null == reader) {
			return null;
		}
		try {
			return IOUtils.toString(reader);
		} catch (IOException e) {
			throw new RuntimeException("Error reading " + type + " column " + columnName + " from database.", e);
		}
	}

	private static byte[] readBlob(String columnName, String type, Blob blob) throws SQLException {
		if (null == blob) {
			return null;
		}
		InputStream inputStream = blob.getBinaryStream();
		if (null == inputStream) {
			return null;
		}
		try {
			return IOUtils.toByteArray(inputStream);
		} catch (IOException e) {
			throw new RuntimeException("Error reading " + type + " column " + columnName + " from database.", e);
		}
	}

	private static Object[] readArray(
			ResultSet rs, String columnName, Object[] emptyValue, Function<Object, ?> valueConverter)
			throws SQLException {
		Array array = rs.getArray(columnName);
		if (array == null) {
			return null;
		}
		return Arrays.stream((Object[]) array.getArray())
				.map(object -> {
					if (object == null) {
						return null;
					}
					return valueConverter.apply(object);
				})
				.collect(Collectors.toList())
				.toArray(emptyValue);
	}
}
