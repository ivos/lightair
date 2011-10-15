package net.sf.lightair.internal.dbunit.dataset;

import java.util.Collection;

import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableIterator;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.OrderedTableNameMap;

/**
 * Dataset to be used to read XML datasets in DbUnit's "flat XML" format.
 * <p>
 * Supports additional columns on subsequent rows.
 */
public class FlatXmlDataSet extends AbstractDataSet {

	private OrderedTableNameMap tables;
	private MergingTable currentTable;

	@Override
	protected ITableIterator createIterator(boolean reversed)
			throws DataSetException {
		@SuppressWarnings("unchecked")
		ITable[] tableArray = ((Collection<ITable>) tables.orderedValues())
				.toArray(new ITable[0]);
		return new DefaultTableIterator(tableArray, reversed);
	}

	/**
	 * Start parsing dataset.
	 */
	public void startDataSet() {
		tables = super.createTableNameMap();
	}

	/**
	 * Start parsing a dataset row.
	 * <p>
	 * Creates a new current table.
	 * 
	 * @param metaData
	 *            Meta data defining columns of the row
	 */
	public void startRow(MutableTableMetaData metaData) {
		currentTable = new MergingTable(metaData);
	}

	/**
	 * Set values of the row started.
	 * <p>
	 * Adds a row to current table.
	 * 
	 * @param values
	 *            Values in the order of the row columns.
	 */
	public void setRowValues(Object[] values) {
		currentTable.addRow(values);
	}

	/**
	 * End parsing a dataset row.
	 * <p>
	 * If the current table already exists in dataset, merges the parsed row of
	 * current table to the existing dataset table.
	 * <p>
	 * If the current table does not exist in dataset, adds it to dataset.
	 * <p>
	 * Reset current table.
	 */
	public void endRow() {
		String tableName = currentTable.getTableMetaData().getTableName();
		if (tables.containsTable(tableName)) {
			MergingTable existingTable = (MergingTable) tables.get(tableName);
			existingTable.addTableRows(currentTable);
		} else {
			try {
				tables.add(tableName, currentTable);
			} catch (AmbiguousTableNameException e) {
				// should never happen
				throw new IllegalStateException(
						"Inconsistent DbUnit behavior.", e);
			}
		}
		currentTable = null;
	}

}
