package net.sf.lightair.internal.unitils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.lightair.exception.DataSetNotFoundException;
import net.sf.lightair.exception.IllegalDataSetContentException;
import net.sf.lightair.internal.util.DataSetResolver;

import org.unitils.dbunit.util.MultiSchemaDataSet;

/**
 * Loads multi-schema dataset for a test method.
 */
public class DataSetLoader {

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
	 * @param testMethod
	 *            Test method
	 * @param suffix
	 *            File name suffix
	 * @param fileNames
	 *            Explicit file names
	 * @return Multi-schema dataset
	 */
	public MultiSchemaDataSet load(Method testMethod, String suffix,
			String... fileNames) {
		try {
			List<File> files = new ArrayList<File>();
			if (fileNames.length == 0) {
				fileNames = addDefaultFile(testMethod, suffix, files);
			} else {
				addExplicitFiles(testMethod, fileNames, files);
			}
			return dataSetFactory
					.createDataSet(files.toArray(new File[] {}));
		} catch (IllegalDataSetContentException e) {
			throw new IllegalDataSetContentException(e, fileNames);
		} catch (DataSetNotFoundException e) {
			throw new DataSetNotFoundException(e, fileNames);
		}
	}

	/**
	 * Add resolved default test method file to files list.
	 * <p>
	 * Resolve default test method dataset file, if it exists. If it does not
	 * exist, resolve default class dataset file.
	 * 
	 * @param testMethod
	 *            Test method
	 * @param suffix
	 *            File name suffix
	 * @param files
	 *            Default file is added to this list
	 * @return Default dataset file name as a 1-sized array
	 */
	private String[] addDefaultFile(Method testMethod, String suffix,
			List<File> files) {
		Class<?> testClass = testMethod.getDeclaringClass();
		String dataSetName = getDefaultTestMethodFileName(testMethod,
				testClass, suffix);
		File file = dataSetResolver.resolveIfExists(testMethod, dataSetName);
		if (null == file) {
			dataSetName = getDefaultTestClassFileName(testClass, suffix);
			file = dataSetResolver.resolve(testMethod, dataSetName);
		}
		files.add(file);
		return new String[] { dataSetName };
	}

	/**
	 * Get default test method file name of the form
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
	private String getDefaultTestMethodFileName(Method testMethod,
			Class<?> testClass, String suffix) {
		return testClass.getSimpleName() + '.' + testMethod.getName() + suffix
				+ DATA_SET_FILE_EXTENSION;
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
	private String getDefaultTestClassFileName(Class<?> testClass, String suffix) {
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
			List<File> files) {
		for (String fileName : fileNames) {
			File file = dataSetResolver.resolve(testMethod, fileName);
			files.add(file);
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
