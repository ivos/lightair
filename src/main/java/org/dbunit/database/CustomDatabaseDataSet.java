package org.dbunit.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.lightair.internal.factory.Factory;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.NoSuchTableException;
import org.dbunit.dataset.OrderedTableNameMap;
import org.dbunit.dataset.filter.ITableFilterSimple;
import org.dbunit.util.QualifiedTableName;
import org.dbunit.util.SQLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom {@link DatabaseDataSet} to use {@link CustomDatabaseTableMetaData}.
 */
public class CustomDatabaseDataSet extends DatabaseDataSet {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	protected OrderedTableNameMap _tableMap = null;
	protected final IDatabaseConnection _connection;
	protected final ITableFilterSimple _tableFilter;
	protected final ITableFilterSimple _oracleRecycleBinTableFilter;

	public CustomDatabaseDataSet(IDatabaseConnection connection)
			throws SQLException {
		super(connection);
		_connection = connection;
		_tableFilter = null;
		_oracleRecycleBinTableFilter = new OracleRecycleBinTableFilter(
				connection.getConfig());
	}

	/**
	 * Get all the table names form the database that are not system tables.
	 */
	protected void initialize() throws DataSetException {
		logger.debug("initialize() - start");

		if (_tableMap != null) {
			return;
		}

		try {
			logger.debug("Initializing the data set from the database...");

			Connection jdbcConnection = _connection.getConnection();
			DatabaseMetaData databaseMetaData = jdbcConnection.getMetaData();

			String schema = _connection.getSchema();

			if (SQLHelper.isSybaseDb(jdbcConnection.getMetaData())
					&& !jdbcConnection.getMetaData().getUserName()
							.equals(schema)) {
				logger.warn("For sybase the schema name should be equal to the user name. "
						+ "Otherwise the DatabaseMetaData#getTables() method might not return any columns. "
						+ "See dbunit tracker #1628896 and http://issues.apache.org/jira/browse/TORQUE-40?page=all");
			}

			DatabaseConfig config = _connection.getConfig();
			String[] tableType = (String[]) config
					.getProperty(DatabaseConfig.PROPERTY_TABLE_TYPE);
			IMetadataHandler metadataHandler = (IMetadataHandler) config
					.getProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER);

			ResultSet resultSet = metadataHandler.getTables(databaseMetaData,
					schema, tableType);

			if (logger.isDebugEnabled()) {
				logger.debug(SQLHelper.getDatabaseInfo(jdbcConnection
						.getMetaData()));
				logger.debug("metadata resultset={}", resultSet);
			}

			try {
				OrderedTableNameMap tableMap = super.createTableNameMap();
				while (resultSet.next()) {
					String schemaName = metadataHandler.getSchema(resultSet);
					String tableName = resultSet.getString(3);

					if (_tableFilter != null && !_tableFilter.accept(tableName)) {
						logger.debug("Skipping table '{}'", tableName);
						continue;
					}
					if (!_oracleRecycleBinTableFilter.accept(tableName)) {
						logger.debug("Skipping oracle recycle bin table '{}'",
								tableName);
						continue;
					}

					QualifiedTableName qualifiedTableName = new QualifiedTableName(
							tableName, schemaName);
					tableName = qualifiedTableName
							.getQualifiedNameIfEnabled(config);

					// Put the table into the table map
					tableMap.add(tableName, null);
				}

				_tableMap = tableMap;
			} finally {
				resultSet.close();
			}
		} catch (SQLException e) {
			throw new DataSetException(e);
		}
	}

	// //////////////////////////////////////////////////////////////////////////
	// IDataSet interface

	@Override
	public String[] getTableNames() throws DataSetException {
		initialize();

		return _tableMap.getTableNames();
	}

	@Override
	public ITableMetaData getTableMetaData(String tableName)
			throws DataSetException {
		logger.debug("getTableMetaData(tableName={}) - start", tableName);

		initialize();

		// Verify if table exist in the database
		if (!_tableMap.containsTable(tableName)) {
			logger.error("Table '{}' not found in tableMap={}", tableName,
					_tableMap);
			throw new NoSuchTableException(tableName);
		}

		// Try to find cached metadata
		ITableMetaData metaData = (ITableMetaData) _tableMap.get(tableName);
		if (metaData != null) {
			return metaData;
		}

		// Create metadata and cache it
		metaData = Factory.getInstance().getTableMetaData(tableName,
				_connection, true, super.isCaseSensitiveTableNames());
		// Put the metadata object into the cache map
		_tableMap.update(tableName, metaData);

		return metaData;
	}

	@Override
	public ITable getTable(String tableName) throws DataSetException {
		logger.debug("getTable(tableName={}) - start", tableName);

		initialize();

		try {
			ITableMetaData metaData = getTableMetaData(tableName);

			DatabaseConfig config = _connection.getConfig();
			IResultSetTableFactory factory = (IResultSetTableFactory) config
					.getProperty(DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY);
			return factory.createTable(metaData, _connection);
		} catch (SQLException e) {
			throw new DataSetException(e);
		}
	}

	public static class OracleRecycleBinTableFilter implements
			ITableFilterSimple {
		private final DatabaseConfig _config;

		public OracleRecycleBinTableFilter(DatabaseConfig config) {
			this._config = config;
		}

		@SuppressWarnings("deprecation")
		public boolean accept(String tableName) throws DataSetException {
			// skip oracle 10g recycle bin system tables if enabled
			if (_config
					.getFeature(DatabaseConfig.FEATURE_SKIP_ORACLE_RECYCLEBIN_TABLES)) {
				// Oracle 10g workaround
				// don't process system tables (oracle recycle bin tables) which
				// are reported to the application due a bug in the oracle JDBC
				// driver
				if (tableName.startsWith("BIN$")) {
					return false;
				}
			}

			return true;
		}
	}

}
