package net.sf.lightair.internal.junit;

import net.sf.lightair.annotation.Verify;

import org.junit.runners.model.FrameworkMethod;

/**
 * JUnit test rule to verify database with multiple <code>@Verify</code>
 * annotations after test method execution.
 */
public class VerifyListTestRule extends AbstractTestRule<Verify.List> {

	/**
	 * Constructor.
	 * 
	 * @param frameworkMethod
	 *            JUnit framework method on which the test rule is being applied
	 */
	public VerifyListTestRule(FrameworkMethod frameworkMethod) {
		super(frameworkMethod, Verify.List.class, Verify.class);
	}

	/**
	 * If the method is annotated with @{@link Verify}, set up the database.
	 */
	@Override
	protected void after() {
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
	 * Set verify executor.
	 * 
	 * @param verifyExecutor executor
	 */
	public void setVerifyExecutor(VerifyExecutor verifyExecutor) {
		this.verifyExecutor = verifyExecutor;
	}
}
