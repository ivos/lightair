package test.support;

import org.testcontainers.oracle.OracleContainer;

import java.util.List;

public class OracleUtils {

	public static OracleContainer initOracle() {
		OracleContainer oracle = new OracleContainer("gvenzl/oracle-free:23-slim-faststart");
		oracle.setPortBindings(List.of("1521:1521"));
		oracle.start();
		return oracle;
	}

	public static void closeOracleIfOpen(OracleContainer oracle) {
		if (oracle != null) {
			oracle.stop();
		}
	}
}
