package net.sf.lightair.internal.db;

import net.sf.lightair.internal.Keywords;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Converter implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Converter.class);

	private static final String NULL_TOKEN = "@null";
	private static final String DATE_TOKEN = "@date";
	private static final String TIME_TOKEN = "@time";
	private static final String TIMESTAMP_TOKEN = "@timestamp";

	public static Map<String, List<Map<String, Object>>> convert(
			Map<String, Map<String, Map<String, Map<String, Object>>>> structures,
			Map<String, List<Map<String, Object>>> datasets) {
		Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
		for (String profile : datasets.keySet()) {
			log.debug("Converting values for profile [{}].", profile);
			Map<String, Map<String, Map<String, Object>>> profileStructure = structures.get(profile);
			Objects.requireNonNull(profileStructure, "Structure for profile [" + profile + "] is missing.");
			result.put(profile, convertDataset(profile, profileStructure, datasets));
		}
		return Collections.unmodifiableMap(result);
	}

	private static List<Map<String, Object>> convertDataset(
			String profile, Map<String, Map<String, Map<String, Object>>> profileStructure,
			Map<String, List<Map<String, Object>>> datasets) {
		List<Map<String, Object>> dataset = datasets.get(profile);
		List<Map<String, Object>> convertedDataset = new ArrayList<>();
		for (Map<String, Object> row : dataset) {
			String tableName = (String) row.get(TABLE);
			@SuppressWarnings("unchecked")
			Map<String, String> columns = (Map) row.get(COLUMNS);
			Map<String, Object> convertedRow = new LinkedHashMap<>();
			convertedRow.put(TABLE, tableName);
			convertedRow.put(COLUMNS, convertColumns(profile, profileStructure, tableName, columns));
			convertedDataset.add(Collections.unmodifiableMap(convertedRow));
		}
		return Collections.unmodifiableList(convertedDataset);
	}

	private static Map<String, Object> convertColumns(
			String profile, Map<String, Map<String, Map<String, Object>>> profileStructure,
			String tableName, Map<String, String> columns) {
		Map<String, Map<String, Object>> table = profileStructure.get(tableName);
		Objects.requireNonNull(table, "Structure for table [" + profile + "]." + tableName + " is missing.");
		Map<String, Object> convertedColumns = new LinkedHashMap<>();
		for (String columnName : columns.keySet()) {
			Map<String, Object> column = table.get(columnName);
			Objects.requireNonNull(column,
					"Structure for column [" + profile + "]." + tableName + "." + columnName + " is missing.");
			Object convertedValue = convertValue(profile, tableName, columnName,
					(String) column.get(DATA_TYPE), (Integer) column.get(JDBC_DATA_TYPE),
					columns.get(columnName));
			convertedColumns.put(columnName, convertedValue);
		}
		return Collections.unmodifiableMap(convertedColumns);
	}

	private static Object convertValue(
			String profile, String tableName, String columnName, String dataType, int jdbcDataType, String value) {
		Object result;
		if (NULL_TOKEN.equals(value)) {
			result = null;
		} else if (DATE_TOKEN.equals(value)) {
			result = getTokenDate(dataType);
		} else if (TIME_TOKEN.equals(value)) {
			result = getTokenTime(dataType);
		} else if (TIMESTAMP_TOKEN.equals(value)) {
			result = getTokenTimestamp(dataType);
		} else {
			result = convertDataType(profile, tableName, columnName, dataType, jdbcDataType, value);
		}
		log.trace("Converted [{}]/{}.{} value {} of type {} ({}) to {}.",
				profile, tableName, columnName, value, dataType, jdbcDataType, result);
		return result;
	}

	private static Object getTokenDate(String dataType) {
		DateTime value = DateMidnight.now().toDateTime();
		if (TIME.equals(dataType)) {
			return new Time(value.withDate(1970, 1, 1).withMillisOfSecond(0).getMillis());
		} else if (TIMESTAMP.equals(dataType)) {
			return new Timestamp(value.getMillis());
		}
		return new Date(value.getMillis());
	}

	private static Object getTokenTime(String dataType) {
		DateTime value = DateTime.now().withDate(1970, 1, 1).withMillisOfSecond(0);
		if (DATE.equals(dataType)) {
			return new Date(value.getMillis());
		} else if (TIMESTAMP.equals(dataType)) {
			return new Timestamp(value.getMillis());
		}
		return new Time(value.getMillis());
	}

	private static Object getTokenTimestamp(String dataType) {
		DateTime value = new DateTime();
		if (DATE.equals(dataType)) {
			return new Date(value.getMillis());
		} else if (TIMESTAMP.equals(dataType)) {
			return new Timestamp(value.getMillis());
		}
		return new Time(value.getMillis());
	}

	private static Object convertDataType(
			String profile, String tableName, String columnName, String dataType, int jdbcDataType, String value) {
		switch (dataType) {
			case BOOLEAN:
				return Boolean.parseBoolean(value);
			case BYTE:
				return Byte.parseByte(value);
			case SHORT:
				return Short.parseShort(value);
			case INTEGER:
				return Integer.parseInt(value);
			case LONG:
				return Long.parseLong(value);
			case FLOAT:
				return Float.parseFloat(value);
			case DOUBLE:
				return Double.parseDouble(value);
			case BIGDECIMAL:
				return new BigDecimal(value);
			case DATE:
				return new Date(LocalDate.parse(value).toDateMidnight().getMillis());
			case TIME:
				return new Time(LocalTime.parse(value).toDateTimeToday().withDate(1970, 1, 1)
						.withMillisOfSecond(0).getMillis());
			case TIMESTAMP:
				return new Timestamp(DateTime.parse(value).getMillis());
			case STRING:
			case NSTRING:
				return value;
			case BYTES:
				return value.getBytes();
			case CLOB:
			case NCLOB:
				return new StringReader(value);
			case BLOB:
				return new ByteArrayInputStream(value.getBytes());
		}
		log.error("Unknown type {} ({}) on [{}]/{}.{}, passing value {} through as String.",
				dataType, jdbcDataType, profile, tableName, columnName, value);
		return value;
	}
}
