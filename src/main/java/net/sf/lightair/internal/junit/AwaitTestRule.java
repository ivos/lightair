package net.sf.lightair.internal.junit;

import net.sf.lightair.annotation.Await;
import org.junit.runners.model.FrameworkMethod;

/**
 * JUnit test rule to verify database after test method execution with asynchronous functionality.
 */
public class AwaitTestRule extends AbstractTestRule<Await> {

	/**
	 * Constructor.
	 *
	 * @param frameworkMethod JUnit framework method on which the test rule is being applied
	 */
	public AwaitTestRule(FrameworkMethod frameworkMethod) {
		super(frameworkMethod, Await.class, Await.List.class);
	}

	/**
	 * If the method is annotated with @{@link Await}, verify the database asynchronously.
	 */
	@Override
	protected void after() {
		if (null != getAnnotation()) {
			awaitExecutor.execute(getAnnotation(), getTestMethod());
		}
	}

	// beans and their setters:

	private AwaitExecutor awaitExecutor;

	/**
	 * Set await executor.
	 *
	 * @param awaitExecutor executor
	 */
	public void setAwaitExecutor(AwaitExecutor awaitExecutor) {
		this.awaitExecutor = awaitExecutor;
	}
}
