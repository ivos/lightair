package net.sf.lightair.internal.junit;

import net.sf.lightair.annotation.Setup;
import net.sf.lightair.Api;
import net.sf.lightair.internal.util.DataSetResolver;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SetupExecutor {

	private final Logger log = LoggerFactory.getLogger(SetupExecutor.class);

	public void execute(Setup setup, Method testMethod) {
		String[] fileNames = setup.value();
		String profile = setup.profile();
		log.info("Setting up database for test method {} " + "and profile {} with configured file names {}.",
				testMethod, profile, fileNames);
		StopWatch stopWatch = null;
		if (log.isDebugEnabled()) {
			stopWatch = new StopWatch();
			stopWatch.start();
		}

		Map<String, List<String>> apiFileNames = new LinkedHashMap<>();
		List<URL> urls = dataSetResolver.resolve(profile, testMethod, "", fileNames);
		List<String> filePaths = urls.stream()
				.map(url -> {
					try {
						return new File(url.toURI()).getPath();
					} catch (URISyntaxException e) {
						throw new RuntimeException("Data set not found " + Arrays.toString(fileNames) + ".", e);
					}
				})
				.collect(Collectors.toList());
		apiFileNames.put(profile, filePaths);
		Api.setup(apiFileNames);

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
	 * @param dataSetResolver
	 */
	public void setDataSetResolver(DataSetResolver dataSetResolver) {
		this.dataSetResolver = dataSetResolver;
	}
}
