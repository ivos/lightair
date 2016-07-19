package net.sf.lightair.internal.util;

import net.sf.lightair.exception.DataSetNotFoundException;
import net.sf.lightair.exception.IllegalDataSetContentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Resolves dataset files by their file names relative to the class declaring
 * the test method.
 */
public class DataSetResolver {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final String DATA_SET_FILE_EXTENSION = ".xml";

	public List<URL> resolve(String profile, Method testMethod, String suffix, String... fileNames) {
		log.debug("Resolving dataset for method {} with suffix [{}] and configured file names {}.",
				testMethod, suffix, fileNames);
		try {
			List<URL> resources = new ArrayList<URL>();
			if (fileNames.length == 0) {
				fileNames = addDefaultFile(testMethod, suffix, resources);
			} else {
				addExplicitFiles(testMethod, fileNames, resources);
			}
			return resources;
		} catch (IllegalDataSetContentException e) {
			throw new IllegalDataSetContentException(e, fileNames);
		} catch (DataSetNotFoundException e) {
			throw new DataSetNotFoundException(e, fileNames);
		}
	}

	/**
	 * Get default test class and test method file name of the form
	 * <code>[TestClassName].[testMethodName][suffix].xml</code>.
	 *
	 * @param testMethod Test method
	 * @param testClass  Test class
	 * @param suffix     Suffix
	 * @return Default test method file name
	 */
	private String getDefaultClassMethodFileName(Method testMethod, Class<?> testClass, String suffix) {
		return testClass.getSimpleName() + '.' + testMethod.getName() + suffix
				+ DATA_SET_FILE_EXTENSION;
	}

	/**
	 * Get default test method file name of the form
	 * <code>[TestClassName].[testMethodName][suffix].xml</code>.
	 *
	 * @param testMethod Test method
	 * @param suffix     Suffix
	 * @return Default test method file name
	 */
	private String getDefaultMethodFileName(Method testMethod, String suffix) {
		return testMethod.getName() + suffix + DATA_SET_FILE_EXTENSION;
	}

	/**
	 * Get default test class file name of the form
	 * <code>[TestClassName][suffix].xml</code>.
	 *
	 * @param testClass Test class
	 * @param suffix    Suffix
	 * @return Default test class file name
	 */
	private String getDefaultClassFileName(Class<?> testClass, String suffix) {
		return testClass.getSimpleName() + suffix + DATA_SET_FILE_EXTENSION;
	}

	/**
	 * Add resolved default test method file to resources list.
	 * <p>
	 * Resolve default test method dataset file, if it exists. If it does not
	 * exist, resolve default class dataset file.
	 *
	 * @param testMethod Test method
	 * @param suffix     File name suffix
	 * @param resources  Default file is added to this list
	 * @return Default dataset file name as a 1-sized array
	 */
	private String[] addDefaultFile(Method testMethod, String suffix, List<URL> resources) {
		Class<?> testClass = testMethod.getDeclaringClass();
		String dataSetName = getDefaultClassMethodFileName(testMethod,
				testClass, suffix);
		URL url = resolveIfExists(testMethod, dataSetName);
		if (null == url) {
			dataSetName = getDefaultMethodFileName(testMethod, suffix);
			url = resolveIfExists(testMethod, dataSetName);
			if (null == url) {
				dataSetName = getDefaultClassFileName(testClass, suffix);
				url = resolve(testMethod, dataSetName);
			}
		}
		resources.add(url);
		return new String[]{dataSetName};
	}

	/**
	 * Add resolved explicit files to files list.
	 *
	 * @param testMethod Test method
	 * @param fileNames  Names of explicit files
	 * @param files      Resolved explicit files are added to this list
	 */
	private void addExplicitFiles(Method testMethod, String[] fileNames, List<URL> files) {
		for (String fileName : fileNames) {
			URL resource = resolve(testMethod, fileName);
			files.add(resource);
		}
	}

	/**
	 * Resolve a file by its name relative to a class declaring the test method.
	 *
	 * @param testMethod Test method
	 * @param fileName   File name to resolve
	 * @return Resolved file
	 * @throws DataSetNotFoundException when no such file exists
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
	 * @param testMethod Test method
	 * @param fileName   File name to resolve
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
