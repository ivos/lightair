package net.sf.lightair.support.unitils;

import java.lang.reflect.Method;
import java.sql.SQLException;

import net.sf.lightair.support.dbunit.DbUnitWrapper;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.unitils.dbunit.util.MultiSchemaDataSet;

public class UnitilsWrapper {

	public void setup(Method testMethod, String[] fileNames)
			throws ClassNotFoundException, SQLException, DatabaseUnitException {
		MultiSchemaDataSet dataSet = dataSetLoader.load(testMethod, "",
				fileNames);
		for (String schemaName : dataSet.getSchemaNames()) {
			IDataSet schemaDataSet = dataSet.getDataSetForSchema(schemaName);
			IDatabaseConnection connection = dbUnitWrapper
					.createConnection(schemaName);
			try {
				DatabaseOperation.CLEAN_INSERT.execute(connection,
						schemaDataSet);
			} finally {
				connection.close();
			}
		}
	}

	private DbUnitWrapper dbUnitWrapper;

	public void setDbUnitWrapper(DbUnitWrapper dbUnitWrapper) {
		this.dbUnitWrapper = dbUnitWrapper;
	}

	private DataSetLoader dataSetLoader;

	public void setDataSetLoader(DataSetLoader dataSetLoader) {
		this.dataSetLoader = dataSetLoader;
	}

}
