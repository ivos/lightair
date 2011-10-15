package net.sf.lightair.internal.unitils;

import java.io.File;

import net.sf.lightair.internal.properties.PropertiesProvider;
import net.sf.lightair.internal.properties.PropertyKeys;

import org.unitils.dbunit.util.MultiSchemaDataSet;

/**
 * Factory producing multi-schema datasets.
 */
public class DataSetFactory implements PropertyKeys {

	/**
	 * Create multi-schema dataset by parsing a set of XML dataset files.
	 * 
	 * @param dataSetFiles
	 *            Dataset files
	 * @return Multi-schema dataset
	 */
	public MultiSchemaDataSet createDataSet(File... dataSetFiles) {
		MultiSchemaXmlDataSetReader multiSchemaXmlDataSetReader = new MultiSchemaXmlDataSetReader();
		return multiSchemaXmlDataSetReader.readDataSetXml(
				propertiesProvider.getProperty(DEFAULT_SCHEMA), dataSetFiles);
	}

	// dependencies and setters

	private PropertiesProvider propertiesProvider;

	/**
	 * Set properties provider.
	 * 
	 * @param propertiesProvider
	 *            Properties provider
	 */
	public void setPropertiesProvider(PropertiesProvider propertiesProvider) {
		this.propertiesProvider = propertiesProvider;
	}

}
