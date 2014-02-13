package net.sf.lightair.internal.util;

import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

import net.sf.lightair.exception.DuplicateAutoValueException;

import org.dbunit.dataset.datatype.DataType;

/**
 * Generate unique <code>@auto</code> value.
 */
public class UniqueAutoValueGenerator implements AutoValueGenerator {

	private final Set<Object> values = new HashSet<Object>();

	private AutoValueGenerator delegate;

	public String generateAutoValue(DataType dataType, String tableName,
			String columnName, int columnLength, Integer decimalDigits,
			int rowId) {
		final String value = delegate.generateAutoValue(dataType, tableName,
				columnName, columnLength, decimalDigits, rowId);
		if (values.contains(value) && dataType.getSqlType() != Types.BOOLEAN) {
			throw new DuplicateAutoValueException(tableName, columnName,
					dataType, value);
		}
		values.add(value);
		return value;
	}

	/**
	 * Reset cache of values.
	 */
	public void init() {
		values.clear();
	}

	// bean setters

	public void setDelegate(AutoValueGenerator delegate) {
		this.delegate = delegate;
	}

}
