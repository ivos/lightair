package net.sf.lightair.support.dbunit;

import java.lang.reflect.Method;

import net.sf.lightair.exception.DataSetNotFoundException;

import org.dbunit.dataset.IDataSet;

/**
 * Loads data set for a test method.
 */
public class DataSetLoader {

	/**
	 * Load data set for a test method.
	 * <p>
	 * Load by method name. If not found, load by class name. If not found,
	 * throw DataSetNotFoundException.
	 * 
	 * @param testMethod
	 *            Test method
	 * @return Data set configured for the test method
	 * @throws DataSetNotFoundException
	 *             When configured data set is not found
	 */
	public IDataSet loadDataSet(Method testMethod)
			throws DataSetNotFoundException {
		Class<?> testClass = testMethod.getDeclaringClass();
		String classDataSetName = testClass.getSimpleName() + "-verify.xml";
		String methodDataSetName = testClass.getSimpleName() + '.'
				+ testMethod.getName() + "-verify.xml";

		IDataSet dataSet = getDbUnit().loadDataSetIfExists(testClass,
				methodDataSetName);
		if (null == dataSet) {
			dataSet = getDbUnit().loadDataSetIfExists(testClass,
					classDataSetName);
		}

		if (null == dataSet) {
			throw new DataSetNotFoundException(classDataSetName,
					methodDataSetName);
		}
		return dataSet;
	}

	private DbUnitWrapper dbUnit;

	public DbUnitWrapper getDbUnit() {
		if (null == dbUnit) {
			dbUnit = new DbUnitWrapper();
		}
		return dbUnit;
	}

	public void setDbUnit(DbUnitWrapper dbUnit) {
		this.dbUnit = dbUnit;
	}

}
