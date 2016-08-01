package net.sf.lightair.internal;

import net.sf.lightair.internal.auto.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Compare implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Compare.class);

	public static Map<String, Map<String, Map<String, List<?>>>> compare(
			Map<String, List<Map<String, Object>>> expectedDatasets,
			Map<String, Map<String, List<Map<String, Object>>>> actualDatasets) {
		Map<String, Map<String, List<Map<String, Object>>>> processedExpectedDatasets =
				processExpectedDatasets(expectedDatasets);

		Map<String, Map<String, Map<String, List<?>>>> differences = new LinkedHashMap<>();
		for (String profile : processedExpectedDatasets.keySet()) {
			log.debug("Comparing profile {}.", profile);
			Map<String, Map<String, List<?>>> profileDifferences = new LinkedHashMap<>();
			Map<String, List<Map<String, Object>>> expectedDataset = processedExpectedDatasets.get(profile);
			Map<String, List<Map<String, Object>>> actualDataset = actualDatasets.get(profile);
			if (null == actualDataset) {
				throw new NullPointerException(
						"Missing profile " + profile + " in actual datasets.");
			}
			for (String tableName : expectedDataset.keySet()) {
				log.debug("Comparing table {}.", tableName);
				List<Map<String, Object>> actualRows = actualDataset.get(tableName);
				if (null == actualRows) {
					throw new NullPointerException(
							"Missing table " + Index.formatTableKey(profile, tableName) + " in actual dataset.");
				}
				Map<String, List<?>> tableDifferences = compareTable(expectedDataset.get(tableName), actualRows);
				profileDifferences.put(tableName, tableDifferences);
			}
			differences.put(profile, Collections.unmodifiableMap(profileDifferences));
		}
		return Collections.unmodifiableMap(differences);
	}

	/**
	 * <ol>
	 * <li>Convert from <code>profile->List->table&columns->column</code>
	 * into <code>profile->table->List->column</code> structure.</li>
	 * <li>Sort table rows by number of columns descending.</li>
	 * </ol>
	 */
	private static Map<String, Map<String, List<Map<String, Object>>>> processExpectedDatasets(
			Map<String, List<Map<String, Object>>> expectedDatasets) {
		Map<String, Map<String, List<Map<String, Object>>>> data = new LinkedHashMap<>();
		for (String profile : expectedDatasets.keySet()) {
			List<Map<String, Object>> expectedDataset = expectedDatasets.get(profile);
			Map<String, List<Map<String, Object>>> processedDataset = new LinkedHashMap<>();
			for (Map<String, Object> row : expectedDataset) {
				String tableName = (String) row.get(TABLE);
				@SuppressWarnings("unchecked")
				Map<String, Object> columns = (Map) row.get(COLUMNS);
				List<Map<String, Object>> table = processedDataset.get(tableName);
				if (null == table) {
					table = new ArrayList<>();
					processedDataset.put(tableName, table);
				}
				table.add(columns);
			}
			for (String tableName : processedDataset.keySet()) {
				List<Map<String, Object>> table = processedDataset.get(tableName);
				Collections.sort(table,
						(Map<String, Object> o1, Map<String, Object> o2) ->
								-Integer.compare(o1.keySet().size(), o2.keySet().size()));
				processedDataset.put(tableName, Collections.unmodifiableList(table));
			}
			data.put(profile, Collections.unmodifiableMap(processedDataset));
		}
		log.trace("Processed expected dataset.");
		return Collections.unmodifiableMap(data);
	}

	private static Map<String, List<?>> compareTable(
			List<Map<String, Object>> expectedRows,
			List<Map<String, Object>> actualRows) {
		List<Map<String, Object>> missing = new ArrayList<>();
		List<Map<String, Object>> different = new ArrayList<>();
		Set<Integer> matched = new HashSet<>();

		for (Map<String, Object> expectedRow : expectedRows) {
			if (expectedRow.keySet().isEmpty()) { // table expected empty
				log.debug("Empty expected row, do not match any actual rows.");
				continue;
			}

			log.trace("Trying to match expected row {}.", expectedRow);
			Integer matchedRowId = null;
			List<Map<String, Object>> matchedRowDifferences = null;
			for (int actualRowId = 0; actualRowId < actualRows.size(); actualRowId++) {
				if (matched.contains(actualRowId)) { // this actual row has already been matched: skip it
					continue;
				}
				Map<String, Object> actualRow = actualRows.get(actualRowId);
				List<Map<String, Object>> rowDifferences = getRowDifferences(expectedRow, actualRow);
				if (null == matchedRowId) { // first actual row, accept as matching for the time being
					matchedRowId = actualRowId;
					matchedRowDifferences = rowDifferences;
				} else if (rowDifferences.size() < matchedRowDifferences.size()) {
					// better match found: replace matched row and its differences
					matchedRowId = actualRowId;
					matchedRowDifferences = rowDifferences;
				}
				if (matchedRowDifferences.isEmpty()) {
					break; // perfect match found: skip all further matching
				}
			}

			if (null == matchedRowDifferences) { // no actual row remained: the expected row is missing
				log.debug("Missing row {}.", expectedRow);
				missing.add(expectedRow);
			} else { // the expected row matched
				matched.add(matchedRowId);
				if (!matchedRowDifferences.isEmpty()) { // the expected row is different
					Map<String, Object> differentRow = new LinkedHashMap<>();
					differentRow.put(EXPECTED, expectedRow);
					differentRow.put(DIFFERENCES, matchedRowDifferences);
					different.add(Collections.unmodifiableMap(differentRow));
					log.debug("Different row {}.", expectedRow);
				} else { // perfect match
					log.trace("Perfect match for row {}.", expectedRow);
				}
			}

		}

		Map<String, List<?>> data = new LinkedHashMap<>();
		data.put(MISSING, Collections.unmodifiableList(missing));
		data.put(DIFFERENT, Collections.unmodifiableList(different));
		data.put(UNEXPECTED, getUnexpectedRows(expectedRows, actualRows, matched));
		return Collections.unmodifiableMap(data);
	}

	private static List<Map<String, Object>> getUnexpectedRows(
			List<Map<String, Object>> expectedRows,
			List<Map<String, Object>> actualRows,
			Set<Integer> matched) {
		List<Map<String, Object>> unexpected = new ArrayList<>();
		for (int actualRowId = 0; actualRowId < actualRows.size(); actualRowId++) {
			if (!matched.contains(actualRowId)) { // unmatched actual row: unexpected
				Map<String, Object> actualRow = actualRows.get(actualRowId);
				unexpected.add(actualRow);
				log.debug("Unexpected row {}.", actualRow);
			}
		}
		return Collections.unmodifiableList(unexpected);
	}

	private static List<Map<String, Object>> getRowDifferences(
			Map<String, Object> expectedRow,
			Map<String, Object> actualRow) {
		List<Map<String, Object>> data = new ArrayList<>();
		for (String columnName : expectedRow.keySet()) {
			Object expectedValue = expectedRow.get(columnName);
			Object actualValue = actualRow.get(columnName);
			if (!valueMatches(expectedValue, actualValue)) {
				data.add(createDifference(columnName, expectedValue, actualValue));
			}
		}
		return Collections.unmodifiableList(data);
	}

	private static Map<String, Object> createDifference(
			String columnName, Object expectedValue, Object actualValue) {
		LinkedHashMap<String, Object> data = new LinkedHashMap<>();
		data.put(COLUMN, columnName);
		data.put(EXPECTED, expectedValue);
		data.put(ACTUAL, actualValue);
		return Collections.unmodifiableMap(data);
	}

	private static boolean valueMatches(Object expectedValue, Object actualValue) {
		if (expectedValue instanceof byte[] && actualValue instanceof byte[]) {
			return Arrays.equals((byte[]) expectedValue, (byte[]) actualValue);
		}
		return Objects.equals(expectedValue, actualValue);
	}
}
