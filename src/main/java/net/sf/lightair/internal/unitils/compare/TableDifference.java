package net.sf.lightair.internal.unitils.compare;

import java.util.ArrayList;
import java.util.List;

import org.unitils.dbunit.dataset.Row;
import org.unitils.dbunit.dataset.Table;

/**
 * Fork of Unitils TableDifference to allow for customization.
 */
public class TableDifference extends
		org.unitils.dbunit.dataset.comparison.TableDifference {

	/**
	 * The rows not present in expected dataset but found in the actual database
	 * dataset.
	 */
	private final List<Row> unexpectedRows = new ArrayList<Row>();

	/**
	 * Creates a table difference.
	 * 
	 * @param table
	 *            The expected table, not null
	 * @param actualTable
	 *            The actual table, not null
	 */
	public TableDifference(Table table, Table actualTable) {
		super(table, actualTable);
	}

	/**
	 * @return The rows not present in expected dataset but found in the actual
	 *         database dataset
	 */
	public List<Row> getUnexpectedRows() {
		return unexpectedRows;
	}

	/**
	 * Add a row not present in expected dataset but found in the actual
	 * database dataset.
	 * 
	 * @param unexpectedRow
	 *            Unexpected row
	 */
	public void addUnexpectedRow(Row unexpectedRow) {
		unexpectedRows.add(unexpectedRow);
	}

	@Override
	public boolean isMatch() {
		return super.isMatch() && unexpectedRows.isEmpty();
	}

}
