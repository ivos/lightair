package net.sf.lightair.internal.unitils.compare;

import java.util.List;

import net.sf.lightair.internal.dbunit.dataset.MergingTable;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;
import org.unitils.dbunit.dataset.Column;
import org.unitils.dbunit.dataset.Row;
import org.unitils.dbunit.dataset.Table;

/**
 * Fork of Unitils SchemaFactory to ignore values of database column not
 * specified in the expected dataset.
 */
public class SchemaFactory extends org.unitils.dbunit.dataset.SchemaFactory {

	@Override
	protected void addRows(ITable dbUnitTable, Table table,
			List<String> primaryKeyColumnNames) throws DataSetException {
		org.dbunit.dataset.Column[] columns = dbUnitTable.getTableMetaData()
				.getColumns();
		int rowCount = dbUnitTable.getRowCount();
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			Row row = new Row();
			table.addRow(row);

			for (org.dbunit.dataset.Column dbUnitColumn : columns) {
				String columnName = dbUnitColumn.getColumnName();
				DataType columnType = dbUnitColumn.getDataType();

				// Ignore column value when column not expected:
				if (isColumnNotExpected(dbUnitTable, rowIndex, columnName)) {
					continue;
				}

				Object value = dbUnitTable.getValue(rowIndex, columnName);

				Column column = new Column(columnName, columnType, value);
				if (primaryKeyColumnNames.contains(columnName)) {
					row.addPrimaryKeyColumn(column);
				} else {
					row.addColumn(column);
				}
			}
		}
	}

	/**
	 * Is the column not specified in the expected dataset on the row?
	 * 
	 * @param dbUnitTable
	 *            Table
	 * @param rowIndex
	 *            Id of row
	 * @param columnName
	 *            Name of column
	 * @return <code>true</code> iff the column is not specified on the row in
	 *         expected table
	 */
	private boolean isColumnNotExpected(ITable dbUnitTable, int rowIndex,
			String columnName) {
		if (dbUnitTable instanceof MergingTable) {
			MergingTable mergingTable = (MergingTable) dbUnitTable;
			if (!mergingTable.hasValue(rowIndex, columnName)) {
				return true;
			}
		}
		return false;
	}
}
