package test.support;

import java.util.Collections;

import net.sf.lightair.LightAir;

import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

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
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        Statement statement = super.methodInvoker(method, test);
        return new RunRules(statement, Collections.<TestRule>singleton(new ExceptionVerifyingTestRule(method.getMethod(),
                testObject)), describeChild(method));
    }

}
