package net.sf.lightair.internal.unitils.compare;

import java.util.ArrayList;
import java.util.List;

import org.unitils.dbunit.dataset.Row;
import org.unitils.dbunit.dataset.comparison.ColumnDifference;

/**
 * Comparison of an actual row to an expected row.
 */
public class RowComparison {

	private final Row expectedRow, actualRow;
	private final int actualRowIndex;
	private final List<org.unitils.dbunit.dataset.Column> missingColumns = new ArrayList<org.unitils.dbunit.dataset.Column>();
	private final List<ColumnDifference> columnDifferences = new ArrayList<ColumnDifference>();
	private final int rowId;

	/**
	 * Constructor.
	 * 
	 * @param expectedRow
	 *            Expected row
	 * @param actualRow
	 *            Actual row
	 * @param actualRowIndex
	 *            Zero-based index of actual row in database table
	 * @param rowId
	 *            expected row index
	 */
	public RowComparison(Row expectedRow, Row actualRow, int actualRowIndex,
			int rowId) {
		this.expectedRow = expectedRow;
		this.actualRow = actualRow;
		this.actualRowIndex = actualRowIndex;
		this.rowId = rowId;
		compare();
	}

	/**
	 * Perform comparison.
	 */
	private void compare() {
		compareColumns(expectedRow.getPrimaryKeyColumns());
		compareColumns(expectedRow.getColumns());
	}

	/**
	 * Compare a set of columns.
	 * 
	 * @param expectedColumns
	 *            Expected columns to compare
	 */
	private void compareColumns(
			List<org.unitils.dbunit.dataset.Column> expectedColumns) {
		for (org.unitils.dbunit.dataset.Column expectedColumn : expectedColumns) {
			org.unitils.dbunit.dataset.Column actualColumn = actualRow
					.getColumn(expectedColumn.getName());
			if (actualColumn == null) {
				missingColumns.add(expectedColumn);
			} else {
				ColumnDifference columnDifference = ((Column) expectedColumn)
						.preCompare(actualColumn, rowId);
				if (columnDifference != null) {
					columnDifferences.add(columnDifference);
				}
			}
		}
	}

	/**
	 * Return count of matching columns.
	 * 
	 * @return Count of matching columns
	 */
	public int getMatchingColumnsCount() {
		int columnCount = expectedRow.getPrimaryKeyColumns().size()
				+ expectedRow.getColumns().size();
		int missingColumnsCount = missingColumns.size();
		int differentColumnsCount = columnDifferences.size();
		return columnCount - missingColumnsCount - differentColumnsCount;
	}

	/**
	 * Is the other row comparison a better match than this?
	 * 
	 * @param other
	 *            Other row comparison
	 * @return <code>true</code> iff the other row comparison a better match
	 *         than this
	 */
	public boolean isBetterMatch(RowComparison other) {
		return other.getMatchingColumnsCount() > getMatchingColumnsCount();
	}

	/**
	 * Return zero-based index of actual row in database table.
	 * 
	 * @return Zero-based index of actual row in database table
	 */
	public int getActualRowIndex() {
		return actualRowIndex;
	}

	/**
	 * Return actual row.
	 * 
	 * @return Actual row
	 */
	public Row getActualRow() {
		return actualRow;
	}

	/**
	 * Return expected row.
	 * 
	 * @return Expected row
	 */
	public Row getExpectedRow() {
		return expectedRow;
	}

	/**
	 * Does the expected row match the actual row in all expected columns?
	 * 
	 * @return <code>true</code> iff the expected row matches the actual row in
	 *         all expected columns
	 */
	public boolean isMatch() {
		return columnDifferences.isEmpty() && missingColumns.isEmpty();
	}

}
