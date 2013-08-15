package net.sf.lightair.internal.dbunit.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.lightair.internal.factory.Factory;
import net.sf.lightair.internal.util.DataSetProcessingData;

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
		Factory.getInstance().initMergingTable(this);
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
	 * Has a value for the row's column been specified?
	 * 
	 * @param rowId
	 *            Id of row
	 * @param column
	 *            Name of column
	 * @return true iff the column is defined on the row
	 */
	public boolean hasValue(int rowId, String column) {
		Map<String, ColumnValue> row = rows.get(rowId);
		ColumnValue columnValue = row.get(column.toUpperCase());
		return (null != columnValue);
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
			setRowColumn(row, column, values[i]);
		}
		rows.add(row);
	}

	/**
	 * Set value for a row's column.
	 * <p>
	 * Replaces possible tokens in the value.
	 * 
	 * @param row
	 *            Row
	 * @param column
	 *            Column
	 * @param rawValue
	 *            Value
	 */
	private void setRowColumn(Map<String, ColumnValue> row, Column column,
			Object rawValue) {
		if ("@any".equals(rawValue)) {
			dataSetProcessingData.setTokenAnyPresent();
		}
		Object value = tokenReplacingFilter.replaceTokens(rawValue);
		row.put(column.getColumnName().toUpperCase(), new ColumnValue(column,
				value));
	}

	/**
	 * Merge-in rows from other table with possibly different columns.
	 * 
	 * @param otherTable
	 *            Table whose rows to merge-in
	 */
	public void addTableRows(MergingTable otherTable) {
		Column[] otherColumns = otherTable.getTableMetaData().getColumns();
		mergeMetaData(otherColumns);
		mergeRows(otherTable, otherColumns);
	}

	/**
	 * Add all columns of other table that this table does not have yet.
	 * 
	 * @param otherColumns
	 *            Other table columns
	 */
	private void mergeMetaData(Column[] otherColumns) {
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
					setRowColumn(row, column,
							otherTable.getValue(rowId, column.getColumnName()));
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

	private TokenReplacingFilter tokenReplacingFilter;

	/**
	 * Set TokenReplacingFilter.
	 * 
	 * @param tokenReplacingFilter
	 */
	public void setTokenReplacingFilter(
			TokenReplacingFilter tokenReplacingFilter) {
		this.tokenReplacingFilter = tokenReplacingFilter;
	}

	private DataSetProcessingData dataSetProcessingData;

	/**
	 * Set DataSetProcessingData.
	 * 
	 * @param dataSetProcessingData
	 */
	public void setDataSetProcessingData(
			DataSetProcessingData dataSetProcessingData) {
		this.dataSetProcessingData = dataSetProcessingData;
	}
}
