package test.support;

import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;

public class PostgresUtils {

	public static PostgreSQLContainer initPostgres() {
		PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16")
			.withUsername("postgres")
			.withPassword("postgres")
			.withDatabaseName("postgres");
		postgres.setPortBindings(List.of("5432:5432"));
		postgres.start();
		return postgres;
	}

	public static void closePostgresIfOpen(PostgreSQLContainer postgres) {
		if (postgres != null) {
			postgres.stop();
		}
	}
}
