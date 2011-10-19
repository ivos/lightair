package test.support;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ExceptionVerifyingTestRule implements TestRule {

	private final Method verifyMethod;
	private final Object testObject;

	public ExceptionVerifyingTestRule(Method testMethod, Object testObject) {
		this.testObject = testObject;
		try {
			verifyMethod = testMethod.getDeclaringClass().getMethod(
					testMethod.getName() + "VerifyException",
					AssertionError.class);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Exception verification method not found for test method "
							+ testMethod, e);
		}
	}

	public Statement apply(final Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				try {
					base.evaluate();
				} catch (AssertionError ae) {
					verifyException(ae);
					return;
				}
				fail("Should throw");
			}
		};
	}

	private void verifyException(AssertionError ae) {
		try {
			verifyMethod.invoke(testObject, ae);
		} catch (Exception e) {
			Throwable cause = e.getCause();
			if (cause instanceof AssertionError) {
				throw (AssertionError) cause;
			}
			throw new RuntimeException(
					"Error invoking exception verification method "
							+ verifyMethod, e);
		}
	}
}
