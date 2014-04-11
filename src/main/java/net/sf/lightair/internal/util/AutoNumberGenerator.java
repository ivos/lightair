package net.sf.lightair.internal.util;

import java.util.HashMap;
import java.util.Map;

import net.sf.lightair.exception.AutoValueColumnOverflowException;
import net.sf.lightair.exception.AutoValueTableOverflowException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates <code>@auto</code> number.
 */
public class AutoNumberGenerator {

	private final Logger log = LoggerFactory
			.getLogger(AutoNumberGenerator.class);

	private HashGenerator hashGenerator;

	private final Map<Integer, String> tables = new HashMap<Integer, String>();

	private final Map<String, Map<Integer, String>> columns = new HashMap<String, Map<Integer, String>>();

	/**
	 * Generate an <code>@auto</code> number from table name, column name and
	 * row index.
	 * <p>
	 * Generates hash for the table name and hash for the column name.
	 * <p>
	 * Make table hash unique by incrementing it when necessary.
	 * <p>
	 * Make column hash unique by incrementing it when necessary.
	 * <p>
	 * Formats the number as <code>TTTCCRR</code>, where <code>TTT</code> is
	 * table name hash, <code>CC</code> is column name hash and <code>RR</code>
	 * is row index.
	 * 
	 * @param tableName
	 * @param columnName
	 * @param rowIndex
	 * @return
	 */
	public int generateAutoNumber(String tableName, String columnName,
			int rowIndex) {
		int tableHash = getTableHash(tableName);
		int columnHash = getColumnHash(tableName, columnName);
		int autoNumber = tableHash * 10000 + columnHash * 100 + rowIndex;
		return autoNumber;
	}

	private int getTableHash(String tableName) {
		if (tables.size() > 999) {
			throw new AutoValueTableOverflowException(tableName);
		}
		int tableHash = hashGenerator.generateHash(tableName, 3);
		log.trace("Generated table hash {} for table {}.", tableHash, tableName);
		String storedTableName = tables.get(tableHash);
		while (null != storedTableName && !tableName.equals(storedTableName)) {
			tableHash++;
			if (tableHash > 999) {
				tableHash -= 1000;
			}
			log.trace(
					"Table hash conflicted with table {}, incrementing to {}.",
					storedTableName, tableHash);
			storedTableName = tables.get(tableHash);
		}
		if (!tableName.equals(storedTableName)) {
			tables.put(tableHash, tableName);
		}
		return tableHash;
	}

	private int getColumnHash(String tableName, String columnName) {
		int columnHash = hashGenerator.generateHash(columnName, 2);
		log.trace("Generated column hash {} for column {}.{}.", columnHash,
				tableName, columnName);
		Map<Integer, String> tableColumns = columns.get(tableName);
		if (null == tableColumns) {
			tableColumns = new HashMap<Integer, String>();
			columns.put(tableName, tableColumns);
		}
		if (tableColumns.size() > 99) {
			throw new AutoValueColumnOverflowException(tableName);
		}
		String storedColumnName = tableColumns.get(columnHash);
		while (null != storedColumnName && !columnName.equals(storedColumnName)) {
			columnHash++;
			if (columnHash > 99) {
				columnHash -= 100;
			}
			log.trace(
					"Column hash conflicted with column {}, incrementing to {}.",
					storedColumnName, columnHash);
			storedColumnName = tableColumns.get(columnHash);
		}
		if (!columnName.equals(storedColumnName)) {
			tableColumns.put(columnHash, columnName);
		}
		return columnHash;
	}

	// bean setters

	public void setHashGenerator(HashGenerator hashGenerator) {
		this.hashGenerator = hashGenerator;
	}

}
