package net.sf.lightair.internal;

import net.sf.lightair.internal.db.Converter;
import net.sf.lightair.internal.db.Delete;
import net.sf.lightair.internal.db.Execute;
import net.sf.lightair.internal.db.Insert;
import net.sf.lightair.internal.db.Structure;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Api {

	public static void generateXsd(String propertiesFileName) {
		Map<String, Map<String, String>> properties = Properties.load(propertiesFileName);
		Map<String, Connection> connections = Connections.open(properties);
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures =
				Structure.loadAll(properties, connections);
		Xsd.generate(properties, structures);
		Connections.close(connections);
	}

	public static void setup(String propertiesFileName, Map<String, List<String>> fileNames) {
		Map<String, Map<String, String>> properties = Properties.load(propertiesFileName);
		Map<String, List<Map<String, Object>>> xmlDatasets = Xml.read(fileNames);

		Map<String, Connection> connections = Connections.open(properties);
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures =
				Structure.loadAll(properties, connections);
		Map<String, List<Map<String, Object>>> datasets = Converter.convert(structures, xmlDatasets);

		for (String profile : datasets.keySet()) {
			Map<String, String> profileProperties = properties.get(profile);
			Map<String, Map<String, Map<String, Object>>> profileStructure = structures.get(profile);
			List<Map<String, Object>> dataset = datasets.get(profile);
			Connection connection = connections.get(profile);

			List<Map<String, Object>> statements = new ArrayList<>();
			statements.addAll(Delete.create(profileProperties, profileStructure, dataset));
			statements.addAll(Insert.create(profileProperties, profileStructure, dataset));

			Execute.update(connection, statements);
		}
		Connections.close(connections);
	}
}
