package net.sf.lightair.internal.util;

/**
 * Generates <code>@auto</code> number.
 */
public class AutoNumberGenerator {

	private HashGenerator hashGenerator;

	/**
	 * Generate an <code>@auto</code> number from table name, column name and
	 * row index.
	 * <p>
	 * Generates hash for the table name and hash for the column name.
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
		final int tableHash = hashGenerator.generateHash(tableName, 3);
		final int columnHash = hashGenerator.generateHash(columnName, 2);
		final int autoNumber = tableHash * 10000 + columnHash * 100 + rowIndex;
		return autoNumber;
	}

	// bean setters

	public void setHashGenerator(HashGenerator hashGenerator) {
		this.hashGenerator = hashGenerator;
	}

}
