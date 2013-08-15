package net.sf.lightair.internal.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.operation.DatabaseOperation;
import org.junit.runners.model.FrameworkMethod;

/**
 * Factory class.
 */
public class Factory implements PropertyKeys {

	// single-instance classes and their getters

	private final PropertiesProvider propertiesProvider = new PropertiesProvider();

	public PropertiesProvider getPropertiesProvider() {
		return propertiesProvider;
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

	private final TokenReplacingFilter tokenReplacingFilter = new TokenReplacingFilter();

	public TokenReplacingFilter getTokenReplacingFilter() {
		return tokenReplacingFilter;
	}

	private final VariableResolver variableResolver = new VariableResolver();

	/**
	 * Initialize single-instance classes.
	 */
	public void init() {
		propertiesProvider.init();
		dbUnitWrapper.setPropertiesProvider(propertiesProvider);
		dbUnitWrapper.setFactory(this);
		unitilsWrapper.setDbUnitWrapper(dbUnitWrapper);
		unitilsWrapper.setDataSetLoader(dataSetLoader);
		unitilsWrapper.setDataSetAssert(dataSetAssert);
		unitilsWrapper.setFactory(this);
		dataSetLoader.setDataSetResolver(dataSetResolver);
		dataSetLoader.setDataSetFactory(dataSetFactory);
		dataSetFactory.setPropertiesProvider(propertiesProvider);
		timeDifferenceLimit = propertiesProvider.getProperty(
				TIME_DIFFERENCE_LIMIT, 0);
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

	public IDatabaseConnection createDatabaseConnection(Connection connection,
			String schemaName) throws DatabaseUnitException {
		return new DatabaseConnection(connection, schemaName);
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

	public Connection getConnection(String connectionUrl, String userName,
			String password) throws SQLException {
		return DriverManager.getConnection(connectionUrl, userName, password);
	}

	public DatabaseOperation getCleanInsertDatabaseOperation() {
		return DatabaseOperation.CLEAN_INSERT;
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
