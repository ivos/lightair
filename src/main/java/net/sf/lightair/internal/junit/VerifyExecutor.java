package net.sf.lightair.internal.junit;

import net.sf.lightair.Api;
import net.sf.lightair.annotation.Verify;
import net.sf.lightair.internal.junit.util.DataSetResolver;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class VerifyExecutor {

	private final Logger log = LoggerFactory.getLogger(VerifyExecutor.class);

	public void execute(Verify verify, Method testMethod) {
		String[] fileNames = verify.value();
		String profile = verify.profile();
		log.info("Verifying database for test method {} and profile {} with configured file names {}.",
				testMethod, profile, fileNames);
		StopWatch stopWatch = ExecutorUtils.startStopWatch(log);

		Map<String, List<String>> apiFileNames = ExecutorUtils.getApiFileNames(
				dataSetResolver, profile, testMethod, fileNames, "-verify");
		Api.verify(apiFileNames);

		if (null != stopWatch) {
			stopWatch.stop();
			log.debug("Database verified in {} ms.", stopWatch.getTime());
		}
	}

	// beans and their setters:

	private DataSetResolver dataSetResolver;

	/**
	 * Set dataset resolver
	 *
	 * @param dataSetResolver resolver
	 */
	public void setDataSetResolver(DataSetResolver dataSetResolver) {
		this.dataSetResolver = dataSetResolver;
	}
}
