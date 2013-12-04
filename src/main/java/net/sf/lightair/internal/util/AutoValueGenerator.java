package net.sf.lightair.internal.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Types;

import net.sf.lightair.exception.UnsupportedDataType;

import org.apache.commons.lang.StringUtils;
import org.dbunit.dataset.datatype.DataType;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class AutoValueGenerator {

	private AutoNumberGenerator autoNumberGenerator;

	public Object generateAutoValue(DataType dataType, String tableName,
			String columnName) {
		final int autoNumber = autoNumberGenerator.generateAutoNumber(
				tableName, columnName, 0);
		final String autoNumberString = StringUtils.leftPad(
				String.valueOf(autoNumber), 7, '0');
		switch (dataType.getSqlType()) {
		case Types.INTEGER:
			return new Integer(autoNumber);
		case Types.CHAR:
		case Types.VARCHAR:
			return columnName + ' ' + autoNumberString;
		case Types.DATE:
			// add autoNumber days from 1900-01-01, wrapping around 2100
			// date from 1900-01-01 to 2099-11-12
			return new Date(new LocalDate(1900, 1, 1)
					.plusDays(autoNumber % 73000).toDate().getTime());
		case Types.TIME:
			// add autoNumber of seconds
			return new Time(new LocalTime(1000L * autoNumber,
					DateTimeZone.getDefault()).getMillisOfDay());
		}
		throw new UnsupportedDataType(dataType.toString());
	}

	public void setAutoNumberGenerator(AutoNumberGenerator autoNumberGenerator) {
		this.autoNumberGenerator = autoNumberGenerator;
	}

}
