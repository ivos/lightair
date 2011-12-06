package net.sf.lightair.internal.junit;

import java.sql.SQLException;

import net.sf.lightair.annotation.Verify;

import org.apache.commons.lang.time.StopWatch;
import org.dbunit.DatabaseUnitException;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerifyTestRule extends AbstractTestRule<Verify> {

	private final Logger log = LoggerFactory.getLogger(VerifyTestRule.class);

	public VerifyTestRule(FrameworkMethod frameworkMethod) {
		super(frameworkMethod, Verify.class);
	}

	@Override
	protected void after() throws ClassNotFoundException, SQLException,
			DatabaseUnitException {
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
