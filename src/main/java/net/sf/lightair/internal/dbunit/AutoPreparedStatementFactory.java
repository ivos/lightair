package net.sf.lightair.internal.dbunit;

import java.sql.SQLException;

import net.sf.lightair.internal.util.AutoValueGenerator;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.statement.IPreparedBatchStatement;
import org.dbunit.database.statement.PreparedStatementFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoPreparedStatementFactory extends PreparedStatementFactory {

	private final Logger log = LoggerFactory
			.getLogger(AutoPreparedStatementFactory.class);

	private AutoValueGenerator autoValueGenerator;

	@Override
	public IPreparedBatchStatement createPreparedBatchStatement(String sql,
			IDatabaseConnection connection) throws SQLException {
		log.debug(
				"Creating PreparedBatchStatement for sql {} and connection {}.",
				sql, connection);
		IPreparedBatchStatement delegate = super.createPreparedBatchStatement(
				sql, connection);
		AutoPreparedBatchStatement autoPreparedBatchStatement = new AutoPreparedBatchStatement(
				delegate, autoValueGenerator);
		return autoPreparedBatchStatement;
	}

	public void setAutoValueGenerator(AutoValueGenerator autoValueGenerator) {
		this.autoValueGenerator = autoValueGenerator;
	}

}
