package net.sf.lightair.internal.dbunit.dataset;

import org.dbunit.dataset.datatype.DataType;

/**
 * Custom {@link org.dbunit.dataset.Column} to provide column length.
 */
public class Column extends org.dbunit.dataset.Column {

	private final int columnLength;
	private final Integer columnPrecision;

	public Column(String columnName, DataType dataType, String sqlTypeName,
			Nullable nullable, String defaultValue, String remarks,
			AutoIncrement autoIncrement, int columnLength,
			Integer columnPrecision) {
		super(columnName, dataType, sqlTypeName, nullable, defaultValue,
				remarks, autoIncrement);
		this.columnLength = columnLength;
		this.columnPrecision = columnPrecision;
	}

	public int getColumnLength() {
		return columnLength;
	}

	public Integer getColumnPrecision() {
		return columnPrecision;
	}

}
