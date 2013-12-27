package net.sf.lightair.internal.factory;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import net.sf.lightair.exception.CreateDatabaseConnectionException;
import net.sf.lightair.exception.DataSourceSetupException;
import net.sf.lightair.exception.DatabaseAccessException;
import net.sf.lightair.internal.dbunit.AutoPreparedStatementFactory;
import net.sf.lightair.internal.dbunit.ConnectionFactory;
import net.sf.lightair.internal.dbunit.DbUnitWrapper;
import net.sf.lightair.internal.dbunit.database.DatabaseConnection;
import net.sf.lightair.internal.dbunit.dataset.MergingTable;
import net.sf.lightair.internal.dbunit.dataset.TokenReplacingFilter;
import net.sf.lightair.internal.dbunit.util.SQLHelper;
import net.sf.lightair.internal.junit.BaseUrlTestRule;
import net.sf.lightair.internal.junit.SetupExecutor;
import net.sf.lightair.internal.junit.SetupListTestRule;
import net.sf.lightair.internal.junit.SetupTestRule;
import net.sf.lightair.internal.junit.VerifyExecutor;
import net.sf.lightair.internal.junit.VerifyListTestRule;
import net.sf.lightair.internal.junit.VerifyTestRule;
import net.sf.lightair.internal.properties.PropertiesProvider;
import net.sf.lightair.internal.properties.PropertyKeys;
import net.sf.lightair.internal.unitils.DataSetFactory;
import net.sf.lightair.internal.unitils.DataSetLoader;
import net.sf.lightair.internal.unitils.UnitilsWrapper;
import net.sf.lightair.internal.unitils.compare.Column;
import net.sf.lightair.internal.unitils.compare.DataSetAssert;
import net.sf.lightair.internal.unitils.compare.VariableResolver;
import net.sf.lightair.internal.util.AutoNumberGenerator;
import net.sf.lightair.internal.util.DataSetProcessingData;
import net.sf.lightair.internal.util.DataSetResolver;
import net.sf.lightair.internal.util.DurationParser;
import net.sf.lightair.internal.util.HashGenerator;
import net.sf.lightair.internal.util.Profiles;
import net.sf.lightair.internal.util.StandardAutoValueGenerator;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.CustomDatabaseTableMetaData;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.statement.PreparedStatementFactory;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.operation.AutoInsertOperation;
import org.dbunit.operation.CompositeOperation;
import org.dbunit.operation.DatabaseOperation;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Factory class.
 */
public class Factory implements PropertyKeys {

	private final Logger log = LoggerFactory.getLogger(Factory.class);

	// single-instance classes and their getters

	private final PropertiesProvider propertiesProvider = new PropertiesProvider();

	public PropertiesProvider getPropertiesProvider() {
		return propertiesProvider;
	}

	private final Map<String, ComboPooledDataSource> dataSources = new HashMap<String, ComboPooledDataSource>();

	public DataSource getDataSource(String profile) {
		return dataSources.get(Profiles.getProfile(profile));
	}

	private final ConnectionFactory connectionFactory = new ConnectionFactory();

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	private final DbUnitWrapper dbUnitWrapper = new DbUnitWrapper();

	public DbUnitWrapper getDbUnitWrapper() {
		return dbUnitWrapper;
	}

	private final UnitilsWrapper unitilsWrapper = new UnitilsWrapper();

	public UnitilsWrapper getUnitilsWrapper() {
		return unitilsWrapper;
	}

	private final SetupExecutor setupExecutor = new SetupExecutor();

	public SetupExecutor getSetupExecutor() {
		return setupExecutor;
	}

	private final VerifyExecutor verifyExecutor = new VerifyExecutor();

	public VerifyExecutor getVerifyExecutor() {
		return verifyExecutor;
	}

	private final DataSetResolver dataSetResolver = new DataSetResolver();

	public DataSetResolver getDataSetResolver() {
		return dataSetResolver;
	}

	private final DataSetLoader dataSetLoader = new DataSetLoader();

	public DataSetLoader getDataSetLoader() {
		return dataSetLoader;
	}

	private final DataSetFactory dataSetFactory = new DataSetFactory();

	public DataSetFactory getDataSetFactory() {
		return dataSetFactory;
	}

