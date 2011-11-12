package net.sf.lightair.internal.unitils.compare;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.unitils.dbunit.dataset.Row;
import org.unitils.dbunit.dataset.comparison.RowDifference;

/**
 * Fork of Unitils Table to allow for customization.
 */
public class Table extends org.unitils.dbunit.dataset.Table {

	/**
	 * Creates a data set table.
	 * 
	 * @param name
	 *            The name of the table, not null
	 */
	public Table(String name) {
		super(name);
	}

	// Extracted to use own TableDifference

	@Override
	public org.unitils.dbunit.dataset.comparison.TableDifference compare(
			org.unitils.dbunit.dataset.Table actualTable) {

		// Use own TableDifference:
		TableDifference result = createTableDifference(actualTable);

		if (isEmpty()) {
			if (actualTable.isEmpty()) {
				return null;
			}
			return result;
		}

		compareRows(getRows(), actualTable, result);
		if (result.isMatch()) {
			return null;
		}
		return result;
	}

	/**
	 * Instantiate TableDifference.
	 * 
	 * @param actualTable
	 *            Table to compare with
	 * @return New TableDifference instance
	 */
	protected TableDifference createTableDifference(
			org.unitils.dbunit.dataset.Table actualTable) {
		return new TableDifference(this, actualTable);
	}

	// Extracted to report unexpected rows

	protected void compareRows(List<Row> rows,
			org.unitils.dbunit.dataset.Table actualTable, TableDifference result) {
		List<Row> rowsWithoutMatch = new ArrayList<Row>(rows);
		for (Row actualRow : actualTable.getRows()) {
			Iterator<Row> rowIterator = rowsWithoutMatch.iterator();
			boolean isUnexpected = true;
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				if (row.hasDifferentPrimaryKeyColumns(actualRow)) {
					continue;
				}
				isUnexpected = false;
				RowDifference rowDifference = row.compare(actualRow);
				if (rowDifference == null) {
					result.setMatchingRow(row, actualRow);
					rowIterator.remove();
					break;
				} else {
					result.setIfBestRowDifference(rowDifference);
				}
			}

			// Report unexpected rows:
			if (isUnexpected) {
				result.addUnexpectedRow(actualRow);
			}
		}

		for (Row row : rowsWithoutMatch) {
			if (result.getBestRowDifference(row) == null) {
				result.addMissingRow(row);
			}
		}
	}

}
