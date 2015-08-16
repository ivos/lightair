package net.sf.lightair.internal.unitils;

import java.net.URL;

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
	 * @param profile
	 *            Profile
	 * @param dataSetFiles
	 *            Dataset files
	 * @return Multi-schema dataset
	 */
	public MultiSchemaDataSet createDataSet(String profile, URL... dataSetFiles) {
		MultiSchemaXmlDataSetReader multiSchemaXmlDataSetReader = createMultiSchemaXmlDataSetReader();
		return multiSchemaXmlDataSetReader.readDataSetXml(
				propertiesProvider.getProperty(profile, DEFAULT_SCHEMA),
				dataSetFiles);
	}

	/**
	 * Instantiate new {@link MultiSchemaXmlDataSetReader}.
	 * 
	 * @return New MultiSchemaXmlDataSetReader
	 */
	protected MultiSchemaXmlDataSetReader createMultiSchemaXmlDataSetReader() {
		return new MultiSchemaXmlDataSetReader();
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
