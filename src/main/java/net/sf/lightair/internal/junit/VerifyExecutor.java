package net.sf.lightair.internal.junit;

import java.lang.reflect.Method;

import net.sf.lightair.annotation.Verify;
import net.sf.lightair.internal.unitils.UnitilsWrapper;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerifyExecutor {

	private final Logger log = LoggerFactory.getLogger(VerifyExecutor.class);

	public void execute(Verify verify, Method testMethod) {
		String[] fileNames = verify.value();
		String profile = verify.profile();
		log.info("Verifying database for test method {} "
				+ "and profile {} with configured file names {}.", testMethod,
				profile, fileNames);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		unitilsWrapper.verify(testMethod, profile, fileNames);
		stopWatch.stop();
		log.debug("Database verified in {} ms.", stopWatch.getTime());
	}

	// beans and their setters:

	protected UnitilsWrapper unitilsWrapper;

	/**
	 * Set Unitils wrapper.
	 * 
	 * @param unitilsWrapper
	 *            Unitils wrapper
	 */
	public void setUnitilsWrapper(UnitilsWrapper unitilsWrapper) {
		this.unitilsWrapper = unitilsWrapper;
	}

}
