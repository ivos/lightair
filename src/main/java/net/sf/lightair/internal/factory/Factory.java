package net.sf.lightair.internal.factory;

import java.beans.PropertyVetoException;
import java.sql.SQLException;

import javax.sql.DataSource;

import net.sf.lightair.exception.CreateDatabaseConnectionException;
import net.sf.lightair.exception.DataSourceSetupException;
import net.sf.lightair.exception.DatabaseAccessException;
import net.sf.lightair.internal.dbunit.AutoPreparedStatementFactory;
import net.sf.lightair.internal.dbunit.ConnectionFactory;
import net.sf.lightair.internal.dbunit.DbUnitWrapper;
import net.sf.lightair.internal.dbunit.dataset.MergingTable;
import net.sf.lightair.internal.dbunit.dataset.TokenReplacingFilter;
import net.sf.lightair.internal.junit.BaseUrlTestRule;
import net.sf.lightair.internal.junit.SetupTestRule;
import net.sf.lightair.internal.junit.VerifyTestRule;
import net.sf.lightair.internal.properties.PropertiesProvider;
import net.sf.lightair.internal.properties.PropertyKeys;
import net.sf.lightair.internal.unitils.DataSetFactory;
import net.sf.lightair.internal.unitils.DataSetLoader;
import net.sf.lightair.internal.unitils.UnitilsWrapper;
import net.sf.lightair.internal.unitils.compare.Column;
import net.sf.lightair.internal.unitils.compare.DataSetAssert;
import net.sf.lightair.internal.unitils.compare.VariableResolver;
import net.sf.lightair.internal.util.DataSetProcessingData;
import net.sf.lightair.internal.util.DataSetResolver;
import net.sf.lightair.internal.util.DurationParser;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.statement.PreparedStatementFactory;
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

	private ComboPooledDataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
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

	private final PreparedStatementFactory statementFactory = new AutoPreparedStatementFactory();

	public PreparedStatementFactory getStatementFactory() {
		return statementFactory;
	}

	private static final DatabaseOperation INSERT = new AutoInsertOperation();

	private static final DatabaseOperation CLEAN_INSERT = new CompositeOperation(
			DatabaseOperation.DELETE_ALL, INSERT);

	/**
	 * Initialize single-instance classes.
	 */
	public void init() {
		log.debug("Initializing factory.");
		propertiesProvider.init();
		initDataSource();
		connectionFactory.setPropertiesProvider(propertiesProvider);
		connectionFactory.setFactory(this);
		dbUnitWrapper.setConnectionFactory(connectionFactory);
		dbUnitWrapper.setPropertiesProvider(propertiesProvider);
		dbUnitWrapper.resetConnectionCache();
		unitilsWrapper.setDbUnitWrapper(dbUnitWrapper);
		unitilsWrapper.setDataSetLoader(dataSetLoader);
		unitilsWrapper.setDataSetAssert(dataSetAssert);
		unitilsWrapper.setFactory(this);
		dataSetLoader.setDataSetResolver(dataSetResolver);
		dataSetLoader.setDataSetFactory(dataSetFactory);
		dataSetFactory.setPropertiesProvider(propertiesProvider);
		timeDifferenceLimit = propertiesProvider.getProperty(
				TIME_DIFFERENCE_LIMIT, 0);
		tokenReplacingFilter.setDurationParser(durationParser);
	}

	private void initDataSource() {
		dataSource = new ComboPooledDataSource();
		final String driverClassName = propertiesProvider
				.getProperty(DRIVER_CLASS_NAME);
		final String connectionUrl = propertiesProvider
				.getProperty(CONNECTION_URL);
		final String userName = propertiesProvider.getProperty(USER_NAME);
		final String password = propertiesProvider.getProperty(PASSWORD);
		log.info(
				"Initializing DataSource for driver [{}], url [{}], user name [{}], password [{}].",
				driverClassName, connectionUrl, userName, password);
		try {
			dataSource.setDriverClass(driverClassName);
		} catch (PropertyVetoException e) {
			throw new DataSourceSetupException(e);
		}
		dataSource.setJdbcUrl(connectionUrl);
		dataSource.setUser(userName);
		dataSource.setPassword(password);
	}

	// custom lifecycle classes

	private DataSetProcessingData dataSetProcessingData;

	public DataSetProcessingData getDataSetProcessingData() {
		return dataSetProcessingData;
	}

	public void initDataSetProcessing() {
		dataSetProcessingData = new DataSetProcessingData();
	}

	// getters for classes always newly instantiated

	public SetupTestRule getSetupTestRule(FrameworkMethod frameworkMethod) {
		SetupTestRule rule = new SetupTestRule(frameworkMethod);
		rule.setUnitilsWrapper(unitilsWrapper);
		return rule;
	}

	public VerifyTestRule getVerifyTestRule(FrameworkMethod frameworkMethod) {
		variableResolver.clear();
		VerifyTestRule rule = new VerifyTestRule(frameworkMethod);
		rule.setUnitilsWrapper(unitilsWrapper);
		return rule;
	}

	public BaseUrlTestRule getBaseUrlTestRule(FrameworkMethod frameworkMethod) {
		return new BaseUrlTestRule(frameworkMethod);
	}

	public IDatabaseConnection createDatabaseConnection(String schemaName)
			throws DatabaseAccessException, CreateDatabaseConnectionException {
		try {
			return new DatabaseConnection(dataSource.getConnection(),
					schemaName);
		} catch (DatabaseUnitException e) {
			throw new DatabaseAccessException(e);
		} catch (SQLException e) {
			throw new CreateDatabaseConnectionException(e);
		}
		// unitils caching of dbunit connections does not work with multiple
		// schemas
		// return new DbUnitDatabaseConnection(dataSource, schemaName);
	}

	// init methods for produced objects

	public void initMergingTable(MergingTable mergingTable) {
		mergingTable.setTokenReplacingFilter(tokenReplacingFilter);
		mergingTable.setDataSetProcessingData(dataSetProcessingData);
	}

	public void initColumn(Column column) {
		column.setVariableResolver(variableResolver);
		column.setTimeDifferenceLimit(timeDifferenceLimit);
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
