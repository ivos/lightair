package net.sf.lightair.support.factory;

import net.sf.lightair.support.dbunit.DataSetLoader;
import net.sf.lightair.support.dbunit.DbUnitWrapper;
import net.sf.lightair.support.junit.SetupTestRule;
import net.sf.lightair.support.junit.VerifyTestRule;
import net.sf.lightair.support.properties.PropertiesProvider;

import org.junit.runners.model.FrameworkMethod;

public class Factory {

	// single-instance classes and getters

	private final PropertiesProvider propertiesProvider = new PropertiesProvider();

	public PropertiesProvider getPropertiesProvider() {
		return propertiesProvider;
	}

	private final DbUnitWrapper dbUnitWrapper = new DbUnitWrapper();

	public DbUnitWrapper getDbUnitWrapper() {
		return dbUnitWrapper;
	}

	private final DataSetLoader dataSetLoader = new DataSetLoader();

	public DataSetLoader getDataSetLoader() {
		return dataSetLoader;
	}

	// initialize single-instance classes

	private void init() {
		dbUnitWrapper.setPropertiesProvider(propertiesProvider);
		dataSetLoader.setDbUnit(dbUnitWrapper);
	}

	// getters for classes always newly instantiated

	public VerifyTestRule getVerifyTestRule(FrameworkMethod frameworkMethod) {
		VerifyTestRule rule = new VerifyTestRule(frameworkMethod);
		rule.setDbUnitWrapper(dbUnitWrapper);
		rule.setDataSetLoader(dataSetLoader);
		return rule;
	}

	public SetupTestRule getSetupTestRule(FrameworkMethod frameworkMethod) {
		SetupTestRule rule = new SetupTestRule(frameworkMethod);
		rule.setDbUnitWrapper(dbUnitWrapper);
		rule.setDataSetLoader(dataSetLoader);
		return rule;
	}

	// access as singleton

	private static final Factory instance = new Factory();

	private Factory() {
		init();
	}

	public static Factory getInstance() {
		return instance;
	}

}
