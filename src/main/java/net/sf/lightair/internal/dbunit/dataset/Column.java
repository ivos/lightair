package net.sf.lightair.internal.dbunit.dataset;

import org.dbunit.dataset.datatype.DataType;

/**
 * Custom {@link org.dbunit.dataset.Column} to provide column lenght.
 */
public class Column extends org.dbunit.dataset.Column {

	public Column(String columnName, DataType dataType, String sqlTypeName,
			Nullable nullable, String defaultValue, String remarks,
			AutoIncrement autoIncrement) {
		super(columnName, dataType, sqlTypeName, nullable, defaultValue,
				remarks, autoIncrement);
		System.out.println("    ========= My column " + columnName);
	}

}