	private final DataSetAssert dataSetAssert = new DataSetAssert();

	public DataSetAssert getDataSetAssert() {
		return dataSetAssert;
	}

	private final DurationParser durationParser = new DurationParser();

	public DurationParser getDurationParser() {
		return durationParser;
	}

	private final TokenReplacingFilter tokenReplacingFilter = new TokenReplacingFilter();

	public TokenReplacingFilter getTokenReplacingFilter() {
		return tokenReplacingFilter;
	}

	private final VariableResolver variableResolver = new VariableResolver();

	private final AutoPreparedStatementFactory statementFactory = new AutoPreparedStatementFactory();

	public PreparedStatementFactory getStatementFactory() {
		return statementFactory;
	}

	private static final DatabaseOperation INSERT = new AutoInsertOperation();

	private static final DatabaseOperation CLEAN_INSERT = new CompositeOperation(
			DatabaseOperation.DELETE_ALL, INSERT);

	private final HashGenerator hashGenerator = new HashGenerator();
	private final AutoNumberGenerator autoNumberGenerator = new AutoNumberGenerator();
	private final StandardAutoValueGenerator standardAutoValueGenerator = new StandardAutoValueGenerator();

	private final SQLHelper sqlHelper = new SQLHelper();

	/**
	 * Initialize single-instance classes.
	 */
	public void init() {
		log.debug("Initializing factory.");
		propertiesProvider.init();
		initDataSources();
		connectionFactory.setPropertiesProvider(propertiesProvider);
		connectionFactory.setFactory(this);
		dbUnitWrapper.setConnectionFactory(connectionFactory);
		dbUnitWrapper.setPropertiesProvider(propertiesProvider);
		dbUnitWrapper.resetConnectionCache();
		unitilsWrapper.setDbUnitWrapper(dbUnitWrapper);
		unitilsWrapper.setDataSetLoader(dataSetLoader);
		unitilsWrapper.setDataSetAssert(dataSetAssert);
		unitilsWrapper.setFactory(this);
		setupExecutor.setUnitilsWrapper(unitilsWrapper);
		verifyExecutor.setUnitilsWrapper(unitilsWrapper);
		dataSetLoader.setDataSetResolver(dataSetResolver);
		dataSetLoader.setDataSetFactory(dataSetFactory);
		dataSetFactory.setPropertiesProvider(propertiesProvider);
		timeDifferenceLimit = propertiesProvider.getProperty(null,
				TIME_DIFFERENCE_LIMIT, 0);
		tokenReplacingFilter.setDurationParser(durationParser);
		autoNumberGenerator.setHashGenerator(hashGenerator);
		standardAutoValueGenerator.setAutoNumberGenerator(autoNumberGenerator);
		statementFactory.setAutoValueGenerator(standardAutoValueGenerator);
	}

	private void initDataSources() {
		initDataSource(Profiles.DEFAULT_PROFILE);
		for (String profile : propertiesProvider.getProfileNames()) {
			initDataSource(profile);
		}
	}

	private void initDataSource(String profile) {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		final String driverClassName = propertiesProvider.getProperty(profile,
				DRIVER_CLASS_NAME);
		final String connectionUrl = propertiesProvider.getProperty(profile,
				CONNECTION_URL);
		final String userName = propertiesProvider.getProperty(profile,
				USER_NAME);
		final String password = propertiesProvider.getProperty(profile,
				PASSWORD);
		log.info(
				"Initializing DataSource for profile {}, driver [{}], url [{}], user name [{}], password [{}].",
				profile, driverClassName, connectionUrl, userName, password);
		try {
			dataSource.setDriverClass(driverClassName);
		} catch (PropertyVetoException e) {
			throw new DataSourceSetupException(e);
		}
		dataSource.setJdbcUrl(connectionUrl);
		dataSource.setUser(userName);
		dataSource.setPassword(password);
		dataSources.put(profile, dataSource);
	}

	// custom lifecycle classes

	private DataSetProcessingData dataSetProcessingData;

	public DataSetProcessingData getDataSetProcessingData() {
		return dataSetProcessingData;
	}

