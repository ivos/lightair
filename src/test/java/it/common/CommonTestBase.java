package it.common;

import net.sf.lightair.internal.factory.Factory;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.BeforeClass;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class CommonTestBase {

	protected final static JdbcTemplate db;

	static {
		SingleConnectionDataSource dataSource = new SingleConnectionDataSource(
				"jdbc:h2:mem:test", "sa", "", true);
		db = new JdbcTemplate(dataSource);
	}

	@BeforeClass
	public static void initCommonTestBase() {
		DateTimeUtils.setCurrentMillisFixed(new DateTime(2009, 8, 28, 19, 49,
				59, 987).getMillis());
		Factory.getInstance().init();
	}

}
