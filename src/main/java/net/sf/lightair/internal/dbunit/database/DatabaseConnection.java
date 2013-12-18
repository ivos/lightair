package net.sf.lightair.internal.dbunit.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.CustomDatabaseDataSet;
import org.dbunit.dataset.IDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom {@link org.dbunit.database.DatabaseConnection} to use
 * {@link CustomDatabaseDataSet}.
 */
public class DatabaseConnection extends org.dbunit.database.DatabaseConnection {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private IDataSet _dataSet = null;

	public DatabaseConnection(Connection connection, String schema)
			throws DatabaseUnitException {
		super(connection, schema);
	}

	@Override
	public IDataSet createDataSet() throws SQLException {
		logger.debug("createDataSet() - start");

		if (_dataSet == null) {
			_dataSet = new CustomDatabaseDataSet(this);
		}

		return _dataSet;
	}

	@Override
	public String toString() {
		return "DatabaseConnection [super=" + super.toString() + ", _dataSet="
				+ _dataSet + "]";
	}

}
