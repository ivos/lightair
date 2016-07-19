package net.sf.lightair.internal.unitils;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.lightair.exception.DataSetNotFoundException;
import net.sf.lightair.exception.IllegalDataSetContentException;
import net.sf.lightair.internal.util.DataSetResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.dbunit.util.MultiSchemaDataSet;

/**
 * Loads multi-schema dataset for a test method.
 */
public class DataSetLoader {

	private final Logger log = LoggerFactory.getLogger(DataSetLoader.class);

	private static final String DATA_SET_FILE_EXTENSION = ".xml";

	/**
	 * Load multi-schema dataset for a test method.
	 * <p>
	 * If no explicit file names specified, load either from default test method
	 * file (i.e. [TestClassName].[testMethodName][suffix].xml) if it exists, or
	 * from default test class name (i.e. [TestClassName][suffix].xml).
	 * <p>
	 * If explicit file names are specified, load from all those files and
	 * merge.
	 * <p>
	 * Wrap datasets in loaded multi-schema dataset with replacement wrapper
	 * replacing common replacements, see {@link ReplacementDataSetWrapper}.
	 *
	 * @param profile
	 *            Profile
	 * @param testMethod
	 *            Test method
	 * @param suffix
	 *            File name suffix
	 * @param fileNames
	 *            Explicit file names
	 * @return Multi-schema dataset
	 */
	public MultiSchemaDataSet load(String profile, Method testMethod, String suffix, String... fileNames) {
		List<URL> resources = dataSetResolver.resolve(profile, testMethod, suffix, fileNames);
		log.debug("Creating dataset for resolved resources {}.", resources);
		return dataSetFactory.createDataSet(profile, resources.toArray(new URL[] {}));
	}

	/**
	 * Add resolved default test method file to resources list.
	 * <p>
	 * Resolve default test method dataset file, if it exists. If it does not
	 * exist, resolve default class dataset file.
	 *
	 * @param testMethod
	 *            Test method
	 * @param suffix
	 *            File name suffix
	 * @param resources
	 *            Default file is added to this list
	 * @return Default dataset file name as a 1-sized array
	 */
	private String[] addDefaultFile(Method testMethod, String suffix,
			List<URL> resources) {
		Class<?> testClass = testMethod.getDeclaringClass();
		String dataSetName = getDefaultClassMethodFileName(testMethod,
				testClass, suffix);
		URL url = dataSetResolver.resolveIfExists(testMethod, dataSetName);
		if (null == url) {
			dataSetName = getDefaultMethodFileName(testMethod, suffix);
			url = dataSetResolver.resolveIfExists(testMethod, dataSetName);
			if (null == url) {
				dataSetName = getDefaultClassFileName(testClass, suffix);
				url = dataSetResolver.resolve(testMethod, dataSetName);
			}
		}
		resources.add(url);
		return new String[] { dataSetName };
	}

	/**
	 * Get default test class and test method file name of the form
	 * <code>[TestClassName].[testMethodName][suffix].xml</code>.
	 *
	 * @param testMethod
	 *            Test method
	 * @param testClass
	 *            Test class
	 * @param suffix
	 *            Suffix
	 * @return Default test method file name
	 */
	private String getDefaultClassMethodFileName(Method testMethod,
			Class<?> testClass, String suffix) {
		return testClass.getSimpleName() + '.' + testMethod.getName() + suffix
				+ DATA_SET_FILE_EXTENSION;
	}

	/**
	 * Get default test method file name of the form
	 * <code>[TestClassName].[testMethodName][suffix].xml</code>.
	 *
	 * @param testMethod
	 *            Test method
	 * @param suffix
	 *            Suffix
	 * @return Default test method file name
	 */
	private String getDefaultMethodFileName(Method testMethod, String suffix) {
		return testMethod.getName() + suffix + DATA_SET_FILE_EXTENSION;
	}

	/**
	 * Get default test class file name of the form
	 * <code>[TestClassName][suffix].xml</code>.
	 *
	 * @param testClass
	 *            Test class
	 * @param suffix
	 *            Suffix
	 * @return Default test class file name
	 */
	private String getDefaultClassFileName(Class<?> testClass, String suffix) {
		return testClass.getSimpleName() + suffix + DATA_SET_FILE_EXTENSION;
	}

	/**
	 * Add resolved explicit files to files list.
	 *
	 * @param testMethod
	 *            Test method
	 * @param fileNames
	 *            Names of explicit files
	 * @param files
	 *            Resolved explicit files are added to this list
	 */
	private void addExplicitFiles(Method testMethod, String[] fileNames,
			List<URL> files) {
		for (String fileName : fileNames) {
			URL resource = dataSetResolver.resolve(testMethod, fileName);
			files.add(resource);
		}
	}

	// dependencies and setters

	private DataSetResolver dataSetResolver;

	/**
	 * Set dataset resolver
	 *
	 * @param dataSetResolver
	 */
	public void setDataSetResolver(DataSetResolver dataSetResolver) {
		this.dataSetResolver = dataSetResolver;
	}

	private DataSetFactory dataSetFactory;

	/**
	 * Set dataset factory.
	 *
	 * @param dataSetFactory
	 */
	public void setDataSetFactory(DataSetFactory dataSetFactory) {
		this.dataSetFactory = dataSetFactory;
	}

}
