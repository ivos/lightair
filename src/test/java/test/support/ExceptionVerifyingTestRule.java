package test.support;

import java.lang.reflect.Method;

public class ExceptionVerifyingTestRule {

	private final Method verifyMethod;
	private final Object testObject;

	public ExceptionVerifyingTestRule(Method testMethod, Object testObject) {
		this.testObject = testObject;
		try {
			verifyMethod = testMethod.getDeclaringClass().getMethod(
				testMethod.getName() + "VerifyException", Throwable.class);
		} catch (Exception e) {
			throw new IllegalArgumentException(
				"Exception verification method not found for test method " + testMethod, e);
		}
	}

	public void verifyException(Throwable ae) {
		try {
			verifyMethod.invoke(testObject, ae);
		} catch (Exception e) {
			Throwable cause = e.getCause();
			if (cause instanceof AssertionError) {
				throw (AssertionError) cause;
			}
			throw new RuntimeException("Error invoking exception verification method " + verifyMethod, e);
		}
	}
}
