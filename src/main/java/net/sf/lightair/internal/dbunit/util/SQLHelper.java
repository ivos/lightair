package net.sf.lightair.internal.dbunit.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.lightair.internal.factory.Factory;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom methods to replace original {@link org.dbunit.util.SQLHelper} methods.
 */
public class SQLHelper {

	private final Logger logger = LoggerFactory.getLogger(SQLHelper.class);

	/**
	 * Custom method to create custom
	 * {@link net.sf.lightair.internal.dbunit.dataset.Column}.
	 * 
	 * @param resultSet
	 * @param dataTypeFactory
	 * @param datatypeWarning
	 * @return
	 * @throws SQLException
	 * @throws DataTypeException
	 */
	public Column createColumn(ResultSet resultSet,
			IDataTypeFactory dataTypeFactory, boolean datatypeWarning)
			throws SQLException, DataTypeException {
		String tableName = resultSet.getString(3);
		String columnName = resultSet.getString(4);
		int sqlType = resultSet.getInt(5);
		// If Types.DISTINCT like SQL DOMAIN, then get Source Date Type of
		// SQL-DOMAIN
		if (sqlType == java.sql.Types.DISTINCT) {
			sqlType = resultSet.getInt("SOURCE_DATA_TYPE");
		}

		String sqlTypeName = resultSet.getString(6);
		// int columnSize = resultSet.getInt(7);
		int nullable = resultSet.getInt(11);
		String remarks = resultSet.getString(12);
		String columnDefaultValue = resultSet.getString(13);
		// This is only available since Java 5 - so we ca try it and if it does
		// not work default it
		String isAutoIncrement = Column.AutoIncrement.NO.getKey();
		try {
			isAutoIncrement = resultSet.getString(23);
		} catch (SQLException e) {
			if (logger.isDebugEnabled())
				logger.debug(
						"Could not retrieve the 'isAutoIncrement' property because not yet running on Java 1.5 - defaulting to NO. "
								+ "Table="
								+ tableName
								+ ", Column="
								+ columnName, e);
			// Ignore this one here
		}

		// Convert SQL type to DataType
		DataType dataType = dataTypeFactory.createDataType(sqlType,
				sqlTypeName, tableName, columnName);
		if (dataType != DataType.UNKNOWN) {
			Column column = Factory.getInstance().getColumn(columnName,
					dataType, sqlTypeName, nullable, columnDefaultValue,
					remarks, isAutoIncrement);
			return column;
		} else {
			if (datatypeWarning)
				logger.warn(tableName
						+ "."
						+ columnName
						+ " data type ("
						+ sqlType
						+ ", '"
						+ sqlTypeName
						+ "') not recognized and will be ignored. See FAQ for more information.");

			// datatype unknown - column not created
			return null;
		}
	}

}
