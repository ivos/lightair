package net.sf.lightair.internal.dbunit.dataset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.NoSuchColumnException;

/**
 * Table metadata that can be changed after created.
 * <p>
 * Used when parsing XML datasets, where additional columns may be defined on
 * any subsequent row.
 */
public class MutableTableMetaData implements ITableMetaData {

	private final String tableName;
	private final List<Column> columns;

	/**
	 * Default constructor.
	 * 
	 * @param tableName
	 *            Table name
	 * @param columns
	 *            Initial table columns
	 */
	public MutableTableMetaData(String tableName, Column... columns) {
		this.tableName = tableName;
		this.columns = new ArrayList<Column>(Arrays.asList(columns));
	}

	public String getTableName() {
		return tableName;
	}

	public Column[] getColumns() {
		return columns.toArray(new Column[0]);
	}

	/**
	 * No primary keys in XML. Returns empty array.
	 * 
	 * @return Empty array
	 */
	public Column[] getPrimaryKeys() {
		return new Column[0];
	}

	public int getColumnIndex(String columnName) throws NoSuchColumnException {
		for (int i = 0; i < columns.size(); i++) {
			if (columnName.equalsIgnoreCase(columns.get(i).getColumnName())) {
				return i;
			}
		}
		throw new NoSuchColumnException(getTableName(), columnName,
				"column not found. Note that column names are NOT case sensitive.");
	}

	/**
	 * Return true iff a column of the given name exists.
	 * 
	 * @param columnName
	 *            Column name
	 * @return true iff such column exists
	 */
	public boolean hasColumn(String columnName) {
		for (int i = 0; i < columns.size(); i++) {
			if (columnName.equalsIgnoreCase(columns.get(i).getColumnName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Add new column.
	 * 
	 * @param column
	 *            Column to add
	 */
	public void addColumn(Column column) {
		columns.add(column);
	}

	@Override
	public String toString() {
		return "MutableTableMetaData [tableName=" + tableName + ", columns="
				+ columns + "]";
	}

}
