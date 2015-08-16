package net.sf.lightair.internal.util;

import java.lang.reflect.Method;
import java.net.URL;

import net.sf.lightair.exception.DataSetNotFoundException;

/**
 * Resolves dataset files by their file names relative to the class declaring
 * the test method.
 */
public class DataSetResolver {

	/**
	 * Resolve a file by its name relative to a class declaring the test method.
	 * 
	 * @param testMethod
	 *            Test method
	 * @param fileName
	 *            File name to resolve
	 * @return Resolved file
	 * @throws DataSetNotFoundException
	 *             when no such file exists
	 */
	public URL resolve(Method testMethod, String fileName)
			throws DataSetNotFoundException {
		URL url = resolveIfExists(testMethod, fileName);
		if (null == url) {
			throw new DataSetNotFoundException(fileName);
		}
		return url;
	}

	/**
	 * Resolve a file by its name relative to a class declaring the test method.
	 * 
	 * @param testMethod
	 *            Test method
	 * @param fileName
	 *            File name to resolve
	 * @return Resolved file or null if no such file exists
	 */
	public URL resolveIfExists(Method testMethod, String fileName) {
		Class<?> testClass = testMethod.getDeclaringClass();
		URL url = testClass.getResource(fileName);

		if (null == url) {
			return null;
		}
		return url;
	}

}
