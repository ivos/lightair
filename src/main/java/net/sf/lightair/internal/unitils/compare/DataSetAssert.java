package net.sf.lightair.internal.unitils.compare;

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.unitils.core.util.ObjectFormatter;
import org.unitils.dbunit.dataset.Column;
import org.unitils.dbunit.dataset.Row;
import org.unitils.dbunit.dataset.Schema;

/**
 * Fork of Unitils DataSetAssert to enable customizations.
 */
public class DataSetAssert extends org.unitils.dbunit.util.DataSetAssert {

	// Extracted to use own SchemaFactory

	/**
	 * Asserts that the given expected DbUnit dataset is equal to the actual
	 * DbUnit dataset.
	 * <p>
	 * Tables or columns that are not specified in the expected dataset will be
	 * ignored. If an empty table is specified in the expected dataset, it will
	 * check that the actual table is empty too.
	 * 
	 * @param schemaName
	 *            The name of the schema that these datasets belong to, not null
	 * @param expectedDataSet
	 *            The expected dataset, not null
	 * @param actualDataSet
	 *            The actual dataset, not null
	 * @throws AssertionError
	 *             When the assertion fails.
	 */
	@Override
	public void assertEqualDbUnitDataSets(String schemaName,
			IDataSet expectedDataSet, IDataSet actualDataSet) {

		// Use own SchemaFactory:
		SchemaFactory dbUnitDataSetBuilder = createSchemaFactory();

		Schema expectedSchema = dbUnitDataSetBuilder
				.createSchemaForDbUnitDataSet(schemaName, expectedDataSet);

		List<String> expectedTableNames = expectedSchema.getTableNames();
		Schema actualSchema = dbUnitDataSetBuilder
				.createSchemaForDbUnitDataSet(schemaName, actualDataSet,
						expectedTableNames);

		assertEqualSchemas(expectedSchema, actualSchema);
	}

	/**
	 * Instantiate SchemaFactory.
	 * 
	 * @return New SchemaFactory instance
	 */
	protected SchemaFactory createSchemaFactory() {
		return new SchemaFactory();
	}

	// Extracted to report unexpected rows

	/**
	 * Appends missing and unexpected rows of the given table difference to the
	 * result.
	 * 
	 * @param tableDifference
	 *            The difference, not null
	 * @param result
	 *            The result to append to, not null
	 */
	@Override
	protected void appendMissingRowDifferences(
			org.unitils.dbunit.dataset.comparison.TableDifference tableDifference,
			StringBuilder result) {
		super.appendMissingRowDifferences(tableDifference, result);

		// Report unexpected rows:
		TableDifference ownTableDifference = (TableDifference) tableDifference;
		for (Row unexpectedRow : ownTableDifference.getUnexpectedRows()) {
			result.append("\n  Unexpected row:\n  ");
			appendColumnNames(unexpectedRow, result);
			result.append("\n  ");
			appendRow(unexpectedRow, result);
			result.append("\n");
		}
	}

	// Extracted to include primary keys in verification failure message

	@Override
	protected void appendColumnNames(Row row, StringBuilder result) {
		for (Column column : row.getPrimaryKeyColumns()) {
			result.append(column.getName());
			result.append(", ");
		}
		super.appendColumnNames(row, result);
	}

	@Override
	protected void appendRow(Row row, StringBuilder result) {
		for (Column column : row.getPrimaryKeyColumns()) {
			result.append(objectFormatter.format(column.getValue()));
			result.append(", ");
		}
		super.appendRow(row, result);
	}

	private final ObjectFormatter objectFormatter = new ObjectFormatter();

}
