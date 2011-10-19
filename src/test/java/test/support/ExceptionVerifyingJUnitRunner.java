package test.support;

import java.util.ArrayList;
import java.util.List;

import net.sf.lightair.LightAir;

import org.junit.rules.TestRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class ExceptionVerifyingJUnitRunner extends LightAir {

	public ExceptionVerifyingJUnitRunner(Class<?> clazz)
			throws InitializationError {
		super(clazz);
	}

	// store test object

	@Override
	protected Object createTest() throws Exception {
		if (null == testObject) {
			testObject = super.createTest();
		}
		return testObject;
	}

	private Object testObject;

	// add test rule to verify exception

	@Override
	protected List<TestRule> createTestRules(FrameworkMethod method) {
		List<TestRule> superTestRules = super.createTestRules(method);
		List<TestRule> testRules = new ArrayList<TestRule>();
		testRules.addAll(superTestRules);
		testRules.add(new ExceptionVerifyingTestRule(method.getMethod(),
				testObject));
		return testRules;
	}

}
