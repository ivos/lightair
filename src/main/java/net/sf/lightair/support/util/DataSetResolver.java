package net.sf.lightair.support.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;

import net.sf.lightair.exception.DataSetNotFoundException;

public class DataSetResolver {

	public File resolve(Method testMethod, String fileName)
			throws DataSetNotFoundException {
		File file = resolveIfExists(testMethod, fileName);
		if (null == file) {
			throw new DataSetNotFoundException(fileName);
		}
		return file;
	}

	public File resolveIfExists(Method testMethod, String fileName) {
		Class<?> testClass = testMethod.getDeclaringClass();
		URL url = testClass.getResource(fileName);

		if (null == url) {
			return null;
		}
		return new File(url.getFile());
	}

}
