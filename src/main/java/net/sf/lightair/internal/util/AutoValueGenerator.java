package net.sf.lightair.internal.util;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import net.sf.lightair.exception.UnsupportedDataType;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.dbunit.dataset.datatype.DataType;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generate <code>@auto</code> value.
 */
public class AutoValueGenerator {

	private final Logger log = LoggerFactory
			.getLogger(AutoValueGenerator.class);

	private AutoNumberGenerator autoNumberGenerator;

	/**
	 * Generate <code>@auto</code> value for table column, based on the column
	 * data type.
	 * 
	 * @param dataType
	 * @param tableName
	 * @param columnName
	 * @return
	 */
	public String generateAutoValue(DataType dataType, String tableName,
			String columnName) {
		final int rowIndex = getNextRowIndex(tableName, columnName);
		int autoNumber = autoNumberGenerator.generateAutoNumber(tableName,
				columnName, rowIndex);
		String value = generate(dataType, columnName, autoNumber);
		log.debug("Generated auto value for {}.{} of data type {} as [{}].",
				tableName, columnName, dataType, value);
		return value;
	}

	private String generate(DataType dataType, String columnName, int autoNumber) {
		String autoNumberString = StringUtils.leftPad(
				String.valueOf(autoNumber), 7, '0');
		String stringValue = columnName + ' ' + autoNumberString;

		switch (dataType.getSqlType()) {
		case Types.INTEGER:
		case Types.BIGINT:
			return String.valueOf(autoNumber);
		case Types.DECIMAL:
		case Types.DOUBLE:
			return String.valueOf(((double) autoNumber) / 100);
		case Types.BOOLEAN:
			return String.valueOf(0 != autoNumber % 2);
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.CLOB:
			return stringValue;
		case Types.BLOB:
		case Types.VARBINARY:
			return Base64.encodeBase64String(stringValue.getBytes());
		case Types.DATE:
			// Add autoNumber days from local date 1900-01-01, wrapping
			// approximately around 2100.
			// Date from 1900-01-01 to 2099-11-12
			return new LocalDate(1900, 1, 1).plusDays(autoNumber % 73000)
					.toString();
		case Types.TIME:
			// Add autoNumber of seconds to local time 0:00:00.
			return new LocalTime(0, 0, 0).plusSeconds(autoNumber).toString(
					"HH:mm:ss");
		case Types.TIMESTAMP:
			// Add autoNumber days from local date 1900-01-01, wrapping
			// approximately around 2100.
			// Add autoNumber of seconds to local time 0:00:00, wrapping at 1
			// day.
			// Add autoNumber of milliseconds, wrapping at 1 second.
			return new LocalDateTime(1900, 1, 1, 0, 0, 0)
					.plusDays(autoNumber % 73000)
					.plusSeconds(autoNumber % 86400)
					.plusMillis(autoNumber % 1000)
					.toString("yyyy-MM-dd HH:mm:ss.SSS");
		}
		throw new UnsupportedDataType(dataType.toString());
	}

	private final Map<String, Integer> rowIndexes = new HashMap<String, Integer>();

	private int getNextRowIndex(String tableName, String columnName) {
		StringBuffer sb = new StringBuffer();
		sb.append('"');
		sb.append(tableName);
		sb.append("\".\"");
		sb.append(columnName);
		sb.append('"');
		String key = sb.toString();
		Integer currentValue = rowIndexes.get(key);
		if (null == currentValue) {
			currentValue = 0;
		} else {
			currentValue++;
		}
		rowIndexes.put(key, currentValue);
		return currentValue;
	}

	/**
	 * Reset cache of row indexes.
	 */
	public void init() {
		rowIndexes.clear();
	}

	// bean setters

	public void setAutoNumberGenerator(AutoNumberGenerator autoNumberGenerator) {
		this.autoNumberGenerator = autoNumberGenerator;
	}

}
