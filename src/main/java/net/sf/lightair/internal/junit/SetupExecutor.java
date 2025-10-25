package net.sf.lightair.internal.junit;

import net.sf.lightair.LightAirApi;
import net.sf.lightair.annotation.Setup;
import net.sf.lightair.internal.junit.util.DataSetResolver;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class SetupExecutor {

	private final Logger log = LoggerFactory.getLogger(SetupExecutor.class);

	public void execute(Setup setup, Method testMethod) {
		String[] fileNames = setup.value();
		String profile = setup.profile();
		log.info("Setting up database for test method {} and profile {} with configured file names {}.",
				testMethod, profile, fileNames);
		StopWatch stopWatch = ExecutorUtils.startStopWatch(log);

		Map<String, List<String>> apiFileNames = ExecutorUtils.getApiFileNames(
				dataSetResolver, profile, testMethod, fileNames, "");
		LightAirApi.setup(apiFileNames);

		if (null != stopWatch) {
			stopWatch.stop();
			log.debug("Database set up in {} ms.", stopWatch.getTime());
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
