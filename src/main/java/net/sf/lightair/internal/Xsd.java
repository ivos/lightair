package net.sf.lightair.internal;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Xsd implements Keywords {

	private static final String FILE_NAME_PREFIX = "dataset";
	private static final String FILE_NAME_SUFFIX = ".xsd";

	public static void generate(
			Map<String, Map<String, String>> properties,
			Map<String, Map<String, Map<String, Map<String, Object>>>> structure) {
		String xsdDirectory = properties.get(DEFAULT_PROFILE).get(XSD_DIRECTORY);
		if (null == xsdDirectory) {
			xsdDirectory = DEFAULT_XSD_DIRECTORY;
		}
		File directory = new File(xsdDirectory);

		for (String profile : structure.keySet()) {
			String content = createContent(structure.get(profile));
			writeFile(directory, profile, content);
		}
	}

	private static String convert(String sqlName) {
		return sqlName.toLowerCase();
	}

	private static String createContent(Map<String, Map<String, Map<String, Object>>> profileStructure) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n");
		sb.append("\t<xsd:element name=\"dataset\">\n");
		sb.append("\t\t<xsd:complexType>\n");
		sb.append("\t\t\t<xsd:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n");
		for (String tableName : profileStructure.keySet()) {
			sb.append("\t\t\t\t<xsd:element name=\"");
			sb.append(convert(tableName));
			sb.append("\" type=\"");
			sb.append(convert(tableName));
			sb.append("__type\"/>\n");
		}
		sb.append("\t\t\t</xsd:choice>\n");
		sb.append("\t\t</xsd:complexType>\n");
		sb.append("\t</xsd:element>\n");
		sb.append("\n");
		for (String tableName : profileStructure.keySet()) {
			sb.append("\t<xsd:complexType name=\"");
			sb.append(convert(tableName));
			sb.append("__type\">\n");
			Map<String, Map<String, Object>> columns = profileStructure.get(tableName);
			for (String columnName : columns.keySet()) {
//				Map<String, Object> column = columns.get(columnName);
				sb.append("\t\t<xsd:attribute name=\"");
				sb.append(convert(columnName));
				sb.append("\" use=\"optional\" type=\"xsd:string\"/>\n");
			}
			sb.append("\t</xsd:complexType>\n");
		}
		sb.append("</xsd:schema>\n");
		return sb.toString();
	}

	private static void writeFile(File directory, String profile, String content) {
		String fileName = FILE_NAME_PREFIX + (profile.isEmpty() ? "" : "-" + profile) + FILE_NAME_SUFFIX;
		File file = new File(directory, fileName);
		try {
			FileUtils.writeStringToFile(file, content);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write XSD file " + file, e);
		}
	}
}
