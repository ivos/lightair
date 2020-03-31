package net.sf.lightair.internal;

import net.sf.lightair.internal.auto.Index;
import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generate textual report from structured differences between datasets.
 */
public class Report implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Report.class);

	public static String report(Map<String, Map<String, Map<String, List<?>>>> differences) {
		StringBuilder sb = new StringBuilder();
		for (String profile : differences.keySet()) {
			Map<String, Map<String, List<?>>> profileDifferences = differences.get(profile);
			for (String tableName : profileDifferences.keySet()) {
				Map<String, List<?>> tableDifferences = profileDifferences.get(tableName);
				appendTableDifferences(profile, tableName, sb, tableDifferences);
			}
		}
		String report = sb.toString();
		if (!report.isEmpty()) {
			report = "Differences found between the expected data set and actual database content." + report + "\n";
		} else {
			log.debug("No differences found.");
		}
		return report;
	}

	private static void appendTableDifferences(
			String profile, String tableName,
			StringBuilder sb, Map<String, List<?>> tableDifferences) {
		@SuppressWarnings({"unchecked", "rawtypes"})
		List<Map<String, Object>> missing = (List) tableDifferences.get(MISSING);
		@SuppressWarnings({"unchecked", "rawtypes"})
		List<Map<String, Object>> different = (List) tableDifferences.get(DIFFERENT);
		@SuppressWarnings({"unchecked", "rawtypes"})
		List<Map<String, Object>> unexpected = (List) tableDifferences.get(UNEXPECTED);

		if (!missing.isEmpty() || !different.isEmpty() || !unexpected.isEmpty()) {
			log.debug("Found differences for table [{}]/{}.", profile, tableName);
			sb.append("\nFound differences for table ");
			if (DEFAULT_PROFILE.equals(profile)) {
				sb.append(tableName);
			} else {
				sb.append(Index.formatTableKey(profile, tableName));
			}
			sb.append(":");
			appendRows(sb, "Missing", missing);
			appendDifferent(sb, different);
			appendRows(sb, "Unexpected", unexpected);
		}
	}

	private static void appendRows(StringBuilder sb, String type, List<Map<String, Object>> missing) {
		for (Map<String, Object> row : missing) {
			sb.append("\n  " + type + " row: ");
			appendRow(sb, row);
		}
	}

	private static void appendRow(StringBuilder sb, Map<String, Object> row) {
		sb.append("{");
		List<String> columnValues = new ArrayList<>();
		for (String columnName : row.keySet()) {
			columnValues.add(columnName + "=" + convertValue(row.get(columnName)));
		}
		sb.append(String.join(", ", columnValues));
		sb.append("}");
	}

	private static void appendDifferent(StringBuilder sb, List<Map<String, Object>> different) {
		for (Map<String, Object> differentRow : different) {
			@SuppressWarnings({"unchecked", "rawtypes"})
			Map<String, Object> expectedRow = (Map) differentRow.get(EXPECTED);
			@SuppressWarnings({"unchecked", "rawtypes"})
			List<Map<String, Object>> rowDifferences = (List) differentRow.get(DIFFERENCES);
			sb.append("\n  Different row: ");
			appendRow(sb, expectedRow);
			sb.append("\n   Best matching differences: ");
			for (Map<String, Object> difference : rowDifferences) {
				String columnName = (String) difference.get(COLUMN);
				Object expectedValue = difference.get(EXPECTED);
				Object actualValue = difference.get(ACTUAL);
				sb.append("\n    " + columnName + ": expected [");
				sb.append(convertValue(expectedValue) + "], but was [" + convertValue(actualValue) + "]");
			}
		}
	}

	private static Object convertValue(Object value) {
		if (value instanceof Timestamp) {
			return new DateTime(value).toString("yyyy-MM-dd'T'HH:mm:ss.SSS");
		}
		if (value instanceof byte[]) {
			return Base64.encodeBase64String((byte[]) value);
		}
		if (value instanceof Object[]) {
			return Arrays.stream((Object[]) value)
					.map(object -> object == null ? NULL_TOKEN : String.valueOf(object))
					.collect(Collectors.joining(ARRAY_ELEMENT_SEPARATOR));
		}
		return value;
	}
}
