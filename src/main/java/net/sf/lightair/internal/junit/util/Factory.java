package net.sf.lightair.internal.junit.util;

import net.sf.lightair.internal.junit.AbstractTestRule;
import net.sf.lightair.internal.junit.AwaitExecutor;
import net.sf.lightair.internal.junit.AwaitListTestRule;
import net.sf.lightair.internal.junit.AwaitTestRule;
import net.sf.lightair.internal.junit.SetupExecutor;
import net.sf.lightair.internal.junit.SetupListTestRule;
import net.sf.lightair.internal.junit.SetupTestRule;
import net.sf.lightair.internal.junit.VerifyExecutor;
import net.sf.lightair.internal.junit.VerifyListTestRule;
import net.sf.lightair.internal.junit.VerifyTestRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
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

	private final AwaitExecutor awaitExecutor = new AwaitExecutor();

	public AwaitExecutor getAwaitExecutor() {
		return awaitExecutor;
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
		awaitExecutor.setDataSetResolver(dataSetResolver);
	}

	// getters for classes always newly instantiated

	public SetupTestRule getSetupTestRule(Method testMethod) {
		SetupTestRule rule = new SetupTestRule(testMethod);
		rule.setSetupExecutor(setupExecutor);
		return rule;
	}

	public SetupListTestRule getSetupListTestRule(Method testMethod) {
		SetupListTestRule rule = new SetupListTestRule(testMethod);
		rule.setSetupExecutor(setupExecutor);
		return rule;
	}

	public VerifyTestRule getVerifyTestRule(Method testMethod) {
		VerifyTestRule rule = new VerifyTestRule(testMethod);
		rule.setVerifyExecutor(verifyExecutor);
		return rule;
	}

	public VerifyListTestRule getVerifyListTestRule(Method testMethod) {
		VerifyListTestRule rule = new VerifyListTestRule(testMethod);
		rule.setVerifyExecutor(verifyExecutor);
		return rule;
	}

	public AwaitTestRule getAwaitTestRule(Method testMethod) {
		AwaitTestRule rule = new AwaitTestRule(testMethod);
		rule.setAwaitExecutor(awaitExecutor);
		return rule;
	}

	public AwaitListTestRule getAwaitListTestRule(Method testMethod) {
		AwaitListTestRule rule = new AwaitListTestRule(testMethod);
		rule.setAwaitExecutor(awaitExecutor);
		return rule;
	}

	public List<AbstractTestRule<?>> getAllTestRules(Method testMethod) {
		return Arrays.asList(
			getSetupTestRule(testMethod),
			getSetupListTestRule(testMethod),
			getVerifyTestRule(testMethod),
			getVerifyListTestRule(testMethod),
			getAwaitTestRule(testMethod),
			getAwaitListTestRule(testMethod)
		);
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
