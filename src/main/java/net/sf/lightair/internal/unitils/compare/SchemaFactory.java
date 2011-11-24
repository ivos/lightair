package net.sf.lightair.internal.unitils.compare;

import java.util.List;

import net.sf.lightair.internal.dbunit.dataset.MergingTable;
import net.sf.lightair.internal.factory.Factory;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.datatype.DataType;
import org.unitils.dbunit.dataset.Row;
import org.unitils.dbunit.dataset.Schema;

/**
 * Fork of Unitils SchemaFactory to allow for customizations.
 */
public class SchemaFactory extends org.unitils.dbunit.dataset.SchemaFactory {

	// Extracted to use own Table

	@Override
	protected void addTables(IDataSet dbUnitDataSet, Schema schema)
			throws DataSetException {
		ITableIterator dbUnitTableIterator = dbUnitDataSet.iterator();
		while (dbUnitTableIterator.next()) {
			ITable dbUnitTable = dbUnitTableIterator.getTable();
			String tableName = dbUnitTable.getTableMetaData().getTableName();

			List<String> primaryKeyColumnNames = getPrimaryKeyColumnNames(dbUnitTable);

			org.unitils.dbunit.dataset.Table table = schema.getTable(tableName);
			if (table == null) {

				// Use own Table:
				table = createTable(tableName);

				schema.addTable(table);
			}
			addRows(dbUnitTable, table, primaryKeyColumnNames);
		}
	}

	/**
	 * Instantiate Table.
	 * 
	 * @param tableName
	 *            Name of table
	 * @return New Table instance
	 */
	protected org.unitils.dbunit.dataset.Table createTable(String tableName) {
		return new Table(tableName);
	}

	// Extracted to ignore column value when column not expected
	// and to use own Column

	@Override
	protected void addRows(ITable dbUnitTable,
			org.unitils.dbunit.dataset.Table table,
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

				org.unitils.dbunit.dataset.Column column = createColumn(
						columnName, columnType, value);
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

	/**
	 * Instantiate Column.
	 * 
	 * @param columnName
	 *            Name of column
	 * @param columnType
	 *            Type of column
	 * @param value
	 *            Column value
	 * @return New Column instance
	 */
	protected org.unitils.dbunit.dataset.Column createColumn(String columnName,
			DataType columnType, Object value) {
		Column column = new Column(columnName, columnType, value);
		Factory.getInstance().initColumn(column);
		return column;
	}
}
