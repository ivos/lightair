package net.sf.lightair.support.factory;

import net.sf.lightair.support.dbunit.DbUnitWrapper;
import net.sf.lightair.support.junit.SetupTestRule;
import net.sf.lightair.support.junit.VerifyTestRule;
import net.sf.lightair.support.properties.PropertiesProvider;
import net.sf.lightair.support.unitils.DataSetLoader;
import net.sf.lightair.support.unitils.UnitilsWrapper;
import net.sf.lightair.support.util.DataSetResolver;

import org.junit.runners.model.FrameworkMethod;
import org.unitils.dbunit.datasetfactory.impl.MultiSchemaXmlDataSetFactory;

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

	private final net.sf.lightair.support.dbunit.DataSetLoader dataSetLoader2 = new net.sf.lightair.support.dbunit.DataSetLoader();

	public net.sf.lightair.support.dbunit.DataSetLoader getDataSetLoader2() {
		return dataSetLoader2;
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

	// initialize single-instance classes

	private void init() {
		dbUnitWrapper.setPropertiesProvider(propertiesProvider);
		dataSetLoader2.setDbUnit(dbUnitWrapper);
		unitilsWrapper.setDbUnitWrapper(dbUnitWrapper);
		unitilsWrapper.setDataSetLoader(dataSetLoader);
		dataSetLoader.setDataSetResolver(dataSetResolver);
		dataSetLoader.setDataSetFactory(new MultiSchemaXmlDataSetFactory());
	}

	// getters for classes always newly instantiated

	public VerifyTestRule getVerifyTestRule(FrameworkMethod frameworkMethod) {
		VerifyTestRule rule = new VerifyTestRule(frameworkMethod);
		rule.setDbUnitWrapper(dbUnitWrapper);
		rule.setDataSetLoader(dataSetLoader2);
		return rule;
	}

	public SetupTestRule getSetupTestRule(FrameworkMethod frameworkMethod) {
		SetupTestRule rule = new SetupTestRule(frameworkMethod);
		rule.setUnitilsWrapper(unitilsWrapper);
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
