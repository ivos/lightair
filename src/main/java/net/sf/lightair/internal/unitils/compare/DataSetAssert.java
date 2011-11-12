package net.sf.lightair.internal.unitils.compare;

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.unitils.dbunit.dataset.Schema;

/**
 * Fork/wrapper of Unitils DataSetAssert to enable using own SchemaFactory.
 * <p>
 * Delegates to Unitils DataSetAssert for the actual comparison work.
 */
public class DataSetAssert {

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
	public void assertEqualDbUnitDataSets(String schemaName,
			IDataSet expectedDataSet, IDataSet actualDataSet) {
		SchemaFactory dbUnitDataSetBuilder = createSchemaFactory();
		Schema expectedSchema = dbUnitDataSetBuilder
				.createSchemaForDbUnitDataSet(schemaName, expectedDataSet);

		List<String> expectedTableNames = expectedSchema.getTableNames();
		Schema actualSchema = dbUnitDataSetBuilder
				.createSchemaForDbUnitDataSet(schemaName, actualDataSet,
						expectedTableNames);

		dataSetAssert.assertEqualSchemas(expectedSchema, actualSchema);
	}

	/**
	 * Instantiate SchemaFactory.
	 * 
	 * @return New SchemaFactory instance
	 */
	protected SchemaFactory createSchemaFactory() {
		return new SchemaFactory();
	}

	private org.unitils.dbunit.util.DataSetAssert dataSetAssert;

	/**
	 * Set Unitils DataSetAssert.
	 * 
	 * @param dataSetAssert
	 */
	public void setDataSetAssert(
			org.unitils.dbunit.util.DataSetAssert dataSetAssert) {
		this.dataSetAssert = dataSetAssert;
	}

}
