package net.sf.lightair.internal.dbunit.dataset;

import org.dbunit.dataset.datatype.DataType;

/**
 * Custom {@link org.dbunit.dataset.Column} to provide column length.
 */
public class Column extends org.dbunit.dataset.Column {

	private final int columnLength;

	public Column(String columnName, DataType dataType, String sqlTypeName,
			Nullable nullable, String defaultValue, String remarks,
			AutoIncrement autoIncrement, int columnLength) {
		super(columnName, dataType, sqlTypeName, nullable, defaultValue,
				remarks, autoIncrement);
		this.columnLength = columnLength;
	}

	public int getColumnLength() {
		return columnLength;
	}

}
