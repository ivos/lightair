package net.sf.lightair.internal.factory;

import net.sf.lightair.internal.dbunit.DbUnitWrapper;
import net.sf.lightair.internal.junit.SetupTestRule;
import net.sf.lightair.internal.junit.VerifyTestRule;
import net.sf.lightair.internal.properties.PropertiesProvider;
import net.sf.lightair.internal.unitils.DataSetFactory;
import net.sf.lightair.internal.unitils.DataSetLoader;
import net.sf.lightair.internal.unitils.UnitilsWrapper;
import net.sf.lightair.internal.util.DataSetResolver;

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

	// initialize single-instance classes

	private void init() {
		dbUnitWrapper.setPropertiesProvider(propertiesProvider);
		unitilsWrapper.setDbUnitWrapper(dbUnitWrapper);
		unitilsWrapper.setDataSetLoader(dataSetLoader);
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

	// access as singleton

	private static final Factory instance = new Factory();

	private Factory() {
		init();
	}

	public static Factory getInstance() {
		return instance;
	}

}
