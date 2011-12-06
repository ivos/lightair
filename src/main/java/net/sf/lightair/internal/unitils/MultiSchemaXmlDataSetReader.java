package net.sf.lightair.internal.unitils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.SAXParserFactory;

import net.sf.lightair.exception.DataSetNotFoundException;
import net.sf.lightair.exception.IllegalDataSetContentException;
import net.sf.lightair.exception.XmlParserException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.dbunit.util.MultiSchemaDataSet;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Reads multi-schema datasets from a set of XML files in DbUnit's "flat XML"
 * format.
 */
public class MultiSchemaXmlDataSetReader {

	private final Logger log = LoggerFactory
			.getLogger(MultiSchemaXmlDataSetReader.class);

	/**
	 * Read multi-schema datasets from a set of XML files in DbUnit's "flat XML"
	 * format.
	 * 
	 * @param defaultSchemaName
	 *            Default database schema name.
	 * @param dataSetFiles
	 *            Files with datasets
	 * @return Multi-schema dataset
	 * @throws DataSetNotFoundException
	 *             When a dataset file cannot be read
	 * @throws IllegalDataSetContentException
	 *             When a dataset file cannot be parsed
	 */
	public MultiSchemaDataSet readDataSetXml(String defaultSchemaName,
			File... dataSetFiles) throws DataSetNotFoundException,
			IllegalDataSetContentException {
		log.debug("Reading dataset with default schema {} and files {}.",
				defaultSchemaName, dataSetFiles);
		DataSetContentHandler dataSetContentHandler = new DataSetContentHandler(
				defaultSchemaName);
		XMLReader xmlReader = createXMLReader();
		xmlReader.setContentHandler(dataSetContentHandler);
		xmlReader.setErrorHandler(dataSetContentHandler);
		for (File dataSetFile : dataSetFiles) {
			log.debug("Reading XML dataset file {}.", dataSetFile);
			InputStream dataSetInputStream = null;
			try {
				dataSetInputStream = new FileInputStream(dataSetFile);
				xmlReader.parse(new InputSource(dataSetInputStream));
			} catch (IOException e) {
				throw new DataSetNotFoundException(e, dataSetFile.getPath());
			} catch (SAXException e) {
				throw new IllegalDataSetContentException(e,
						dataSetFile.getPath());
			} finally {
				IOUtils.closeQuietly(dataSetInputStream);
			}
		}
		return dataSetContentHandler.getMultiSchemaDataSet();
	}

	private XMLReader createXMLReader() {
		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setNamespaceAware(true);
			disableValidation(saxParserFactory);
			return saxParserFactory.newSAXParser().getXMLReader();
		} catch (Exception e) {
			throw new XmlParserException(e);
		}
	}

	/**
	 * Disables validation on the given sax parser factory, so data set can
	 * still be used when a DTD or XSD is missing.
	 * 
	 * @param saxParserFactory
	 *            The factory, not null
	 */
	private void disableValidation(SAXParserFactory saxParserFactory) {
		saxParserFactory.setValidating(false);
		try {
			saxParserFactory.setFeature(EXTERNAL_PARAMETER_ENTITIES, false);
		} catch (Exception e) {
			log.debug("Unable to set "
					+ EXTERNAL_PARAMETER_ENTITIES
					+ " feature on SAX parser factory to false. Igoring exception: "
					+ e.getMessage());
		}
		try {
			saxParserFactory.setFeature(LOAD_EXTERNAL_DTD, false);
		} catch (Exception e) {
			log.debug("Unable to set "
					+ LOAD_EXTERNAL_DTD
					+ " feature on SAX parser factory to false. Igoring exception: "
					+ e.getMessage());
		}
	}

	private static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
	private static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

}
