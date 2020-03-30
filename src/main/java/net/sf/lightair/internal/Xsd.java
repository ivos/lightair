package net.sf.lightair.internal;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Generate XSD files from database structure.
 */
public class Xsd implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Xsd.class);

	private static final String FILE_NAME_PREFIX = "dataset";
	private static final String FILE_NAME_SUFFIX = ".xsd";
	private static final String BASE_TYPES_FILE_NAME = "light-air-types.xsd";

	public static void generate(
			Map<String, Map<String, String>> properties,
			Map<String, Map<String, Map<String, Map<String, Object>>>> structure) {
		log.info("Generating XSD files.");
		String xsdDirectory = properties.get(DEFAULT_PROFILE).get(XSD_DIRECTORY);
		if (null == xsdDirectory) {
			xsdDirectory = DEFAULT_XSD_DIRECTORY;
		}
		File directory = new File(xsdDirectory);

		copyBaseTypes(directory);
		for (String profile : structure.keySet()) {
			log.debug("Reading DB structure for profile [{}].", profile);
			String content = createContent(structure.get(profile));
			log.debug("Writing XSD file for profile [{}].", profile);
			writeProfileXsd(directory, profile, content);
		}
		log.info("Generated XSD files.");
	}

	private static String convert(String sqlName) {
		return sqlName.toLowerCase();
	}

	private static String createContent(Map<String, Map<String, Map<String, Object>>> profileStructure) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:la=\"http://lightair.sourceforge.net/\">\n");
		sb.append("\t<xsd:import namespace=\"http://lightair.sourceforge.net/\" schemaLocation=\"light-air-types.xsd\"/>\n\n");
		writeDatasetElement(sb, profileStructure);
		sb.append("\n");
		writeTableTypes(sb, profileStructure);
		sb.append("</xsd:schema>\n");
		return sb.toString();
	}

	private static void writeDatasetElement(
			StringBuilder sb, Map<String, Map<String, Map<String, Object>>> profileStructure) {
		sb.append("\t<xsd:element name=\"dataset\">\n");
		sb.append("\t\t<xsd:complexType>\n");
		sb.append("\t\t\t<xsd:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n");
		for (String tableName : profileStructure.keySet()) {
			sb.append("\t\t\t\t<xsd:element name=\"");
			sb.append(convert(tableName));
			sb.append("\" type=\"");
			sb.append(convert(tableName));
			sb.append("__Type\"/>\n");
		}
		sb.append("\t\t\t</xsd:choice>\n");
		sb.append("\t\t</xsd:complexType>\n");
		sb.append("\t</xsd:element>\n");
	}

	private static void writeTableTypes(
			StringBuilder sb, Map<String, Map<String, Map<String, Object>>> profileStructure) {
		for (String tableName : profileStructure.keySet()) {
			sb.append("\t<xsd:complexType name=\"");
			sb.append(convert(tableName));
			sb.append("__Type\">\n");
			Map<String, Map<String, Object>> columns = profileStructure.get(tableName);
			for (String columnName : columns.keySet()) {
				writeColumn(sb, columnName, columns);
			}
			sb.append("\t</xsd:complexType>\n");
		}
	}

	private static void writeColumn(StringBuilder sb, String columnName, Map<String, Map<String, Object>> columns) {
		Map<String, Object> column = columns.get(columnName);
		String dataType = (String) column.get(DATA_TYPE);
		if (FLOAT.equals(dataType) || DOUBLE.equals(dataType)) {
			dataType = BIGDECIMAL;
		}
		if (FIXED_STRING.equals(dataType) || NSTRING.equals(dataType) || FIXED_NSTRING.equals(dataType)
				|| BYTES.equals(dataType) || BLOB.equals(dataType) || CLOB.equals(dataType) || NCLOB.equals(dataType)
				|| UUID.equals(dataType) || JSON.equals(dataType)) {
			dataType = STRING;
		}
		boolean notNull = (boolean) column.get(NOT_NULL);
		sb.append("\t\t<xsd:attribute name=\"");
		sb.append(convert(columnName));
		sb.append("\" use=\"optional\" type=\"la:");
		sb.append(StringUtils.capitalize(dataType.toLowerCase()));
		if (!notNull) {
			sb.append("Nullable");
		}
		sb.append("Type\"/>\n");
	}

	private static void writeProfileXsd(File directory, String profile, String content) {
		String fileName = FILE_NAME_PREFIX + (profile.isEmpty() ? "" : "-" + profile) + FILE_NAME_SUFFIX;
		writeFile(directory, fileName, content);
	}

	private static void copyBaseTypes(File directory) {
		InputStream inputStream = Xsd.class.getClassLoader().getResourceAsStream(BASE_TYPES_FILE_NAME);
		try {
			String content = IOUtils.toString(inputStream, "UTF-8");
			writeFile(directory, BASE_TYPES_FILE_NAME, content);
		} catch (IOException e) {
			throw new RuntimeException("Cannot load base Light Air XSD types file.", e);
		}
	}

	private static void writeFile(File directory, String fileName, String content) {
		File file = new File(directory, fileName);
		try {
			FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write XSD file " + file, e);
		}
	}
}
