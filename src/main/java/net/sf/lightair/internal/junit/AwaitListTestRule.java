package net.sf.lightair.internal.junit;

import net.sf.lightair.annotation.Await;
import org.junit.runners.model.FrameworkMethod;

/**
 * JUnit test rule to verify database with multiple <code>@Await</code>
 * annotations after test method execution with asynchronous functionality.
 */
public class AwaitListTestRule extends AbstractTestRule<Await.List> {

	/**
	 * Constructor.
	 *
	 * @param frameworkMethod JUnit framework method on which the test rule is being applied
	 */
	public AwaitListTestRule(FrameworkMethod frameworkMethod) {
		super(frameworkMethod, Await.List.class, Await.class);
	}

	/**
	 * If the method is annotated with @{@link Await}, set up the database.
	 */
	@Override
	protected void after() {
		if (null != getAnnotation()) {
			Await[] verifies = getAnnotation().value();
			for (Await await : verifies) {
				awaitExecutor.execute(await, getTestMethod());
			}
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
