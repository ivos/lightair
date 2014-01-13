package org.dbunit.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sf.lightair.internal.dbunit.util.SQLHelper;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.NoSuchTableException;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.util.QualifiedTableName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom {@link DatabaseTableMetaData} to use custom {@link SQLHelper} to
 * create custom {@link net.sf.lightair.internal.dbunit.dataset.Column}.
 */
public class CustomDatabaseTableMetaData extends DatabaseTableMetaData {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	protected Column[] _columns;
	protected final QualifiedTableName _qualifiedTableNameSupport;
	protected final IDatabaseConnection _connection;
	protected boolean _caseSensitiveMetaData;
	protected final String _originalTableName;
	private SQLHelper sqlHelper;

	public CustomDatabaseTableMetaData(final String tableName,
			IDatabaseConnection connection, boolean validate,
			boolean caseSensitiveMetaData) throws DataSetException {
		super(tableName, connection, validate, caseSensitiveMetaData);

		_connection = connection;
		_caseSensitiveMetaData = caseSensitiveMetaData;

		try {
			Connection jdbcConnection = connection.getConnection();
			if (!caseSensitiveMetaData) {
				_originalTableName = org.dbunit.util.SQLHelper.correctCase(
						tableName, jdbcConnection);
				org.dbunit.util.SQLHelper.logDebugIfValueChanged(tableName,
						_originalTableName, "Corrected table name:",
						DatabaseTableMetaData.class);
			} else {
				_originalTableName = tableName;
			}

			// qualified names support - table name and schema is stored here
			_qualifiedTableNameSupport = new QualifiedTableName(
					_originalTableName, _connection.getSchema());

			if (validate) {
				String schemaName = _qualifiedTableNameSupport.getSchema();
				String plainTableName = _qualifiedTableNameSupport.getTable();
				logger.debug(
						"Validating if table '{}' exists in schema '{}' ...",
						plainTableName, schemaName);
				try {
					DatabaseConfig config = connection.getConfig();
					IMetadataHandler metadataHandler = (IMetadataHandler) config
							.getProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER);
					DatabaseMetaData databaseMetaData = jdbcConnection
							.getMetaData();
					if (!metadataHandler.tableExists(databaseMetaData,
							schemaName, plainTableName)) {
						throw new NoSuchTableException("Did not find table '"
								+ plainTableName + "' in schema '" + schemaName
								+ "'");
					}
				} catch (SQLException e) {
					throw new DataSetException(
							"Exception while validation existence of table '"
									+ plainTableName + "'", e);
				}
			} else {
				logger.debug("Validation switched off. Will not check if table exists.");
			}
		} catch (SQLException e) {
			throw new DataSetException(
					"Exception while retrieving JDBC connection from dbunit connection '"
							+ connection + "'", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Column[] getColumns() throws DataSetException {
		logger.debug("getColumns() - start");

		if (_columns == null) {
			try {
				// qualified names support
				String schemaName = _qualifiedTableNameSupport.getSchema();
				String tableName = _qualifiedTableNameSupport.getTable();

				Connection jdbcConnection = _connection.getConnection();
				DatabaseMetaData databaseMetaData = jdbcConnection
						.getMetaData();

				DatabaseConfig config = _connection.getConfig();

				IMetadataHandler metadataHandler = (IMetadataHandler) config
						.getProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER);
				ResultSet resultSet = metadataHandler.getColumns(
						databaseMetaData, schemaName, tableName);

				try {
					IDataTypeFactory dataTypeFactory = super
							.getDataTypeFactory(_connection);
					@SuppressWarnings("deprecation")
					boolean datatypeWarning = config
							.getFeature(DatabaseConfig.FEATURE_DATATYPE_WARNING);

					@SuppressWarnings("rawtypes")
					List columnList = new ArrayList();
					while (resultSet.next()) {
						// Check for exact table/schema name match because
						// databaseMetaData.getColumns() uses patterns for the
						// lookup
						boolean match = metadataHandler.matches(resultSet,
								schemaName, tableName, _caseSensitiveMetaData);
						if (match) {
							Column column = sqlHelper.createColumn(resultSet,
									dataTypeFactory, datatypeWarning);
							if (column != null) {
								columnList.add(column);
							}
						} else {
							logger.debug("Skipping <schema.table> '"
									+ resultSet.getString(2) + "."
									+ resultSet.getString(3)
									+ "' because names do not exactly match.");
						}
					}

					if (columnList.size() == 0) {
						logger.warn("No columns found for table '" + tableName
								+ "' that are supported by dbunit. "
								+ "Will return an empty column list");
					}

					_columns = (Column[]) columnList.toArray(new Column[0]);
				} finally {
					resultSet.close();
				}
			} catch (SQLException e) {
				throw new DataSetException(e);
			}
		}
		return _columns;
	}

	public SQLHelper getSqlHelper() {
		return sqlHelper;
	}

	public void setSqlHelper(SQLHelper sqlHelper) {
		this.sqlHelper = sqlHelper;
	}

}
