package net.sf.lightair.internal.dbunit.dataset;

import org.dbunit.dataset.Column;

/**
 * Value of a table column in a row.
 * <p>
 * Immutable JavaBean with column and value properties.
 */
public class ColumnValue {

	private final Column column;
	private final Object value;

	/**
	 * Default constructor.
	 * 
	 * @param column
	 *            Column
	 * @param value
	 *            Value
	 */
	public ColumnValue(Column column, Object value) {
		this.column = column;
		this.value = value;
	}

	/**
	 * Return column.
	 * 
	 * @return Column
	 */
	public Column getColumn() {
		return column;
	}

	/**
	 * Return value.
	 * 
	 * @return Value
	 */
	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "ColumnValue [column=" + column + ", value=" + value + "]";
	}

}
