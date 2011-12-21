package net.sf.lightair.internal.unitils;

import java.util.HashMap;
import java.util.Map;

import net.sf.lightair.internal.dbunit.dataset.FlatXmlDataSet;
import net.sf.lightair.internal.dbunit.dataset.MutableTableMetaData;

import org.apache.commons.lang.StringUtils;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.datatype.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.dbunit.util.MultiSchemaDataSet;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX handler to parse {@link FlatXmlDataSet}.
 */
public class DataSetContentHandler extends DefaultHandler {

	private final Logger log = LoggerFactory
			.getLogger(DataSetContentHandler.class);

	private String defaultSchemaName;
	private final Map<String, FlatXmlDataSet> dataSets = new HashMap<String, FlatXmlDataSet>();

	/**
	 * Default constructor.
	 * 
	 * @param defaultSchemaName
	 *            Database schema to assign to XML rows without namespace
	 */
	public DataSetContentHandler(String defaultSchemaName) {
		this.defaultSchemaName = defaultSchemaName;
	}

	@Override
	public void startElement(String namespace, String localName, String qName,
			Attributes attributes) {
		log.debug("Processing XML element {}:{}.", namespace, localName);
		if (processDatasetElement(namespace, localName)) {
			return;
		}
		FlatXmlDataSet dataSet = startDataset(namespace);
		MutableTableMetaData metaData = startRow(dataSet, localName, attributes);
		setRowValues(dataSet, metaData, attributes);
		dataSet.endRow();
	}

	/**
	 * Process the root &lt;dataset /> XML element.
	 * <p>
	 * Reads default schema name from its namespace, if present.
	 * 
	 * @param namespace
	 *            XML attribute namespace
	 * @param localName
	 *            XML attribute local name
	 * @return true iff this is dataset element
	 */
	private boolean processDatasetElement(String namespace, String localName) {
		if (isDatasetElement(localName)) {
			if (hasNamespace(namespace)) {
				defaultSchemaName = namespace;
			}
			return true;
		}
		return false;
	}

	/**
	 * Return true iff this is &lt;dataset /> XML element.
	 * 
	 * @param localName
	 *            XML attribute local name
	 * @return true iff this is dataset element
	 */
	private boolean isDatasetElement(String localName) {
		return DATASET_XML_ELEMENT.equals(localName);
	}

	/**
	 * Dataset XML element name.
	 */
	private static final String DATASET_XML_ELEMENT = "dataset";

	/**
	 * Return true iff this XML element has an XML namespace declared.
	 * 
	 * @param namespace
	 *            Element namespace
	 * @return true iff namespace is declared
	 */
	private boolean hasNamespace(String namespace) {
		return !StringUtils.isEmpty(namespace);
	}

	/**
	 * Start dataset parsing if it does not exist yet, or get the existing
	 * dataset for the database schema.
	 * 
	 * @param namespace
	 *            Element namespace
	 * @return Dataset
	 */
	private FlatXmlDataSet startDataset(String namespace) {
		String schemaName = getSchemaName(namespace);
		FlatXmlDataSet dataSet = dataSets.get(schemaName);
		if (dataSet == null) {
			dataSet = createFlatXmlDataSet();
			dataSet.startDataSet();
			dataSets.put(schemaName, dataSet);
		}
		return dataSet;
	}

	/**
	 * Instantiate a new {@link FlatXmlDataSet}.
	 * 
	 * @return New {@link FlatXmlDataSet}
	 */
	protected FlatXmlDataSet createFlatXmlDataSet() {
		return new FlatXmlDataSet();
	}

	/**
	 * Extract database schema name from XML element namespace.
	 * <p>
	 * Use element namespace as schema name, if it is declared, otherwise use
	 * default schema name.
	 * 
	 * @param namespace
	 *            Element namespace
	 * @return Database schema name
	 */
	private String getSchemaName(String namespace) {
		if (hasNamespace(namespace)) {
			return namespace;
		}
		return defaultSchemaName;
	}

	/**
	 * Start processing a table row.
	 * <p>
	 * Create new table metadata from element attributes. Start a row on the
	 * dataset from the metadata.
	 * 
	 * @param dataSet
	 *            Dataset
	 * @param tableName
	 *            Table name
	 * @param attributes
	 *            Element attributes
	 * @return Created table metadata
	 */
	private MutableTableMetaData startRow(FlatXmlDataSet dataSet,
			String tableName, Attributes attributes) {
		Column[] columns = new Column[attributes.getLength()];
		for (int i = 0; i < attributes.getLength(); i++) {
			columns[i] = new Column(attributes.getQName(i), DataType.UNKNOWN);
		}
		MutableTableMetaData tableMetaData = createMutableTableMetaData(
				tableName, columns);
		dataSet.startRow(tableMetaData);
		return tableMetaData;
	}

	/**
	 * Instantiate new {@link MutableTableMetaData}.
	 * 
	 * @param tableName
	 *            Table name
	 * @param columns
	 * @return New {@link MutableTableMetaData}
	 */
	protected MutableTableMetaData createMutableTableMetaData(String tableName,
			Column[] columns) {
		return new MutableTableMetaData(tableName, columns);
	}

	/**
	 * Set row values from element attributes.
	 * <p>
	 * If it is an empty table row, return. Otherwise, parse column values from
	 * XML attributes and set row values.
	 * 
	 * @param dataSet
	 *            Dataset
	 * @param metaData
	 *            Table metadata
	 * @param attributes
	 *            Element attributes
	 */
	private void setRowValues(FlatXmlDataSet dataSet,
			MutableTableMetaData metaData, Attributes attributes) {
		Column[] columns = metaData.getColumns();
		if (isEmptyTableRow(columns, attributes)) {
			return;
		}
		String[] rowValues = new String[columns.length];
		for (int i = 0; i < columns.length; i++) {
			Column column = columns[i];
			rowValues[i] = attributes.getValue(column.getColumnName());
			log.debug("Parsed XML column {} with value [{}].",
					column.getColumnName(), rowValues[i]);
		}
		dataSet.setRowValues(rowValues);
	}

	/**
	 * Return true iff it is an empty table row.
	 * <p>
	 * Return true if there are no columns in table metadata or there are no
	 * element attributes.
	 * 
	 * @param columns
	 *            Table columns
	 * @param attributes
	 *            Element attributes
	 * @return true iff it is empty table row
	 */
	private boolean isEmptyTableRow(Column[] columns, Attributes attributes) {
		return columns.length == 0 || attributes.getLength() == 0;
	}

	/**
	 * Return parsed dataset.
	 * 
	 * @return Parsed dataset
	 */
	public MultiSchemaDataSet getMultiSchemaDataSet() {
		MultiSchemaDataSet multiSchemaDataSet = new MultiSchemaDataSet();
		for (String schemaName : dataSets.keySet()) {
			FlatXmlDataSet dataSet = dataSets.get(schemaName);
			multiSchemaDataSet.setDataSetForSchema(schemaName, dataSet);
		}
		return multiSchemaDataSet;
	}
}
