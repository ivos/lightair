package net.sf.lightair.internal.auto;

import net.sf.lightair.internal.Keywords;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * Generate unique pseudo-random values for the <code>@auto</code> token.
 */
public class Auto implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Auto.class);

	private static final String AUTO_NUMBER_PREFIX = "1";

	public static String generate(
			Map<String, String> index,
			String profile, String tableName, String columnName, int rowId,
			String dataType, Integer columnLength, Integer decimalDigits) {
		Objects.requireNonNull(index, "Index is required.");
		if (null == columnLength) {
			throw new NullPointerException("Column length missing for column " +
					Index.formatColumnKey(profile, tableName, columnName) + ".");
		}
		String autoNumber = formatAutoNumber(index, profile, tableName, columnName, rowId);
		String value = convert(autoNumber, columnName, dataType, columnLength, decimalDigits);

		log.debug("Generated auto value for [{}]/{}.{} of data type {} as [{}].",
				profile, tableName, columnName, dataType, value);
		return value;
	}

	private static String formatAutoNumber(
			Map<String, String> index,
			String profile, String tableName, String columnName, int rowId) {
		String tableKey = Index.formatTableKey(profile, tableName);
		String columnKey = Index.formatColumnKey(profile, tableName, columnName);
		String tableHash = index.get(tableKey);
		String columnHash = index.get(columnKey);

		if (null == tableHash) {
			throw new NullPointerException("Table " +
					Index.formatTableKey(profile, tableName) + " is missing in index.");
		}
		if (null == columnHash) {
			throw new NullPointerException("Column " +
					Index.formatColumnKey(profile, tableName, columnName) + " is missing in index.");
		}

		String rowHash = StringUtils.leftPad(String.valueOf(rowId), 2, '0');
		return AUTO_NUMBER_PREFIX + tableHash + columnHash + rowHash;
	}

	private static String formatStringValue(String columnName, String autoNumber, int columnLength) {
		if (autoNumber.length() > columnLength) {
			return autoNumber.substring(autoNumber.length() - columnLength);
		}
		String prefix = columnName + ' ';
		if (prefix.length() + autoNumber.length() > columnLength) {
			prefix = prefix.substring(0, columnLength - autoNumber.length());
		}
		return prefix + autoNumber;
	}

	private static String formatDecimalValue(int number, int columnLength, Integer decimalDigits) {
		// cut precision to column length
		int cut = number;
		if (columnLength > 0) {
			cut = cut % (int) Math.pow(10, Math.min(columnLength, 10));
		}
		// convert to String
		String decimal = String.valueOf(cut);
		// pad with zeroes left to decimal digits + 1
		decimal = StringUtils.leftPad(decimal, decimalDigits + 1, '0');
		// insert decimal point
		if (decimalDigits > 0) {
			int pointIndex = decimal.length() - decimalDigits;
			decimal = decimal.substring(0, pointIndex) + "." + decimal.substring(pointIndex);
		}
		return decimal;
	}

	private static String convert(
			String autoNumber, String columnName,
			String dataType, int columnLength, Integer decimalDigits) {
		String stringValue = formatStringValue(columnName, autoNumber, columnLength);
		log.trace("Generating auto value for data type {}, column name {}, "
						+ "auto number {}, column length {}, decimal digits {}, string value {}.",
				dataType, columnName, autoNumber, columnLength, decimalDigits, stringValue);
		int number = Integer.valueOf(autoNumber);
		switch (dataType) {
			case BOOLEAN:
				return String.valueOf(0 != number % 2);
			case BYTE:
				return String.valueOf(number % 100);
			case SHORT:
				return String.valueOf(number % 10_000);
			case INTEGER:
			case LONG:
				return autoNumber;
			case FLOAT:
			case DOUBLE:
				return String.valueOf(((double) number) / 100);
			case BIGDECIMAL:
				return formatDecimalValue(number, columnLength, decimalDigits);
			case DATE:
				// Add number days from local date 1900-01-01, wrapping approximately around 2100.
				// Date from 1900-01-01 to 2099-11-12
				return new LocalDate(1900, 1, 1).plusDays(number % 73_000).toString();
			case TIME:
				// Add number of seconds to local time 0:00:00.
				return new LocalTime(0, 0, 0).plusSeconds(number).toString("HH:mm:ss");
			case TIMESTAMP:
				// Add number days from local date 1900-01-01, wrapping approximately around 2100.
				// Add number of seconds to local time 0:00:00, wrapping at 1 day.
				// Add number of milliseconds, wrapping at 1 second.
				return new LocalDateTime(1900, 1, 1, 0, 0, 0)
						.plusDays(number % 73_000)
						.plusSeconds(number % 86_400)
						.plusMillis(number % 1_000)
						.toString("yyyy-MM-dd'T'HH:mm:ss.SSS");
			case STRING:
			case NSTRING:
			case CLOB:
			case NCLOB:
				return stringValue;
			case FIXED_STRING:
			case FIXED_NSTRING:
				return StringUtils.rightPad(stringValue, columnLength);
			case BYTES:
			case BLOB:
				return Base64.encodeBase64String(stringValue.getBytes());
		}
		log.error("Unknown type {} on column {}, returning integer value {}.", dataType, columnName, autoNumber);
		return autoNumber;
	}
}
