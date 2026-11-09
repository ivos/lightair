package net.sf.lightair.internal.junit;

import net.sf.lightair.annotation.Verify;

import java.lang.reflect.Method;

/**
 * Test rule helper to verify the database with multiple <code>@Verify</code>
 * annotations after test method execution.
 */
public class VerifyListTestRule extends AbstractTestRule<Verify.List> {

	/**
	 * Constructor.
	 *
	 * @param testMethod Method on which the test rule is being applied
	 */
	public VerifyListTestRule(Method testMethod) {
		super(testMethod, Verify.List.class, Verify.class);
	}

	/**
	 * If the method is annotated with @{@link Verify}, verify the database.
	 */
	@Override
	public void after() {
		if (null != getAnnotation()) {
			Verify[] verifies = getAnnotation().value();
			for (Verify verify : verifies) {
				verifyExecutor.execute(verify, getTestMethod());
			}
		}
	}

	// beans and their setters:

	private VerifyExecutor verifyExecutor;

	/**
	 * Set the verify executor.
	 *
	 * @param verifyExecutor executor
	 */
	public void setVerifyExecutor(VerifyExecutor verifyExecutor) {
		this.verifyExecutor = verifyExecutor;
	}
}
