package net.sf.lightair.internal.unitils;

import java.lang.reflect.Method;
import java.sql.SQLException;

import net.sf.lightair.internal.dbunit.DbUnitWrapper;
import net.sf.lightair.internal.unitils.compare.DataSetAssert;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.unitils.dbunit.util.MultiSchemaDataSet;

public class UnitilsWrapper {

	public void setup(Method testMethod, String[] fileNames)
			throws ClassNotFoundException, SQLException, DatabaseUnitException {
		MultiSchemaDataSet multiSchemaDataSet = dataSetLoader.load(testMethod,
				"", fileNames);
		for (String schemaName : multiSchemaDataSet.getSchemaNames()) {
			IDataSet dataSet = multiSchemaDataSet
					.getDataSetForSchema(schemaName);
			IDatabaseConnection connection = dbUnitWrapper
					.createConnection(schemaName);
			try {
				DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
			} finally {
				connection.close();
			}
		}
	}

	public void verify(Method testMethod, String[] fileNames)
			throws ClassNotFoundException, SQLException, DatabaseUnitException {
		MultiSchemaDataSet multiSchemaDataSet = dataSetLoader.load(testMethod,
				VERIFY_FILE_NAME_SUFFIX, fileNames);
		for (String schemaName : multiSchemaDataSet.getSchemaNames()) {
			IDataSet dataSetExpected = multiSchemaDataSet
					.getDataSetForSchema(schemaName);
			IDatabaseConnection connection = dbUnitWrapper
					.createConnection(schemaName);
			try {
				IDataSet dataSetActual = connection.createDataSet();
				dataSetAssert.assertEqualDbUnitDataSets(schemaName,
						dataSetExpected, dataSetActual);
			} finally {
				connection.close();
			}
		}
	}

	private static final String VERIFY_FILE_NAME_SUFFIX = "-verify";

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

}
