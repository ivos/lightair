package net.sf.lightair.internal.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.sf.lightair.internal.dbunit.DbUnitWrapper;
import net.sf.lightair.internal.dbunit.dataset.MergingTable;
import net.sf.lightair.internal.dbunit.dataset.TokenReplacingFilter;
import net.sf.lightair.internal.junit.SetupTestRule;
import net.sf.lightair.internal.junit.VerifyTestRule;
import net.sf.lightair.internal.properties.PropertiesProvider;
import net.sf.lightair.internal.unitils.DataSetFactory;
import net.sf.lightair.internal.unitils.DataSetLoader;
import net.sf.lightair.internal.unitils.UnitilsWrapper;
import net.sf.lightair.internal.unitils.compare.Column;
import net.sf.lightair.internal.unitils.compare.DataSetAssert;
import net.sf.lightair.internal.unitils.compare.VariableResolver;
import net.sf.lightair.internal.util.DataSetResolver;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.operation.DatabaseOperation;
import org.junit.runners.model.FrameworkMethod;

/**
 * Factory class.
 */
public class Factory {

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
	private void init() {
		dbUnitWrapper.setPropertiesProvider(propertiesProvider);
		dbUnitWrapper.setFactory(this);
		unitilsWrapper.setDbUnitWrapper(dbUnitWrapper);
		unitilsWrapper.setDataSetLoader(dataSetLoader);
		unitilsWrapper.setDataSetAssert(dataSetAssert);
		unitilsWrapper.setFactory(this);
		dataSetLoader.setDataSetResolver(dataSetResolver);
		dataSetLoader.setDataSetFactory(dataSetFactory);
		dataSetFactory.setPropertiesProvider(propertiesProvider);
	}

	// getters for classes always newly instantiated

	public VerifyTestRule getVerifyTestRule(FrameworkMethod frameworkMethod) {
		VerifyTestRule rule = new VerifyTestRule(frameworkMethod);
		rule.setUnitilsWrapper(unitilsWrapper);
		return rule;
	}

	public SetupTestRule getSetupTestRule(FrameworkMethod frameworkMethod) {
		SetupTestRule rule = new SetupTestRule(frameworkMethod);
		rule.setUnitilsWrapper(unitilsWrapper);
		return rule;
	}

	public IDatabaseConnection createDatabaseConnection(Connection connection,
			String schemaName) throws DatabaseUnitException {
		return new DatabaseConnection(connection, schemaName);
	}

	// init methods

	public void initMergingTable(MergingTable mergingTable) {
		mergingTable.setTokenReplacingFilter(tokenReplacingFilter);
	}

	public void initColumn(Column column) {
		column.setVariableResolver(variableResolver);
	}

	// static method call wrappers

	public Connection getConnection(String connectionUrl, String userName,
			String password) throws SQLException {
		return DriverManager.getConnection(connectionUrl, userName, password);
	}

	public DatabaseOperation getCleanInsertDatabaseOperation() {
		return DatabaseOperation.CLEAN_INSERT;
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
