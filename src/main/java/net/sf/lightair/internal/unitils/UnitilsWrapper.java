package net.sf.lightair.internal.unitils;

import java.lang.reflect.Method;
import java.sql.SQLException;

import net.sf.lightair.exception.CloseDatabaseConnectionException;
import net.sf.lightair.exception.DatabaseAccessException;
import net.sf.lightair.internal.dbunit.DbUnitWrapper;
import net.sf.lightair.internal.factory.Factory;
import net.sf.lightair.internal.unitils.compare.DataSetAssert;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.unitils.dbunit.util.MultiSchemaDataSet;

/**
 * Wrapper around Unitils.
 */
public class UnitilsWrapper {

	/**
	 * Setup database for test method.
	 * 
	 * @param testMethod
	 *            Test method
	 * @param fileNames
	 *            File names from @Setup annotation
	 */
	public void setup(Method testMethod, String[] fileNames) {
		MultiSchemaDataSet multiSchemaDataSet = dataSetLoader.load(testMethod,
				"", fileNames);
		final DatabaseOperation cleanInsert = factory
				.getCleanInsertDatabaseOperation();
		for (String schemaName : multiSchemaDataSet.getSchemaNames()) {
			final IDataSet dataSet = multiSchemaDataSet
					.getDataSetForSchema(schemaName);
			final IDatabaseConnection connection = dbUnitWrapper
					.createConnection(schemaName);
			new Template() {
				@Override
				void databaseOperation() throws DatabaseUnitException,
						SQLException {
					cleanInsert.execute(connection, dataSet);
				}
			}.execute(connection);
		}
	}

	/**
	 * Verify database after test method.
	 * 
	 * @param testMethod
	 *            Test method
	 * @param fileNames
	 *            File names from @Setup annotation
	 */
	public void verify(Method testMethod, String[] fileNames)
			throws SQLException {
		MultiSchemaDataSet multiSchemaDataSet = dataSetLoader.load(testMethod,
				VERIFY_FILE_NAME_SUFFIX, fileNames);
		for (final String schemaName : multiSchemaDataSet.getSchemaNames()) {
			final IDataSet dataSetExpected = multiSchemaDataSet
					.getDataSetForSchema(schemaName);
			final IDatabaseConnection connection = dbUnitWrapper
					.createConnection(schemaName);
			new Template() {
				@Override
				void databaseOperation() throws DatabaseUnitException,
						SQLException {
					IDataSet dataSetActual = connection.createDataSet();
					dataSetAssert.assertEqualDbUnitDataSets(schemaName,
							dataSetExpected, dataSetActual);
				}
			}.execute(connection);
		}
	}

	private static final String VERIFY_FILE_NAME_SUFFIX = "-verify";

	/**
	 * Database operation template. Closes database connection and translates
	 * exceptions.
	 */
	private abstract class Template {
		abstract void databaseOperation() throws DatabaseUnitException,
				SQLException;

		void execute(IDatabaseConnection connection) {
			try {
				try {
					databaseOperation();
				} catch (DatabaseUnitException e) {
					throw new DatabaseAccessException(e);
				} catch (SQLException e) {
					throw new DatabaseAccessException(e);
				} finally {
					connection.close();
				}
			} catch (SQLException e) {
				throw new CloseDatabaseConnectionException(e);
			}
		}
	}

	// beans and their setters

	private DbUnitWrapper dbUnitWrapper;

	public void setDbUnitWrapper(DbUnitWrapper dbUnitWrapper) {
		this.dbUnitWrapper = dbUnitWrapper;
	}

	private DataSetLoader dataSetLoader;

	public void setDataSetLoader(DataSetLoader dataSetLoader) {
		this.dataSetLoader = dataSetLoader;
	}

	private DataSetAssert dataSetAssert;

	public void setDataSetAssert(DataSetAssert dataSetAssert) {
		this.dataSetAssert = dataSetAssert;
	}

	private Factory factory;

	public void setFactory(Factory factory) {
		this.factory = factory;
	}

}
