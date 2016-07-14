package net.sf.lightair.internal;

import net.sf.lightair.internal.db.Structure;

import java.sql.Connection;
import java.util.Map;

public class Api {

	public static void generateXsd(String propertiesFileName) {
		Map<String, Map<String, String>> properties = Properties.load(propertiesFileName);
		Map<String, Connection> connections = Connections.open(properties);
		Map<String, Map<String, Map<String, Map<String, Object>>>> structure =
				Structure.loadAll(properties, connections);
		Xsd.generate(properties, structure);
		Connections.close(connections);
	}
}
