package net.sf.lightair.internal.unitils.compare;

import java.util.ArrayList;
import java.util.Collections;
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

		compareRows(actualTable, result);
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

	// Extracted to re-implement the row matching algorithm

	protected void compareRows(org.unitils.dbunit.dataset.Table actualTable,
			TableDifference result) {
		List<RowComparison> rowComparisons = new ArrayList<RowComparison>();
		int actualRowIndex = 0;
		for (Row actualRow : actualTable.getRows()) {
			RowComparison bestRowComparison = findBestRowComparison(actualRow,
					actualRowIndex, getRows());
			rowComparisons.add(bestRowComparison);
			actualRowIndex++;
		}
		Collections.sort(rowComparisons, new RowComparisonComparator());
		List<Row> sortedActualRows = new ArrayList<Row>();
		for (RowComparison rowComparison : rowComparisons) {
			sortedActualRows.add(rowComparison.getActualRow());
		}
		List<Row> unmatchedExpectedRows = new ArrayList<Row>(getRows());
		for (Row actualRow : sortedActualRows) {
			if (unmatchedExpectedRows.isEmpty()) {
				result.addUnexpectedRow(actualRow);
				continue;
			}
			RowComparison bestRowComparison = findBestRowComparison(actualRow,
					0, unmatchedExpectedRows);
			Row matchedExpectedRow = bestRowComparison.getExpectedRow();
			unmatchedExpectedRows.remove(matchedExpectedRow);
			RowDifference rowDifference = matchedExpectedRow.compare(actualRow);
			if (null != rowDifference) {
				result.setBestRowDifference(rowDifference);
			}
		}
		for (Row row : unmatchedExpectedRows) {
			result.addMissingRow(row);
		}
	}

	private RowComparison findBestRowComparison(Row actualRow,
			int actualRowIndex, List<Row> expectedRows) {
		RowComparison bestRowComparison = null;
		int rowId = -1;
		for (Row expectedRow : expectedRows) {
			rowId++;
			RowComparison rowComparison = new RowComparison(expectedRow,
					actualRow, actualRowIndex, rowId);
			if (null == bestRowComparison
					|| bestRowComparison.isBetterMatch(rowComparison)) {
				bestRowComparison = rowComparison;
			}
		}
		return bestRowComparison;
	}

}
