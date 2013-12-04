package net.sf.lightair.internal.util;

import org.dbunit.dataset.datatype.DataType;

public class AutoValueGenerator {

	private AutoNumberGenerator autoNumberGenerator;

	public Object generateAutoValue(DataType dataType, String tableName,
			String columnName) {
		final int autoNumber = autoNumberGenerator.generateAutoNumber(
				tableName, columnName, 0);
		return new Integer(autoNumber);
	}

	public void setAutoNumberGenerator(AutoNumberGenerator autoNumberGenerator) {
		this.autoNumberGenerator = autoNumberGenerator;
	}

}
