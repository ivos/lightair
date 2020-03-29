package net.sf.lightair.internal.auto;

import net.sf.lightair.internal.Keywords;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Keep index file with hashes for tables and columns.
 */
public class Index implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Index.class);

	public static final String AUTO_INDEX_FILE = "light-air.auto-index.properties";
	private static final char COLUMN_SEPARATOR = '.';

	public static Map<String, String> readAndUpdate(
			Map<String, Map<String, String>> properties,
			Map<String, Map<String, Map<String, Map<String, Object>>>> structures) {
		log.debug("Reading index.");
		String autoIndexDirectory = getAutoIndexDirectory(properties);
		File file = new File(autoIndexDirectory, AUTO_INDEX_FILE);

		Map<String, String> indexFile = new LinkedHashMap<>();
		loadIndexFile(file, indexFile);
		Map<String, String> updates = getUpdates(Collections.unmodifiableMap(indexFile), structures);
		writeUpdatesToIndexFile(file, updates);

		indexFile.putAll(updates);
		return Collections.unmodifiableMap(indexFile);
	}

	public static String formatTableKey(String profile, String tableName) {
		return "[" + profile + "]/" + tableName;
	}

	public static String formatColumnKey(String profile, String tableName, String columnName) {
		return formatTableKey(profile, tableName) + COLUMN_SEPARATOR + columnName;
	}

	private static String getAutoIndexDirectory(Map<String, Map<String, String>> properties) {
		String autoIndexDirectory = properties.get(DEFAULT_PROFILE).get(AUTO_INDEX_DIRECTORY);
		if (null == autoIndexDirectory) {
			return DEFAULT_AUTO_INDEX_DIRECTORY;
		}
		return autoIndexDirectory;
	}

	private static void loadIndexFile(File file, Map<String, String> indexFile) {
		if (!file.exists()) {
			return;
		}
		log.debug("Reading index file {}.", file.getPath());
		try {
			FileUtils.readLines(file, StandardCharsets.UTF_8).stream()
					.forEachOrdered(line -> {
						String[] parts = line.split("=");
						indexFile.put(parts[0].trim(), parts[1].trim());
					});
		} catch (IOException e) {
			throw new RuntimeException("Cannot load auto index file " + file.getPath(), e);
		}
	}

	private static boolean isTableKey(String key) {
		return !isColumnKey(key);
	}

	private static boolean isColumnKey(String key) {
		return key.contains(String.valueOf(COLUMN_SEPARATOR));
	}

	private static String getTableKeyFromColumnKey(String key) {
		return key.substring(0, key.indexOf(COLUMN_SEPARATOR));
	}

	private static void prepareTableIndex(Map<String, String> indexFile, Set<Integer> tableIndex) {
		indexFile.keySet().stream()
				.filter(Index::isTableKey)
				.forEachOrdered(key -> tableIndex.add(Integer.valueOf(indexFile.get(key))));
	}

	private static void verifyTableOverflow(
			String profile,
			Map<String, Map<String, Map<String, Object>>> profileStructure) {
		if (profileStructure.keySet().size() > 10_000) {
			throw new RuntimeException("Profile [" + profile + "] contains more than 10.000 tables," +
					" which is the maximum allowed limit (required by @auto functionality).");
		}
	}

	private static void verifyColumnOverflow(
			String profile,
			String tableName,
			Map<String, Map<String, Map<String, Object>>> profileStructure) {
		if (profileStructure.get(tableName).keySet().size() > 1_000) {
			throw new RuntimeException("Table [" + profile + "]/" + tableName + " contains more than 1.000 columns," +
					" which is the maximum allowed limit (required by @auto functionality).");
		}
	}

	private static void prepareColumnIndex(
			Map<String, Map<String, Map<String, Map<String, Object>>>> structures,
			Map<String, String> indexFile,
			Map<String, Set<Integer>> columnIndex) {
		for (String profile : structures.keySet()) {
			Map<String, Map<String, Map<String, Object>>> profileStructure = structures.get(profile);
			verifyTableOverflow(profile, profileStructure);
			for (String tableName : profileStructure.keySet()) {
				verifyColumnOverflow(profile, tableName, profileStructure);
				columnIndex.put(formatTableKey(profile, tableName), new HashSet<>());
			}
		}
		indexFile.keySet().stream()
				.filter(Index::isColumnKey) // only process columns from index file
				.filter(key -> columnIndex.containsKey(getTableKeyFromColumnKey(key))) // skip columns deleted from DB
				.forEachOrdered(key ->
						columnIndex.get(getTableKeyFromColumnKey(key))
								.add(Integer.valueOf(indexFile.get(key))));
	}

	private static void updateTable(
			Map<String, String> indexFile,
			Set<Integer> tableIndex,
			String profile, String tableName,
			Map<String, String> updates) {
		String tableKey = formatTableKey(profile, tableName);
		if (indexFile.containsKey(tableKey)) {
			return;
		}
		int generated = Hash.generate(tableName, 4);
		while (tableIndex.contains(generated)) {
			generated++;
			if (generated > 9999) {
				generated = 0;
			}
		}
		tableIndex.add(generated);
		String index = StringUtils.leftPad(String.valueOf(generated), 4, '0');
		updates.put(tableKey, index);
		log.debug("Identified table  update: {}={}", tableKey, index); // align log with column
	}

	private static void updateColumn(
			Map<String, String> indexFile,
			Map<String, Set<Integer>> columnIndex,
			String profile, String tableName, String columnName,
			Map<String, String> updates) {
		String tableKey = formatTableKey(profile, tableName);
		String columnKey = formatColumnKey(profile, tableName, columnName);
		if (indexFile.containsKey(columnKey)) {
			return;
		}
		int generated = Hash.generate(columnName, 3);
		while (columnIndex.get(tableKey).contains(generated)) {
			generated++;
			if (generated > 999) {
				generated = 0;
			}
		}
		columnIndex.get(tableKey).add(generated);
		String index = StringUtils.leftPad(String.valueOf(generated), 3, '0');
		updates.put(columnKey, index);
		log.debug("Identified column update: {}={}", columnKey, index);
	}

	private static Map<String, String> getUpdates(
			Map<String, String> indexFile,
			Map<String, Map<String, Map<String, Map<String, Object>>>> structures) {
		Set<Integer> tableIndex = new HashSet<>();
		prepareTableIndex(indexFile, tableIndex);

		Map<String, Set<Integer>> columnIndex = new HashMap<>();
		prepareColumnIndex(structures, indexFile, columnIndex);

		Map<String, String> updates = new LinkedHashMap<>();
		for (String profile : structures.keySet()) {
			Map<String, Map<String, Map<String, Object>>> profileStructure = structures.get(profile);
			for (String tableName : profileStructure.keySet()) {
				updateTable(indexFile, tableIndex, profile, tableName, updates);
				Map<String, Map<String, Object>> table = profileStructure.get(tableName);
				for (String columnName : table.keySet()) {
					updateColumn(indexFile, columnIndex, profile, tableName, columnName, updates);
				}
			}
		}
		return Collections.unmodifiableMap(updates);
	}

	private static void writeUpdatesToIndexFile(File file, Map<String, String> updates) {
		if (updates.isEmpty()) {
			return;
		}
		log.debug("Writing updates to index file {}.", file.getPath());
		StringBuilder sb = new StringBuilder();
		updates.keySet().stream()
				.forEachOrdered(key -> {
					sb.append(key);
					sb.append('=');
					sb.append(updates.get(key));
					sb.append(System.lineSeparator());
				});
		try {
			FileUtils.writeStringToFile(file, sb.toString(), StandardCharsets.UTF_8, true);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write to auto index file " + file.getPath(), e);
		}
	}
}
