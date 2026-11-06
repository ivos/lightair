package it.common;

import net.sf.lightair.LightAirApi;
import net.sf.lightair.internal.junit.util.Factory;
import org.junit.BeforeClass;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class CommonTestBase {

	protected final static JdbcTemplate db;

	static {
		SingleConnectionDataSource dataSource = new SingleConnectionDataSource("jdbc:h2:mem:test", "sa", "", true);
		db = new JdbcTemplate(dataSource);
	}

	@BeforeClass
	@org.testng.annotations.BeforeClass
	public static void initCommonTestBase() {
		LightAirApi.setFixedTime(
			LocalDateTime.of(2009, 8, 28, 19, 49, 59, 987000000)
				.atZone(ZoneId.systemDefault())
		);
		Factory.getInstance().init();
	}
}
