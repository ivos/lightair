package net.sf.lightair.internal.unitils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.lightair.exception.IllegalDataSetContentException;
import net.sf.lightair.internal.util.DataSetResolver;

import org.unitils.core.UnitilsException;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.util.MultiSchemaDataSet;

public class DataSetLoader {

	private static final String DATA_SET_FILE_EXTENSION = ".xml";

	public MultiSchemaDataSet load(Method testMethod, String suffix,
			String... fileNames) {
		List<File> files = new ArrayList<File>();
		if (fileNames.length == 0) {
			Class<?> testClass = testMethod.getDeclaringClass();
			String dataSetName = testClass.getSimpleName() + '.'
					+ testMethod.getName() + suffix + DATA_SET_FILE_EXTENSION;
			File file = dataSetResolver
					.resolveIfExists(testMethod, dataSetName);
			if (null == file) {
				dataSetName = testClass.getSimpleName() + suffix
						+ DATA_SET_FILE_EXTENSION;
				file = dataSetResolver.resolve(testMethod, dataSetName);
			}
			fileNames = new String[] { dataSetName };
			files.add(file);
		} else {
			for (String fileName : fileNames) {
				File file = dataSetResolver.resolve(testMethod, fileName);
				files.add(file);
			}
		}
		try {
			return dataSetFactory.createDataSet(files.toArray(new File[] {}));
		} catch (UnitilsException cause) {
			throw new IllegalDataSetContentException(cause, fileNames);
		}
	}

	private DataSetResolver dataSetResolver;

	public void setDataSetResolver(DataSetResolver dataSetResolver) {
		this.dataSetResolver = dataSetResolver;
	}

	private DataSetFactory dataSetFactory;

	public void setDataSetFactory(DataSetFactory dataSetFactory) {
		this.dataSetFactory = dataSetFactory;
	}

}
