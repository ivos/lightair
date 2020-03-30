package net.sf.lightair.internal;

import net.sf.lightair.internal.auto.Auto;
import net.sf.lightair.internal.auto.Index;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Convert String values in datasets parsed from XML into proper data types based on database structure.
 */
public class Convert implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Convert.class);

	public static Map<String, List<Map<String, Object>>> convert(
			Map<String, Map<String, Map<String, Map<String, Object>>>> structures,
			Map<String, String> index,
			Map<String, List<Map<String, Object>>> datasets) {
		Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
		Set<String> autoValues = new HashSet<>();
		for (String profile : datasets.keySet()) {
			log.debug("Converting values for profile [{}].", profile);
			Map<String, Map<String, Map<String, Object>>> profileStructure = structures.get(profile);
			if (null == profileStructure) {
				throw new NullPointerException("Structure for profile [" + profile + "] is missing.");
			}
			List<Map<String, Object>> dataset = datasets.get(profile);
			result.put(profile, convertDataset(index, autoValues, profile, profileStructure, dataset));
		}
		return Collections.unmodifiableMap(result);
	}

	private static List<Map<String, Object>> convertDataset(
			Map<String, String> index, Set<String> autoValues,
			String profile, Map<String, Map<String, Map<String, Object>>> profileStructure,
			List<Map<String, Object>> dataset) {
		Map<String, Integer> rowIds = new HashMap<>();
		List<Map<String, Object>> convertedDataset = new ArrayList<>();
		for (Map<String, Object> row : dataset) {
			String tableName = (String) row.get(TABLE);
			@SuppressWarnings({"unchecked", "rawtypes"})
			Map<String, String> columns = (Map) row.get(COLUMNS);
			Map<String, Object> convertedRow = new LinkedHashMap<>();
			convertedRow.put(TABLE, tableName);
			Map<String, Object> convertedColumns;
			if (columns.isEmpty()) { // do not increment rowId on empty rows
				convertedColumns = Collections.emptyMap();
			} else {
				Integer rowId = rowIds.get(tableName);
				rowId = (null == rowId) ? 1 : rowId + 1;
				rowIds.put(tableName, rowId);
				convertedColumns = convertColumns(index, autoValues, profile, profileStructure, tableName, columns, rowId);
			}
			convertedRow.put(COLUMNS, convertedColumns);
			convertedDataset.add(Collections.unmodifiableMap(convertedRow));
		}
		return Collections.unmodifiableList(convertedDataset);
	}

	private static Map<String, Object> convertColumns(
			Map<String, String> index, Set<String> autoValues,
			String profile, Map<String, Map<String, Map<String, Object>>> profileStructure,
			String tableName, Map<String, String> columns, int rowId) {
		Map<String, Map<String, Object>> table = profileStructure.get(tableName);
		if (null == table) {
			throw new NullPointerException("Structure for table " +
					Index.formatTableKey(profile, tableName) + " is missing.");
		}
		Map<String, Object> convertedColumns = new LinkedHashMap<>();
		Set<String> datasetColumnNames = columns.keySet();
		for (String columnName : table.keySet()) { // order columns by structure, not XML dataset
			if (!datasetColumnNames.contains(columnName)) {
				continue;
			}
			Map<String, Object> column = table.get(columnName);
			Object convertedValue = convertValue(index, autoValues,
					profile, tableName, columnName, rowId,
					(String) column.get(DATA_TYPE), (Integer) column.get(JDBC_DATA_TYPE),
					(Integer) column.get(SIZE), (Integer) column.get(DECIMAL_DIGITS),
					columns.get(columnName));
			convertedColumns.put(columnName, convertedValue);
		}
		for (String columnName : datasetColumnNames) { // verify no other column in dataset
			if (!table.containsKey(columnName)) {
				throw new NullPointerException("Structure for column " +
						Index.formatColumnKey(profile, tableName, columnName) + " is missing.");
			}
		}
		return Collections.unmodifiableMap(convertedColumns);
	}

	private static Object convertValue(
			Map<String, String> index, Set<String> autoValues,
			String profile, String tableName, String columnName, int rowId,
			String dataType, int jdbcDataType, Integer columnLength, Integer decimalDigits, String value) {
		Object result;
		if (NULL_TOKEN.equals(value)) {
			result = null;
		} else if (ANY_TOKEN.equals(value) || StringUtils.startsWith(value, VARIABLE_PREFIX)) {
			result = value;
		} else if (DATE_TOKEN.equals(value)) {
			result = convertTemporal(getTokenDate(), dataType);
		} else if (TIME_TOKEN.equals(value)) {
			result = convertTemporal(getTokenTime(), dataType);
		} else if (TIMESTAMP_TOKEN.equals(value)) {
			result = convertTemporal(getTokenTimestamp(), dataType);
		} else if (isDuration(value, DATE_TOKEN)) {
			Period period = parsePeriod(value.substring(DATE_TOKEN.length()));
			result = convertTemporal(getTokenDate().plus(period), dataType);
		} else if (isDuration(value, TIME_TOKEN)) {
			Period period = parsePeriod(value.substring(TIME_TOKEN.length()));
			result = convertTemporal(getTokenTime().plus(period), dataType);
		} else if (isDuration(value, TIMESTAMP_TOKEN)) {
			Period period = parsePeriod(value.substring(TIMESTAMP_TOKEN.length()));
			result = convertTemporal(getTokenTimestamp().plus(period), dataType);
		} else {
			if (AUTO_TOKEN.equals(value)) {
				value = Auto.generate(index, profile, tableName, columnName, rowId,
						dataType, columnLength, decimalDigits);
				if (autoValues.contains(value)) {
					throw new IllegalStateException("Duplicate auto value [" + value + "] in "
							+ Index.formatColumnKey(profile, tableName, columnName) + ".");
				}
				if (!BOOLEAN.equals(dataType)) {
					autoValues.add(value);
				}
			}
			result = convertDataType(profile, tableName, columnName, dataType, jdbcDataType, value);
		}
		if (log.isTraceEnabled()) {
			if (null != result && result instanceof byte[]) {
				log.trace("Converted [{}]/{}.{} value {} of type {} ({}) bytes {}.",
						profile, tableName, columnName, value, dataType, jdbcDataType,
						Base64.encodeBase64String((byte[]) result));
			} else {
				log.trace("Converted [{}]/{}.{} value {} of type {} ({}) to {}.",
						profile, tableName, columnName, value, dataType, jdbcDataType, result);
			}
		}
		return result;
	}

	private static DateTime getTokenDate() {
		return coerceToDate(DateTime.now());
	}

	private static DateTime getTokenTime() {
		return coerceToTime(DateTime.now());
	}

	private static DateTime getTokenTimestamp() {
		return DateTime.now();
	}

	private static DateTime coerceToTime(DateTime value) {
		return value.withDate(1970, 1, 1).withMillisOfSecond(0);
	}

	private static DateTime coerceToDate(DateTime value) {
		return value.withTime(0, 0, 0, 0);
	}

	private static java.util.Date convertTemporal(DateTime value, String dataType) {
		if (DATE.equals(dataType)) {
			return new Date(coerceToDate(value).getMillis());
		}
		if (TIME.equals(dataType)) {
			return new Time(coerceToTime(value).getMillis());
		}
		return new Timestamp(value.getMillis());
	}

	private static boolean isDuration(String value, String temporalType) {
		return value.startsWith(temporalType + "+") || value.startsWith(temporalType + "-");
	}

	private static Period parsePeriod(String value) {
		if (value.startsWith("-")) {
			return Period.parse(value.substring(1)).negated();
		}
		return Period.parse(value.substring(1));
	}

	private static boolean isTrue(String value) {
		return "true".equalsIgnoreCase(value);
	}

	private static boolean isFalse(String value) {
		return "false".equalsIgnoreCase(value);
	}

	private static Object convertDataType(
			String profile, String tableName, String columnName, String dataType, int jdbcDataType, String value) {
		switch (dataType) {
			case BYTE:
			case SHORT:
			case INTEGER:
			case LONG:
			case BIGDECIMAL:
				if (isTrue(value)) {
					value = "1";
				} else if (isFalse(value)) {
					value = "0";
				}
		}
		switch (dataType) {
			case BOOLEAN:
				return isTrue(value) || "1".equals(value);
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
			case FIXED_STRING:
			case NSTRING:
			case FIXED_NSTRING:
			case CLOB:
			case NCLOB:
			case UUID:
			case JSON:
				return value;
			case BYTES:
			case BLOB:
				return Base64.decodeBase64(value);
		}
		log.error("Unknown type {} ({}) on [{}]/{}.{}, passing value {} through as String.",
				dataType, jdbcDataType, profile, tableName, columnName, value);
		return value;
	}
}
