package net.sf.lightair.support.dbunit;

import java.lang.reflect.Method;

import net.sf.lightair.exception.DataSetNotFoundException;

import org.dbunit.dataset.IDataSet;

/**
 * Loads data set for a test method.
 */
public class DataSetLoader {

	private static final String DATA_SET_FILE_EXTENSION = ".xml";

	/**
	 * Load data set for a test method.
	 * <p>
	 * Load by method name. If not found, load by class name. If not found,
	 * throw DataSetNotFoundException.
	 * 
	 * @param testMethod
	 *            Test method
	 * @param suffix
	 *            File name suffix
	 * @return Data set configured for the test method
	 * @throws DataSetNotFoundException
	 *             When configured data set is not found
	 */
	public IDataSet loadDataSet(Method testMethod, String suffix)
			throws DataSetNotFoundException {
		Class<?> testClass = testMethod.getDeclaringClass();
		String classDataSetName = testClass.getSimpleName() + suffix
				+ DATA_SET_FILE_EXTENSION;
		String methodDataSetName = testClass.getSimpleName() + '.'
				+ testMethod.getName() + suffix + DATA_SET_FILE_EXTENSION;

		IDataSet dataSet = dbUnit.loadDataSetIfExists(testClass,
				methodDataSetName);
		if (null == dataSet) {
			dataSet = dbUnit.loadDataSetIfExists(testClass, classDataSetName);
		}

		if (null == dataSet) {
			throw new DataSetNotFoundException(classDataSetName,
					methodDataSetName);
		}
		return dataSet;
	}

	private DbUnitWrapper dbUnit;

	public void setDbUnit(DbUnitWrapper dbUnit) {
		this.dbUnit = dbUnit;
	}

}
