package net.sf.lightair.internal.junit;

import net.sf.lightair.annotation.Setup;

import org.apache.commons.lang.time.StopWatch;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit test rule to setup database before test method execution.
 */
public class SetupTestRule extends AbstractTestRule<Setup> {

	private final Logger log = LoggerFactory.getLogger(SetupTestRule.class);

	/**
	 * Constructor.
	 * 
	 * @param frameworkMethod
	 *            JUnit framework method on which the test rule is being applied
	 */
	public SetupTestRule(FrameworkMethod frameworkMethod) {
		super(frameworkMethod, Setup.class);
	}

	/**
	 * If the method is annotated with @{@link Setup}, set up the database.
	 */
	@Override
	protected void before() {
		if (null != getAnnotation()) {
			String[] fileNames = getAnnotation().value();
			log.info("Setting up database for test method {} "
					+ "with configured file names {}.", getTestMethod(),
					fileNames);
			StopWatch stopWatch = new StopWatch();
			stopWatch.start();
			unitilsWrapper.setup(getTestMethod(), fileNames);
			stopWatch.stop();
			log.debug("Database set up in {} ms.", stopWatch.getTime());
		}
	}

}
