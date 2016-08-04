package net.sf.lightair.internal;

import net.sf.lightair.internal.auto.Index;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Compare implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Compare.class);

	public static Map<String, Map<String, Map<String, List<?>>>> compare(
			Map<String, String> profileProperties,
			Map<String, List<Map<String, Object>>> expectedDatasets,
			Map<String, Map<String, List<Map<String, Object>>>> actualDatasets) {
		long limit = getLimit(profileProperties);
		Map<String, Object> variables = new HashMap<>();

		Map<String, Map<String, List<Map<String, Object>>>> processedExpectedDatasets =
				processExpectedDatasets(expectedDatasets);

		Map<String, Map<String, Map<String, List<?>>>> differences = new LinkedHashMap<>();
		for (String profile : processedExpectedDatasets.keySet()) {
			log.debug("Comparing profile [{}].", profile);
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
				Map<String, List<?>> tableDifferences =
						compareTable(limit, variables, expectedDataset.get(tableName), actualRows);
				profileDifferences.put(tableName, tableDifferences);
			}
			differences.put(profile, Collections.unmodifiableMap(profileDifferences));
		}
		return Collections.unmodifiableMap(differences);
	}

	private static long getLimit(Map<String, String> profileProperties) {
		String limit = profileProperties.get(TIME_DIFFERENCE_LIMIT_MILLIS);
		if (null == limit) {
			return 0;
		}
		return Long.valueOf(limit);
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
			long limit, Map<String, Object> variables,
			List<Map<String, Object>> expectedRows,
			List<Map<String, Object>> actualRows) {
		List<Map<String, Object>> sortedActualRows = sortActualRows(limit, expectedRows, actualRows);

		List<Map<String, Object>> different = new ArrayList<>();
		List<Map<String, Object>> unexpected = new ArrayList<>();
		List<Integer> unmatchedExpectedRowIds = IntStream
				.rangeClosed(0, expectedRows.size() - 1).boxed()
				.filter(i -> !expectedRows.get(i).keySet().isEmpty()) // skip empty expected rows
				.collect(Collectors.toList());

		for (int actualRowId = 0; actualRowId < sortedActualRows.size(); actualRowId++) {
			Map<String, Object> actualRow = sortedActualRows.get(actualRowId);
			if (unmatchedExpectedRowIds.isEmpty()) {
				unexpected.add(actualRow);
				continue;
			}
			List<Map<String, Object>> unmatchedExpectedRows =
					getUnmatchedExpectedRows(expectedRows, unmatchedExpectedRowIds);
			int bestMatchingRowId = findBestMatchingRowId(limit, actualRow, unmatchedExpectedRows);
			unmatchedExpectedRowIds.remove(bestMatchingRowId);
			Map<String, Object> expectedRow = unmatchedExpectedRows.get(bestMatchingRowId);
			List<Map<String, Object>> rowDifferences = getRowDifferences(limit, variables, expectedRow, actualRow);
			if (rowDifferences.isEmpty()) {
				log.trace("Perfect match for row {}.", expectedRow);
			} else {
				Map<String, Object> differentRow = new LinkedHashMap<>();
				differentRow.put(EXPECTED, expectedRow);
				differentRow.put(DIFFERENCES, rowDifferences);
				different.add(Collections.unmodifiableMap(differentRow));
				log.debug("Different row {}.", expectedRow);
			}
		}

		List<Map<String, Object>> missing = getUnmatchedExpectedRows(expectedRows, unmatchedExpectedRowIds);
		missing.stream().forEachOrdered(row -> log.debug("Missing row {}.", row));

		Map<String, List<?>> data = new LinkedHashMap<>();
		data.put(MISSING, Collections.unmodifiableList(missing));
		data.put(DIFFERENT, Collections.unmodifiableList(different));
		data.put(UNEXPECTED, Collections.unmodifiableList(unexpected));
		return Collections.unmodifiableMap(data);
	}

	private static List<Map<String, Object>> getUnmatchedExpectedRows(
			List<Map<String, Object>> expectedRows,
			List<Integer> unmatchedExpectedRowIds) {
		return unmatchedExpectedRowIds.stream()
				.map(expectedRows::get)
				.collect(Collectors.toList());
	}

	private static List<Map<String, Object>> sortActualRows(
			long limit,
			List<Map<String, Object>> expectedRows,
			List<Map<String, Object>> actualRows) {
		if (expectedRows.isEmpty()) {
			return actualRows;
		}
		int[][] data = new int[actualRows.size()][2];
		for (int actualRowId = 0; actualRowId < actualRows.size(); actualRowId++) {
			Map<String, Object> actualRow = actualRows.get(actualRowId);
			int bestMatchingRowId = findBestMatchingRowId(limit, actualRow, expectedRows);
			data[actualRowId][0] = actualRowId;
			data[actualRowId][1] =
					getRowDifferences(limit, null, expectedRows.get(bestMatchingRowId), actualRow).size();
		}
		Arrays.sort(data, (int[] o1, int[] o2) -> {
			int matchingColumnsComparison = Integer.compare(o1[1], o2[1]);
			if (0 != matchingColumnsComparison) {
				return matchingColumnsComparison;
			}
			return Integer.compare(o1[0], o2[0]);
		});
		List<Map<String, Object>> sortedActualRows = new ArrayList<>();
		for (int actualRowId = 0; actualRowId < actualRows.size(); actualRowId++) {
			sortedActualRows.add(actualRows.get(data[actualRowId][0]));
		}
		return Collections.unmodifiableList(sortedActualRows);
	}

	private static Integer findBestMatchingRowId(
			long limit,
			Map<String, Object> actualRow,
			List<Map<String, Object>> expectedRows) {
		List<Map<String, Object>> bestRowDifferences = null;
		Integer bestExpectedRowId = null;
		for (int expectedRowId = 0; expectedRowId < expectedRows.size(); expectedRowId++) {
			Map<String, Object> expectedRow = expectedRows.get(expectedRowId);
			List<Map<String, Object>> rowDifferences = getRowDifferences(limit, null, expectedRow, actualRow);
			if (null == bestRowDifferences || rowDifferences.size() < bestRowDifferences.size()) {
				bestRowDifferences = rowDifferences;
				bestExpectedRowId = expectedRowId;
			}
		}
		return bestExpectedRowId;
	}

	private static List<Map<String, Object>> getRowDifferences(
			long limit, Map<String, Object> variables,
			Map<String, Object> expectedRow,
			Map<String, Object> actualRow) {
		List<Map<String, Object>> data = new ArrayList<>();
		for (String columnName : expectedRow.keySet()) {
			Object expectedValue = expectedRow.get(columnName);
			Object actualValue = actualRow.get(columnName);
			if (!valueMatches(limit, variables, expectedValue, actualValue)) {
				data.add(createDifference(variables, columnName, expectedValue, actualValue));
			}
		}
		return Collections.unmodifiableList(data);
	}

	private static Map<String, Object> createDifference(
			Map<String, Object> variables,
			String columnName, Object expectedValue, Object actualValue) {
		if (isVariable(variables, expectedValue)) {
			expectedValue = variables.get(expectedValue);
		}
		LinkedHashMap<String, Object> data = new LinkedHashMap<>();
		data.put(COLUMN, columnName);
		data.put(EXPECTED, expectedValue);
		data.put(ACTUAL, actualValue);
		return Collections.unmodifiableMap(data);
	}

	private static boolean valueMatches(
			long limit, Map<String, Object> variables,
			Object expectedValue, Object actualValue) {
		if (ANY_TOKEN.equals(expectedValue) && null != actualValue) {
			return true;
		}
		if (isVariable(variables, expectedValue)) {
			if (variables.containsKey(expectedValue)) {
				expectedValue = variables.get(expectedValue);
			} else {
				variables.put((String) expectedValue, actualValue);
				log.debug("Assigned to variable [{}] value [{}].", expectedValue, actualValue);
				return true;
			}
		}
		if (expectedValue instanceof Date && actualValue instanceof Date) {
			long diff = Math.abs(((Date) expectedValue).getTime() - ((Date) actualValue).getTime());
			return diff <= limit;
		}
		if (expectedValue instanceof byte[] && actualValue instanceof byte[]) {
			return Arrays.equals((byte[]) expectedValue, (byte[]) actualValue);
		}
		return Objects.equals(expectedValue, actualValue);
	}

	private static boolean isVariable(Map<String, Object> variables, Object expectedValue) {
		return (null != variables) && (expectedValue instanceof String)
				&& StringUtils.startsWith((String) expectedValue, VARIABLE_PREFIX);
	}
}
