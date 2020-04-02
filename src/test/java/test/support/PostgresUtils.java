package test.support;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import java.io.IOException;

public class PostgresUtils {

	public static EmbeddedPostgres initPostgres() {
		try {
			return EmbeddedPostgres.builder()
					.setPort(5432)
					.setCleanDataDirectory(false)
					.setDataDirectory("./target/embeddedpostgres")
					.setServerConfig("max_connections", "10")
					.setServerConfig("max_wal_senders", "0")
					.start();
		} catch (IOException e) {
			throw new RuntimeException("Cannot start embedded Postgres.", e);
		}
	}

	public static void closePostgresIfOpen(EmbeddedPostgres postgres) {
		if (postgres != null) {
			try {
				postgres.close();
			} catch (IOException e) {
				throw new RuntimeException("Cannot stop embedded Postgres.", e);
			}
		}
	}
}
