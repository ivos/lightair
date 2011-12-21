package net.sf.lightair.internal.junit;

import net.sf.lightair.annotation.Verify;

import org.apache.commons.lang.time.StopWatch;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit test rule to verify database after test method execution.
 */
public class VerifyTestRule extends AbstractTestRule<Verify> {

	private final Logger log = LoggerFactory.getLogger(VerifyTestRule.class);

	/**
	 * Constructor.
	 * 
	 * @param frameworkMethod
	 *            JUnit framework method on which the test rule is being applied
	 */
	public VerifyTestRule(FrameworkMethod frameworkMethod) {
		super(frameworkMethod, Verify.class);
	}

	/**
	 * If the method is annotated with @{@link Verify}, set up the database.
	 */
	@Override
	protected void after() {
		if (null != getAnnotation()) {
			String[] fileNames = getAnnotation().value();
			log.info("Verifying database for test method {} "
					+ "with configured file names {}.", getTestMethod(),
					fileNames);
			StopWatch stopWatch = new StopWatch();
			stopWatch.start();
			unitilsWrapper.verify(getTestMethod(), fileNames);
			stopWatch.stop();
			log.debug("Database verified in {} ms.", stopWatch.getTime());
		}
	}

}
