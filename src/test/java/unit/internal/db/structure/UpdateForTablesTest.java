package unit.internal.db.structure;

import net.sf.lightair.internal.Connections;
import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.db.Structure;
import org.junit.Test;
import test.support.DbTemplate;
import unit.internal.ConnectionsTest;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class UpdateForTablesTest implements Keywords {

	private Map<String, String> createProperties(
			String driverClassName, String url, String userName, String password, String dialect, String schema) {
		Map<String, String> properties = ConnectionsTest.createProperties(driverClassName, url, userName, password);
		properties.put(DATABASE_DIALECT, dialect);
		properties.put(DATABASE_SCHEMA, schema);
		return properties;
	}

	private List<Map<String, Object>> createDataset(String... tableNames) {
		ArrayList<Map<String, Object>> dataset = new ArrayList<>();
		for (String tableName : tableNames) {
			HashMap<String, Object> table = new HashMap<>();
			table.put(TABLE, tableName);
			dataset.add(Collections.unmodifiableMap(table));
		}
		return Collections.unmodifiableList(dataset);
	}

	private Map<String, List<Map<String, Object>>> createDatasets() {
		Map<String, List<Map<String, Object>>> datasets = new LinkedHashMap<>();
		datasets.put("p1", createDataset("p1t1exs", "p1t2new"));
		datasets.put("p2", createDataset("p2t1new", "p2t3new"));
		return Collections.unmodifiableMap(datasets);
	}

	@Test
	public void empty() {
		Map<String, Map<String, String>> properties = new HashMap<>();
		properties.put("p1", createProperties(
				"org.h2.Driver", "jdbc:h2:mem:test", "sa", "", "h2", "PUBLIC"));
		properties.put("p2", createProperties(
				"org.hsqldb.jdbc.JDBCDriver", "jdbc:hsqldb:mem:test", "sa", "", "hsql", "PUBLIC"));

		Map<String, Connection> connections = Connections.open(properties);

		DbTemplate h2 = new DbTemplate("jdbc:h2:mem:test", "sa", "");
		h2.db.execute("create table p1t1exs (p1t1exsc1exs varchar(50), p1t1exsc1new varchar(50))");
		h2.db.execute("create table p1t2new (p1t2newc1 varchar(50), p1t2newc2 varchar(50))");
		h2.db.execute("create table p1t3uns (p1t3unsc1 varchar(50), p1t3unsc2 varchar(50))");

		DbTemplate hsql = new DbTemplate("jdbc:hsqldb:mem:test", "sa", "");
		hsql.db.execute("create table p2t1new (p2t1newc1 varchar(50))");
		hsql.db.execute("create table p2t2uns (p2t2unsc1 int)");
		hsql.db.execute("create table p2t3new (p2t3newc1 int)");

		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new LinkedHashMap<>();

		Structure.updateForTables(structures, createDatasets(), properties, connections);

		h2.db.execute("drop table p1t1exs");
		h2.db.execute("drop table p1t2new");
		h2.db.execute("drop table p1t3uns");
		hsql.db.execute("drop table p2t1new");
		hsql.db.execute("drop table p2t2uns");
		hsql.db.execute("drop table p2t3new");

		String expected = "{p1={p1t1exs={...},\n" +
				" p1t2new={...}},\n" +
				" p2={p2t1new={...},\n" +
				" p2t3new={...}}}";
		assertEquals(expected, structures.toString().replace("}, ", "},\n "));

		Connections.close(connections);
	}
}
