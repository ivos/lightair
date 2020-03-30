package net.sf.lightair.internal.db;

import net.sf.lightair.internal.Keywords;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * Execute <code>delete</code> and <code>insert</code> SQL statements.
 */
public class ExecuteUpdate implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(ExecuteUpdate.class);

	public static void run(Connection connection, List<Map<String, Object>> statements) {
		for (Map<String, Object> statement : statements) {
			String sql = (String) statement.get(SQL);
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> parameters = (List<Map<String, Object>>) statement.get(PARAMETERS);
			updateStatement(connection, sql, parameters);
		}
	}

	private static void updateStatement(Connection connection, String sql, List<Map<String, Object>> parameters) {
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			for (int index = 0; index < parameters.size(); index++) {
				Map<String, Object> parameter = parameters.get(index);
				String type = (String) parameter.get(DATA_TYPE);
				int sqlDataType = (int) parameter.get(JDBC_DATA_TYPE);
				setParameter(statement, index + 1, type, sqlDataType, parameter.get(VALUE));
			}
			int updateCount = statement.executeUpdate();
			log.debug("Updated {} rows executing sql: {}", updateCount, sql);
		} catch (SQLException e) {
			throw new RuntimeException("Error executing DB update.", e);
		}
	}

	private static void setParameter(
			PreparedStatement statement, int index, String type, int sqlDataType, Object value)
			throws SQLException {
		if (log.isTraceEnabled()) {
			if (null != value && value instanceof byte[]) {
				log.trace("Setting parameter {} with type {} to bytes {}.", index, type,
						Base64.encodeBase64String((byte[]) value));
			} else {
				log.trace("Setting parameter {} with type {} to value {}.", index, type, value);
			}
		}
		if (null == value) {
			statement.setNull(index, sqlDataType);
			return;
		}
		switch (type) {
			case BOOLEAN:
				statement.setBoolean(index, (boolean) value);
				return;
			case BYTE:
				statement.setByte(index, (byte) value);
				return;
			case SHORT:
				statement.setShort(index, (short) value);
				return;
			case INTEGER:
				statement.setInt(index, (int) value);
				return;
			case LONG:
				statement.setLong(index, (long) value);
				return;
			case FLOAT:
				statement.setFloat(index, (float) value);
				return;
			case DOUBLE:
				statement.setDouble(index, (double) value);
				return;
			case BIGDECIMAL:
				statement.setBigDecimal(index, (BigDecimal) value);
				return;
			case DATE:
				statement.setDate(index, (Date) value);
				return;
			case TIME:
				statement.setTime(index, (Time) value);
				return;
			case TIMESTAMP:
				statement.setTimestamp(index, (Timestamp) value);
				return;
			case STRING:
			case FIXED_STRING:
				statement.setString(index, (String) value);
				return;
			case NSTRING:
			case FIXED_NSTRING:
				statement.setNString(index, (String) value);
				return;
			case BYTES:
				statement.setBytes(index, (byte[]) value);
				return;
			case CLOB:
				statement.setClob(index, new StringReader((String) value));
				return;
			case NCLOB:
				statement.setNClob(index, new StringReader((String) value));
				return;
			case BLOB:
				statement.setBlob(index, new ByteArrayInputStream((byte[]) value));
				return;
			case UUID:
				statement.setObject(index, java.util.UUID.fromString((String) value));
				return;
			case JSON:
			case JSONB:
				statement.setObject(index, value, Types.OTHER);
				return;
		}
		log.error("Unknown type {}, trying to set it as STRING. Parameter index {} with value {}.", type, index, value);
		statement.setString(index, (String) value);
	}
}
