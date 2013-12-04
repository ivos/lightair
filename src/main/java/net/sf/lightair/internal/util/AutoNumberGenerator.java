package net.sf.lightair.internal.util;

public class AutoNumberGenerator {

	private HashGenerator hashGenerator;

	public int generateAutoNumber(String tableName, String columnName,
			int rowIndex) {
		final int tableHash = hashGenerator.generateHash(tableName, 3);
		final int columnHash = hashGenerator.generateHash(columnName, 2);
		final int autoNumber = tableHash * 10000 + columnHash * 100 + rowIndex;
		return autoNumber;
	}

	public void setHashGenerator(HashGenerator hashGenerator) {
		this.hashGenerator = hashGenerator;
	}

}
