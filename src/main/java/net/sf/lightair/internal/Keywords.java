package net.sf.lightair.internal;

/**
 * Constant keywords.
 */
public interface Keywords {

	// Properties
	//   DB
	String DATABASE_DRIVER_CLASS_NAME = "database.driverClassName";
	String DATABASE_CONNECTION_URL = "database.connectionUrl";
	String DATABASE_USER_NAME = "database.userName";
	String DATABASE_PASSWORD = "database.password";
	String DATABASE_SCHEMA = "database.schema";
	//   Functional
	String TIME_DIFFERENCE_LIMIT_MILLIS = "time.difference.limit.millis";
	//   Directories
	String XSD_DIRECTORY = "xsd.directory";
	String AUTO_INDEX_DIRECTORY = "auto.index.directory";

	// Environment variable names
	String ENV_DATABASE_DRIVER_CLASS_NAME = "LIGHT_AIR_DATABASE_DRIVER_CLASS_NAME";
	String ENV_DATABASE_CONNECTION_URL = "LIGHT_AIR_DATABASE_CONNECTION_URL";
	String ENV_DATABASE_USER_NAME = "LIGHT_AIR_DATABASE_USER_NAME";
	String ENV_DATABASE_PASSWORD = "LIGHT_AIR_DATABASE_PASSWORD";
	String ENV_DATABASE_SCHEMA = "LIGHT_AIR_DATABASE_SCHEMA";
	String ENV_TIME_DIFFERENCE_LIMIT_MILLIS = "LIGHT_AIR_TIME_DIFFERENCE_LIMIT_MILLIS";

	// Default values
	String DEFAULT_PROPERTIES_FILE_NAME = "target/test-classes/light-air.properties";
	String DEFAULT_XSD_DIRECTORY = "src/test/java";
	String DEFAULT_AUTO_INDEX_DIRECTORY = "src/test/resources";

	String DEFAULT_PROFILE = "";

	String TABLE = "TABLE";
	String COLUMNS = "COLUMNS";
	String DATA_TYPE = "DATA_TYPE";
	String JDBC_DATA_TYPE = "JDBC_DATA_TYPE";
	String NOT_NULL = "NOT_NULL";
	String SIZE = "SIZE";
	String DECIMAL_DIGITS = "DECIMAL_DIGITS";
	String VALUE = "VALUE";
	String SQL = "SQL";
	String PARAMETERS = "PARAMETERS";
	String COLUMN = "COLUMN";
	String EXPECTED = "EXPECTED";
	String ACTUAL = "ACTUAL";
	String DIFFERENT = "DIFFERENT";
	String MISSING = "MISSING";
	String UNEXPECTED = "UNEXPECTED";
	String DIFFERENCES = "DIFFERENCES";

	String BOOLEAN = "BOOLEAN";
	String BYTE = "BYTE";
	String SHORT = "SHORT";
	String INTEGER = "INTEGER";
	String LONG = "LONG";
	String FLOAT = "FLOAT";
	String DOUBLE = "DOUBLE";
	String BIGDECIMAL = "BIGDECIMAL";
	String DATE = "DATE";
	String TIME = "TIME";
	String TIMESTAMP = "TIMESTAMP";
	String STRING = "STRING";
	String FIXED_STRING = "FIXED_STRING";
	String NSTRING = "NSTRING";
	String FIXED_NSTRING = "FIXED_NSTRING";
	String BYTES = "BYTES";
	String CLOB = "CLOB";
	String NCLOB = "NCLOB";
	String BLOB = "BLOB";
	String UUID = "UUID";
	String JSON = "JSON";
	String JSONB = "JSONB";
	String ARRAY_STRING = "ARRAY_STRING";
	String ARRAY_INTEGER = "ARRAY_INTEGER";
	String ARRAY_LONG = "ARRAY_LONG";

	String NULL_TOKEN = "@null";
	String DATE_TOKEN = "@date";
	String TIME_TOKEN = "@time";
	String TIMESTAMP_TOKEN = "@timestamp";
	String AUTO_TOKEN = "@auto";
	String ANY_TOKEN = "@any";
	String VARIABLE_PREFIX = "$";
	String ARRAY_ELEMENT_SEPARATOR = ",";
}
