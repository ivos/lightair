package net.sf.lightair.internal.dbunit;

import java.sql.SQLException;

import net.sf.lightair.internal.util.AutoValueGenerator;

import org.dbunit.database.statement.IPreparedBatchStatement;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoPreparedBatchStatement implements IPreparedBatchStatement {

	private final Logger log = LoggerFactory
			.getLogger(AutoPreparedBatchStatement.class);

	private final IPreparedBatchStatement delegate;

	private final AutoValueGenerator autoValueGenerator;

	public AutoPreparedBatchStatement(IPreparedBatchStatement delegate,
			AutoValueGenerator autoValueGenerator) {
		this.delegate = delegate;
		this.autoValueGenerator = autoValueGenerator;
	}

	public void addValue(Object value, DataType dataType, String tableName,
			String columnName, int columnLength, Integer columnPrecision,
			int rowId) throws TypeCastException, SQLException {
		log.debug("Adding value {} for data type {} on {}.{} with rowId {}.",
				value, dataType, tableName, columnName, rowId);
		if ("@auto".equals(value)) {
			value = autoValueGenerator.generateAutoValue(dataType, tableName,
					columnName, columnLength, columnPrecision, rowId);
		}
		addValue(value, dataType);
	}

	public void addValue(Object value, DataType dataType)
			throws TypeCastException, SQLException {
		delegate.addValue(value, dataType);
	}

	public void addBatch() throws SQLException {
		delegate.addBatch();
	}

	public int executeBatch() throws SQLException {
		return delegate.executeBatch();
	}

	public void clearBatch() throws SQLException {
		delegate.clearBatch();
	}

	public void close() throws SQLException {
		delegate.close();
	}

}
