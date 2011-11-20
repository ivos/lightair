package net.sf.lightair.internal.unitils.compare;

import java.util.Comparator;

/**
 * Compares row comparisons.
 * <p>
 * Enables sorting row comparisons, so that they are subsequently processed from
 * the best match to the worst match by the number of matching expected columns.
 * Secondary ordering (in case number of matching expected columns is the same)
 * is the order of the actual rows read from the database, which should be given
 * by the primary keys of the database table.
 */
public class RowComparisonComparator implements Comparator<RowComparison> {

	public int compare(RowComparison c1, RowComparison c2) {
		int matchingColumnsComparison = new Integer(
				c1.getMatchingColumnsCount()).compareTo(c2
				.getMatchingColumnsCount());
		if (0 != matchingColumnsComparison) {
			return -matchingColumnsComparison;
		}
		return new Integer(c1.getActualRowIndex()).compareTo(c2
				.getActualRowIndex());
	}

}
