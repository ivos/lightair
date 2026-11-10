package net.sf.lightair.internal;

import net.sf.lightair.internal.auto.Auto;
import net.sf.lightair.internal.auto.Index;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.PeriodDuration;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Convert String values in datasets parsed from XML into proper data types based on the database structure.
 */
public class Convert implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Convert.class);

	public static Map<String, List<Map<String, Object>>> convert(
		Clock clock,
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures,
		Map<String, String> index,
		Map<String, List<Map<String, Object>>> datasets
	) {
		Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
		Set<String> autoValues = new HashSet<>();
		for (String profile : datasets.keySet()) {
			log.debug("Converting values for profile [{}].", profile);
			Map<String, Map<String, Map<String, Object>>> profileStructure = structures.get(profile);
			if (null == profileStructure) {
				throw new NullPointerException("Structure for profile [" + profile + "] is missing.");
			}
			List<Map<String, Object>> dataset = datasets.get(profile);
			result.put(profile, convertDataset(clock, index, autoValues, profile, profileStructure, dataset));
		}
		return Collections.unmodifiableMap(result);
	}

	private static List<Map<String, Object>> convertDataset(
		Clock clock,
		Map<String, String> index, Set<String> autoValues,
		String profile, Map<String, Map<String, Map<String, Object>>> profileStructure,
		List<Map<String, Object>> dataset
	) {
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
				convertedColumns = convertColumns(clock, index, autoValues, profile, profileStructure, tableName,
					columns, rowId);
			}
			convertedRow.put(COLUMNS, convertedColumns);
			convertedDataset.add(Collections.unmodifiableMap(convertedRow));
		}
		return Collections.unmodifiableList(convertedDataset);
	}

	private static Map<String, Object> convertColumns(
		Clock clock,
		Map<String, String> index, Set<String> autoValues,
		String profile, Map<String, Map<String, Map<String, Object>>> profileStructure,
		String tableName, Map<String, String> columns, int rowId
	) {
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
			Object convertedValue = convertValue(clock, index, autoValues,
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
		Clock clock,
		Map<String, String> index, Set<String> autoValues,
		String profile, String tableName, String columnName, int rowId,
		String dataType, int jdbcDataType, Integer columnLength, Integer decimalDigits, String value
	) {
		Object result;
		if (NULL_TOKEN.equals(value)) {
			result = null;
		} else if (ANY_TOKEN.equals(value) || Strings.CS.startsWith(value, VARIABLE_PREFIX)) {
			result = value;
		} else if (DATE_TOKEN.equals(value)) {
			result = convertTemporal(getTokenDate(clock), dataType);
		} else if (TIME_TOKEN.equals(value)) {
			result = convertTemporal(getTokenTime(clock), dataType);
		} else if (TIMESTAMP_TOKEN.equals(value)) {
			result = convertTemporal(getTokenTimestamp(clock), dataType);
		} else if (isDuration(value, DATE_TOKEN)) {
			TemporalAmount period = parsePeriod(value, DATE_TOKEN);
			result = convertTemporal(getTokenDate(clock).plus(period), dataType);
		} else if (isDuration(value, TIME_TOKEN)) {
			TemporalAmount period = parsePeriod(value, TIME_TOKEN);
			result = convertTemporal(getTokenTime(clock).plus(period), dataType);
		} else if (isDuration(value, TIMESTAMP_TOKEN)) {
			TemporalAmount period = parsePeriod(value, TIMESTAMP_TOKEN);
			result = convertTemporal(getTokenTimestamp(clock).plus(period), dataType);
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
			if (result instanceof byte[]) {
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

	private static LocalDateTime getTokenDate(Clock clock) {
		return coerceToDate(LocalDateTime.now(clock));
	}

	private static LocalDateTime getTokenTime(Clock clock) {
		return coerceToTime(LocalDateTime.now(clock));
	}

	private static LocalDateTime getTokenTimestamp(Clock clock) {
		return LocalDateTime.now(clock);
	}

	private static LocalDateTime coerceToDate(LocalDateTime value) {
		return value.truncatedTo(ChronoUnit.DAYS);
	}

	private static LocalDateTime coerceToTime(LocalDateTime value) {
		return value
			.withYear(1970)
			.withMonth(1)
			.withDayOfMonth(1)
			.truncatedTo(ChronoUnit.SECONDS);
	}

	private static java.util.Date convertTemporal(LocalDateTime value, String dataType) {
		if (DATE.equals(dataType)) {
			return Date.valueOf(coerceToDate(value).toLocalDate());
		}
		if (TIME.equals(dataType)) {
			return Time.valueOf(coerceToTime(value).toLocalTime());
		}
		return Timestamp.valueOf(value);
	}

	private static boolean isDuration(String value, String temporalType) {
		return value.startsWith(temporalType + "+") || value.startsWith(temporalType + "-");
	}

	private static PeriodDuration parsePeriod(String value, String token) {
		String period = value.substring(token.length());
		return PeriodDuration.parse(period.startsWith("+") ? period.substring(1) : period);
	}

	private static boolean isTrue(String value) {
		return "true".equalsIgnoreCase(value);
	}

	private static boolean isFalse(String value) {
		return "false".equalsIgnoreCase(value);
	}

	private static Object convertDataType(
		String profile, String tableName, String columnName, String dataType, int jdbcDataType, String value
	) {
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
				return Date.valueOf(LocalDate.parse(value));
			case TIME:
				return Time.valueOf(LocalTime.parse(value));
			case TIMESTAMP:
				return Timestamp.valueOf(LocalDateTime.parse(value));
			case STRING:
			case FIXED_STRING:
			case NSTRING:
			case FIXED_NSTRING:
			case CLOB:
			case NCLOB:
			case UUID:
			case JSON:
			case JSONB:
				return value;
			case BYTES:
			case BLOB:
				return Base64.decodeBase64(value);
			case ARRAY_STRING:
				return convertArray(new String[0], Function.identity(), value);
			case ARRAY_INTEGER:
				return convertArray(new Integer[0], Integer::parseInt, value);
			case ARRAY_LONG:
				return convertArray(new Long[0], Long::parseLong, value);
		}
		log.error("Unknown type {} ({}) on [{}]/{}.{}, passing value {} through as String.",
			dataType, jdbcDataType, profile, tableName, columnName, value);
		return value;
	}

	private static Object convertArray(Object[] emptyValue, Function<String, ?> valueConverter, String value) {
		if ("".equals(value)) { // treat empty string as an empty array rather than array with an empty string
			return emptyValue;
		}
		return Arrays.stream(value.split(ARRAY_ELEMENT_SEPARATOR))
			.map(s -> NULL_TOKEN.equals(s) ? null : valueConverter.apply(s))
			.toList()
			.toArray(emptyValue);
	}
}
