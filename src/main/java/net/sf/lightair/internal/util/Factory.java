package net.sf.lightair.internal.util;

import net.sf.lightair.internal.junit.SetupExecutor;
import net.sf.lightair.internal.junit.SetupListTestRule;
import net.sf.lightair.internal.junit.SetupTestRule;
import net.sf.lightair.internal.junit.VerifyExecutor;
import net.sf.lightair.internal.junit.VerifyListTestRule;
import net.sf.lightair.internal.junit.VerifyTestRule;
import org.junit.rules.TestRule;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Factory class.
 */
public class Factory {

	private final Logger log = LoggerFactory.getLogger(Factory.class);

	// single-instance classes and their getters

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

	/**
	 * Initialize single-instance classes.
	 */
	public void init() {
		log.debug("Initializing factory.");
		setupExecutor.setDataSetResolver(dataSetResolver);
		verifyExecutor.setDataSetResolver(dataSetResolver);
	}

	// getters for classes always newly instantiated

	public SetupTestRule getSetupTestRule(FrameworkMethod frameworkMethod) {
		SetupTestRule rule = new SetupTestRule(frameworkMethod);
		rule.setSetupExecutor(setupExecutor);
		return rule;
	}

	public SetupListTestRule getSetupListTestRule(FrameworkMethod frameworkMethod) {
		SetupListTestRule rule = new SetupListTestRule(frameworkMethod);
		rule.setSetupExecutor(setupExecutor);
		return rule;
	}

	public VerifyTestRule getVerifyTestRule(FrameworkMethod frameworkMethod) {
		VerifyTestRule rule = new VerifyTestRule(frameworkMethod);
		rule.setVerifyExecutor(verifyExecutor);
		return rule;
	}

	public VerifyListTestRule getVerifyListTestRule(FrameworkMethod frameworkMethod) {
		VerifyListTestRule rule = new VerifyListTestRule(frameworkMethod);
		rule.setVerifyExecutor(verifyExecutor);
		return rule;
	}

	public List<TestRule> getAllTestRules(FrameworkMethod method) {
		return Arrays.asList((TestRule) getSetupTestRule(method),
				(TestRule) getSetupListTestRule(method),
				(TestRule) getVerifyTestRule(method),
				(TestRule) getVerifyListTestRule(method));
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
