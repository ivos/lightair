package net.sf.lightair.internal.dbunit.dataset.datatype;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;

import org.dbunit.dataset.datatype.BinaryStreamDataType;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.StringDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Informix custom data factory.
 */
public class InformixDataTypeFactory extends DefaultDataTypeFactory {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(InformixDataTypeFactory.class);
	/**
	 * Database product names supported.
	 */
	private static final Collection<?> DATABASE_PRODUCTS = Arrays.asList(new String[] { "Informix" });

	protected static final DataType CLOB_AS_STRING = new StringDataType("CLOB", Types.CLOB);
	protected static final DataType BLOB_AS_STREAM = new BinaryStreamDataType("BLOB", Types.BLOB);

	/**
	 * @see org.dbunit.dataset.datatype.IDbProductRelatable#getValidDbProducts()
	 */
	@Override
	public Collection<?> getValidDbProducts() {
		return DATABASE_PRODUCTS;
	}

	@Override
	public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
		if (logger.isDebugEnabled()) {
			logger.debug("createDataType(sqlType={}, sqlTypeName={}) - start", String.valueOf(sqlType), sqlTypeName);
		}
		// BLOB
		if (sqlType == DataType.BLOB.getSqlType()) {
			return BLOB_AS_STREAM;
		}
		// CLOB
		if (sqlType == DataType.CLOB.getSqlType()) {
			return CLOB_AS_STRING;
		}
		return super.createDataType(sqlType, sqlTypeName);
	}
}