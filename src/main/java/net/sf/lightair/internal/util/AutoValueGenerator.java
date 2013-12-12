package net.sf.lightair.internal.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.GregorianCalendar;

import net.sf.lightair.exception.UnsupportedDataType;

import org.apache.commons.lang.StringUtils;
import org.dbunit.dataset.datatype.DataType;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoValueGenerator {

	private final Logger log = LoggerFactory
			.getLogger(AutoValueGenerator.class);

	private AutoNumberGenerator autoNumberGenerator;

	public String generateAutoValue(DataType dataType, String tableName,
			String columnName) {
		int autoNumber = autoNumberGenerator.generateAutoNumber(tableName,
				columnName, 0);
		String value = generate(dataType, columnName, autoNumber);
		log.debug("Generated auto value for {}.{} data type {} as {}.",
				tableName, columnName, dataType, value);
		return value;
	}

	private String generate(DataType dataType, String columnName, int autoNumber) {
		String autoNumberString = StringUtils.leftPad(
				String.valueOf(autoNumber), 7, '0');
		int offset = new GregorianCalendar().get(Calendar.ZONE_OFFSET);

		switch (dataType.getSqlType()) {
		case Types.INTEGER:
		case Types.BIGINT:
		case Types.DECIMAL:
			return String.valueOf(autoNumber);
		case Types.DOUBLE:
			return String.valueOf(((double) autoNumber) / 100);
		case Types.BOOLEAN:
			return String.valueOf(0 != autoNumber % 2);
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.CLOB:
		case Types.BLOB:
		case Types.VARBINARY:
			return columnName + ' ' + autoNumberString;
		case Types.DATE:
			// Add autoNumber days from local date 1900-01-01, wrapping
			// approximately around 2100.
			// Date from 1900-01-01 to 2099-11-12
			return new LocalDate(1900, 1, 1).plusDays(autoNumber % 73000)
					.toString();
		case Types.TIME:
			// Add autoNumber of seconds to local time 0:00:00.
			return new Time(1000L * autoNumber - offset).toString();
		case Types.TIMESTAMP:
			// Put autoNumber twice after each other.
			// Add resulting number of milliseconds from local date 1900-01-01,
			// wrapping approximately around 2100.
			return new Timestamp(new LocalDateTime(1900, 1, 1, 0, 0, 0)
					.plusDays(autoNumber % 73000)
					.plusSeconds(autoNumber % 86400)
					.plusMillis(autoNumber % 1000).toDate().getTime())
					.toString();
		}
		throw new UnsupportedDataType(dataType.toString());
	}

	public void setAutoNumberGenerator(AutoNumberGenerator autoNumberGenerator) {
		this.autoNumberGenerator = autoNumberGenerator;
	}

}