	public void initDataSetProcessing() {
		dataSetProcessingData = new DataSetProcessingData();
		standardAutoValueGenerator.init();
	}

	// getters for classes always newly instantiated

	public SetupTestRule getSetupTestRule(FrameworkMethod frameworkMethod) {
		SetupTestRule rule = new SetupTestRule(frameworkMethod);
		rule.setSetupExecutor(setupExecutor);
		return rule;
	}

	public SetupListTestRule getSetupListTestRule(
			FrameworkMethod frameworkMethod) {
		SetupListTestRule rule = new SetupListTestRule(frameworkMethod);
		rule.setSetupExecutor(setupExecutor);
		return rule;
	}

	public VerifyTestRule getVerifyTestRule(FrameworkMethod frameworkMethod) {
		variableResolver.clear();
		VerifyTestRule rule = new VerifyTestRule(frameworkMethod);
		rule.setVerifyExecutor(verifyExecutor);
		return rule;
	}

	public VerifyListTestRule getVerifyListTestRule(
			FrameworkMethod frameworkMethod) {
		variableResolver.clear();
		VerifyListTestRule rule = new VerifyListTestRule(frameworkMethod);
		rule.setVerifyExecutor(verifyExecutor);
		return rule;
	}

	public BaseUrlTestRule getBaseUrlTestRule(FrameworkMethod frameworkMethod) {
		return new BaseUrlTestRule(frameworkMethod);
	}

	public IDatabaseConnection createDatabaseConnection(String profile,
			String schemaName) throws DatabaseAccessException,
			CreateDatabaseConnectionException {
		try {
			return new DatabaseConnection(getDataSource(profile)
					.getConnection(), schemaName);
		} catch (DatabaseUnitException e) {
			throw new DatabaseAccessException(e);
		} catch (SQLException e) {
			throw new CreateDatabaseConnectionException(e);
		}
		// unitils caching of dbunit connections does not work with multiple
		// schemas
		// return new DbUnitDatabaseConnection(dataSource, schemaName);
	}

	public ITableMetaData getTableMetaData(String tableName,
			IDatabaseConnection connection, boolean validate,
			boolean caseSensitiveMetaData) throws DataSetException {
		final CustomDatabaseTableMetaData tableMetaData = new CustomDatabaseTableMetaData(
				tableName, connection, true, caseSensitiveMetaData);
		tableMetaData.setSqlHelper(sqlHelper);
		return tableMetaData;
	}

	public net.sf.lightair.internal.dbunit.dataset.Column getColumn(
			String columnName, DataType dataType, String sqlTypeName,
			int nullable, String columnDefaultValue, String remarks,
			String isAutoIncrement, int columnLength, Integer columnPrecision) {
		return new net.sf.lightair.internal.dbunit.dataset.Column(columnName,
				dataType, sqlTypeName,
				org.dbunit.dataset.Column.nullableValue(nullable),
				columnDefaultValue, remarks,
				org.dbunit.dataset.Column.AutoIncrement
						.autoIncrementValue(isAutoIncrement), columnLength,
				columnPrecision);
	}

	// init methods for produced objects

	public void initMergingTable(MergingTable mergingTable) {
		mergingTable.setTokenReplacingFilter(tokenReplacingFilter);
		mergingTable.setDataSetProcessingData(dataSetProcessingData);
	}

	public void initColumn(Column column) {
		column.setVariableResolver(variableResolver);
		column.setTimeDifferenceLimit(timeDifferenceLimit);
		column.setAutoValueGenerator(standardAutoValueGenerator);
	}

	// static method call wrappers

	public DatabaseOperation getCleanInsertDatabaseOperation() {
		return CLEAN_INSERT;
	}

	// properties

	private long timeDifferenceLimit;

	/**
	 * Set time difference limit.
	 * 
	 * @param timeDifferenceLimit
	 */
	public void setTimeDifferenceLimit(long timeDifferenceLimit) {
		this.timeDifferenceLimit = timeDifferenceLimit;
	}

	// access as singleton

	private static final Factory instance = new Factory();

	private Factory() {
		init();
	}

	/**
	 * Return singleton Factory instance.
	 * 
	 * @return Factory instance
	 */
	public static Factory getInstance() {
		return instance;
	}

}
