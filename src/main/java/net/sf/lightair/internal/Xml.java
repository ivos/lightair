package net.sf.lightair.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Read XML files with datasets.
 */
public class Xml implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Xml.class);

	public static Map<String, List<Map<String, Object>>> read(Map<String, List<String>> fileNames) {
		Map<String, List<Map<String, Object>>> datasets = new LinkedHashMap<>();
		for (String profile : fileNames.keySet()) {
			List<Map<String, Object>> profileDataset = new ArrayList<>();
			List<String> profileFileNames = fileNames.get(profile);
			for (String fileName : profileFileNames) {
				log.debug("For profile [{}] reading XML file {}.", profile, fileName);
				List<Map<String, Object>> fileDataset = readFile(new File(fileName));
				profileDataset.addAll(fileDataset);
			}
			datasets.put(profile, Collections.unmodifiableList(profileDataset));
		}
		return Collections.unmodifiableMap(datasets);
	}

	private static List<Map<String, Object>> readFile(File file) {
		List<Map<String, Object>> data = new ArrayList<>();
		Document doc = readDocument(file);
		Element dataset = doc.getDocumentElement();
		NodeList rows = dataset.getChildNodes();
		for (int rowIndex = 0; rowIndex < rows.getLength(); rowIndex++) {
			Node row = rows.item(rowIndex);
			if (Node.ELEMENT_NODE != row.getNodeType()) {
				continue; // only process elements
			}
			data.add(createRowData(row));
		}
		return Collections.unmodifiableList(data);
	}

	private static Document readDocument(File file) {
		Document doc;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(file);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Cannot initialize XML reader.");
		} catch (SAXException e) {
			throw new RuntimeException("Cannot parse XML in file " + file + ". Is the XML content well-formed?");
		} catch (IOException e) {
			throw new RuntimeException("Cannot read file " + file + ". Does the file exist?");
		}
		doc.getDocumentElement().normalize();
		return doc;
	}

	private static String convert(String name) {
		return name.toLowerCase();
	}

	private static Map<String, Object> createRowData(Node row) {
		Map<String, Object> rowData = new LinkedHashMap<>();
		String nodeName = row.getNodeName();
		rowData.put(TABLE, convert(nodeName));
		rowData.put(COLUMNS, createAttributeData(row));
		return Collections.unmodifiableMap(rowData);
	}

	private static Map<String, String> createAttributeData(Node row) {
		Map<String, String> attributeData = new HashMap<>(); // attributes order in DOM is not preserved
		NamedNodeMap attributes = row.getAttributes();
		for (int attrIndex = 0; attrIndex < attributes.getLength(); attrIndex++) {
			Node attribute = attributes.item(attrIndex);
			attributeData.put(convert(attribute.getNodeName()), attribute.getNodeValue());
		}
		return Collections.unmodifiableMap(attributeData);
	}
}
