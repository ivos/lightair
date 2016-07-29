package net.sf.lightair.internal.db;

import net.sf.lightair.internal.Keywords;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				rows.add(queryRow(columns, rs));
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
			row.put(columnName, value);
		}
		return Collections.unmodifiableMap(row);
	}

	private static Object getValue(ResultSet rs, String columnName, String type, int sqlDataType)
			throws SQLException {
		log.trace("Getting value for column {} with type {}.", columnName, type);
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
				return rs.getString(columnName);
			case NSTRING:
				return rs.getNString(columnName);
			case BYTES:
				return rs.getBytes(columnName);
			case CLOB:
				try {
					return IOUtils.toString(rs.getClob(columnName).getCharacterStream());
				} catch (IOException e) {
					throw new RuntimeException("Error reading CLOB column " + columnName + " from database.", e);
				}
			case NCLOB:
				try {
					return IOUtils.toString(rs.getNClob(columnName).getCharacterStream());
				} catch (IOException e) {
					throw new RuntimeException("Error reading NCLOB column " + columnName + " from database.", e);
				}
			case BLOB:
				try {
					return IOUtils.toByteArray(rs.getBlob(columnName).getBinaryStream());
				} catch (IOException e) {
					throw new RuntimeException("Error reading BLOB column " + columnName + " from database.", e);
				}
		}
		log.error("Unknown type {} on column {}, trying to get it as Object.", type, columnName);
		return rs.getObject(columnName);
	}
}
