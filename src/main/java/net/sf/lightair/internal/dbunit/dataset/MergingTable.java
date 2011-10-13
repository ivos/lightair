package net.sf.lightair.internal.dbunit.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.dataset.AbstractTable;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.RowOutOfBoundsException;

/**
 * Table that can merge-in rows from other table with different columns.
 * <p>
 * Used when parsing XML datasets, where additional columns may be defined on
 * any subsequent row.
 */
public class MergingTable extends AbstractTable {

	private final MutableTableMetaData metaData;
	private final List<Map<String, ColumnValue>> rows;

	/**
	 * Default constructor.
	 * 
	 * @param metaData
	 *            Table meta data
	 */
	public MergingTable(MutableTableMetaData metaData) {
		this.metaData = metaData;
		this.rows = new ArrayList<Map<String, ColumnValue>>();
	}

	public MutableTableMetaData getTableMetaData() {
		return metaData;
	}

	public int getRowCount() {
		return rows.size();
	}

	/**
	 * Returns this table value for the specified row and column.
	 * 
	 * @param row
	 *            The row index, starting with 0
	 * @param column
	 *            The name of the column
	 * @return The value, or null if there is no such column in the row
	 * 
	 * @throws RowOutOfBoundsException
	 *             If specified row is equal or greater than
	 *             <code>getRowCount</code>. This is required by DbUnit as a
	 *             signal to stop iterating rows in database operations (the
	 *             exception is caught and swallowed there).
	 */
	public Object getValue(int rowId, String column)
			throws RowOutOfBoundsException {
		if (rowId >= rows.size()) {
			throw new RowOutOfBoundsException(
					"Signal end of table rows to DbUnit.");
		}
		Map<String, ColumnValue> row = rows.get(rowId);
		ColumnValue columnValue = row.get(column.toUpperCase());
		if (null == columnValue) {
			return null;
		}
		return columnValue.getValue();
	}

	/**
	 * Add new row with values corresponding to the current table columns.
	 * 
	 * @param values
	 *            Row values corresponding to the current table columns
	 */
	public void addRow(Object... values) {
		Map<String, ColumnValue> row = new HashMap<String, ColumnValue>();
		Column[] columns = metaData.getColumns();
		for (int i = 0; i < values.length; i++) {
			Column column = columns[i];
			row.put(column.getColumnName().toUpperCase(), new ColumnValue(
					column, values[i]));
		}
		rows.add(row);
	}

	/**
	 * Merge-in rows from other table with possibly different columns.
	 * 
	 * @param otherTable
	 *            Table whose rows to merge-in
	 */
	public void addTableRows(MergingTable otherTable) {
		Column[] otherColumns = otherTable.getTableMetaData().getColumns();
		mergeMetaData(otherTable, otherColumns);
		mergeRows(otherTable, otherColumns);
	}

	/**
	 * Add all columns of other table that this table does not have yet.
	 * 
	 * @param otherTable
	 *            Other table
	 */
	private void mergeMetaData(MergingTable otherTable, Column[] otherColumns) {
		for (int colId = 0; colId < otherColumns.length; colId++) {
			Column column = otherColumns[colId];
			if (!metaData.hasColumn(column.getColumnName())) {
				metaData.addColumn(column);
			}
		}
	}

	/**
	 * Add all rows values from other table to this table.
	 * 
	 * @param otherTable
	 *            Other table
	 */
	private void mergeRows(MergingTable otherTable, Column[] otherColumns) {
		for (int rowId = 0; rowId < otherTable.getRowCount(); rowId++) {
			Map<String, ColumnValue> row = new HashMap<String, ColumnValue>();
			for (int colId = 0; colId < otherColumns.length; colId++) {
				Column column = otherColumns[colId];
				try {
					row.put(column.getColumnName().toUpperCase(),
							new ColumnValue(column, otherTable.getValue(rowId,
									column.getColumnName())));
				} catch (RowOutOfBoundsException e) {
					// this should never happen as we iterate until rowCount
					throw new IllegalStateException(e);
				}
			}
			rows.add(row);
		}
	}

	@Override
	public String toString() {
		return "MergingTable [metaData=" + metaData + ", rows=" + rows + "]";
	}

}
