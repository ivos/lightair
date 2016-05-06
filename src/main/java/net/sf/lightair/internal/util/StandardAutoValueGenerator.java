package net.sf.lightair.internal.util;

import java.sql.Types;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.dbunit.dataset.datatype.DataType;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.lightair.exception.UnsupportedDataType;

/**
 * Generate <code>@auto</code> value.
 */
public class StandardAutoValueGenerator implements AutoValueGenerator {

	private final Logger log = LoggerFactory.getLogger(StandardAutoValueGenerator.class);

	private AutoNumberGenerator autoNumberGenerator;

	@Override
	public String generateAutoValue(DataType dataType, String tableName, String columnName, int columnLength,
			Integer decimalDigits, int rowId) {
		String lowerColumnName = columnName.toLowerCase();
		String lowerTableName = tableName.toLowerCase();
		int autoNumber = autoNumberGenerator.generateAutoNumber(lowerTableName, lowerColumnName, rowId);
		String value = generate(dataType, lowerColumnName, autoNumber, columnLength, decimalDigits);
		log.debug("Generated auto value for {}.{} of data type {} as [{}].", lowerTableName, lowerColumnName, dataType,
				value);
		return value;
	}

	private String generate(DataType dataType, String columnName, int autoNumber, int columnLength,
			Integer decimalDigits) {
		String autoNumberString = StringUtils.leftPad(String.valueOf(autoNumber), 7, '0');
		String stringValue = formatStringValue(columnName, autoNumberString, columnLength);
		log.trace(
				"Generating auto value for data type {}, column name {}, "
						+ "auto number {}, column length {}, decimal digits {}, string value {}.",
				dataType, columnName, autoNumber, columnLength, decimalDigits);

		switch (dataType.getSqlType()) {
		case Types.INTEGER:
		case Types.BIGINT:
			return String.valueOf(autoNumber);
		case Types.SMALLINT:
			return String.valueOf(autoNumber % 10000);
		case Types.TINYINT:
			return String.valueOf(autoNumber % 100);
		case Types.REAL:
		case Types.DOUBLE:
		case Types.FLOAT:
			return String.valueOf(((double) autoNumber) / 100);
		case Types.DECIMAL:
		case Types.NUMERIC:
			return formatDecimalValue(autoNumber, columnLength, decimalDigits);
		case Types.BOOLEAN:
			return String.valueOf(0 != autoNumber % 2);
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.NCHAR:
		case Types.NVARCHAR:
		case Types.CLOB:
			return stringValue;
		case Types.BLOB:
		case Types.BINARY:
		case Types.VARBINARY:
			return Base64.encodeBase64String(stringValue.getBytes());
		case Types.DATE:
			// Add autoNumber days from local date 1900-01-01, wrapping
			// approximately around 2100.
			// Date from 1900-01-01 to 2099-11-12
			return new LocalDate(1900, 1, 1).plusDays(autoNumber % 73000).toString();
		case Types.TIME:
			// Add autoNumber of seconds to local time 0:00:00.
			return new LocalTime(0, 0, 0).plusSeconds(autoNumber).toString("HH:mm:ss");
		case Types.TIMESTAMP:
			// Add autoNumber days from local date 1900-01-01, wrapping
			// approximately around 2100.
			// Add autoNumber of seconds to local time 0:00:00, wrapping at 1
			// day.
			// Add autoNumber of milliseconds, wrapping at 1 second.
			return new LocalDateTime(1900, 1, 1, 0, 0, 0).plusDays(autoNumber % 73000).plusSeconds(autoNumber % 86400)
					.plusMillis(autoNumber % 1000).toString("yyyy-MM-dd HH:mm:ss.SSS");
		}
		throw new UnsupportedDataType(dataType.toString());
	}

	private String formatDecimalValue(int autoNumber, int columnLength, Integer decimalDigits) {
		// cut precision to column length
		int cut = autoNumber;
		if (columnLength > 0) {
			cut = cut % (int) Math.pow(10, Math.min(columnLength, 7));
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

	private String formatStringValue(String columnName, String autoNumberString, int columnLength) {
		if (autoNumberString.length() > columnLength) {
			return autoNumberString.substring(autoNumberString.length() - columnLength);
		}
		String prefix = columnName + ' ';
		if (prefix.length() + autoNumberString.length() > columnLength) {
			prefix = prefix.substring(0, columnLength - autoNumberString.length());
		}
		return prefix + autoNumberString;
	}

	// bean setters

	public void setAutoNumberGenerator(AutoNumberGenerator autoNumberGenerator) {
		this.autoNumberGenerator = autoNumberGenerator;
	}

}
