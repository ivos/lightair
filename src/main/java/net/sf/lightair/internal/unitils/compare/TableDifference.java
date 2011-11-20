package net.sf.lightair.internal.unitils.compare;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.unitils.dbunit.dataset.Row;
import org.unitils.dbunit.dataset.Table;
import org.unitils.dbunit.dataset.comparison.RowDifference;

/**
 * Fork of Unitils TableDifference to allow for customization.
 */
public class TableDifference extends
		org.unitils.dbunit.dataset.comparison.TableDifference {

	// Extracted to use LinkedHashMap to maintain order to enable testing:
	/**
	 * For any given expected row keeps a difference to best matching actual
	 * row.
	 */
	private final Map<Row, RowDifference> bestRowDifferences;

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
		bestRowDifferences = createBestRowDifferences();
	}

	protected Map<Row, RowDifference> createBestRowDifferences() {
		return new LinkedHashMap<Row, RowDifference>();
	}

	/**
	 * @return The best results in the comparison between the rows, not null
	 */
	@Override
	public List<RowDifference> getBestRowDifferences() {
		return new ArrayList<RowDifference>(bestRowDifferences.values());
	}

	/**
	 * @param row
	 *            The row to get the difference for, not null
	 * @return The best difference, null if not found or if there was a match
	 */
	@Override
	public RowDifference getBestRowDifference(Row row) {
		return bestRowDifferences.get(row);
	}

	/**
	 * Sets the given difference as best row difference.
	 * 
	 * @param rowDifference
	 *            The difference
	 */
	public void setBestRowDifference(RowDifference rowDifference) {
		bestRowDifferences.put(rowDifference.getRow(), rowDifference);
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
		return getMissingRows().isEmpty() && bestRowDifferences.isEmpty()
				&& unexpectedRows.isEmpty();
	}

}
