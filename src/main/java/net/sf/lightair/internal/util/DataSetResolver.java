package net.sf.lightair.internal.util;

import java.io.File;
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
	public File resolve(Method testMethod, String fileName)
			throws DataSetNotFoundException {
		File file = resolveIfExists(testMethod, fileName);
		if (null == file) {
			throw new DataSetNotFoundException(fileName);
		}
		return file;
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
	public File resolveIfExists(Method testMethod, String fileName) {
		Class<?> testClass = testMethod.getDeclaringClass();
		URL url = testClass.getResource(fileName);

		if (null == url) {
			return null;
		}
		return new File(url.getFile());
	}

}
