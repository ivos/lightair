package net.sf.lightair.internal.util;

import org.dbunit.dataset.datatype.DataType;

/**
 * Generate <code>@auto</code> value.
 */
public interface AutoValueGenerator {

	/**
	 * Generate <code>@auto</code> value for table column, based on the column
	 * data type.
	 * 
	 * @param dataType
	 * @param tableName
	 * @param columnName
	 * @param columnLength
	 * @param decimalDigits
	 * @return
	 */
	String generateAutoValue(DataType dataType, String tableName,
			String columnName, int columnLength, Integer decimalDigits);

}
