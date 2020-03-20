package net.sf.lightair.internal.junit;

import net.sf.lightair.internal.junit.util.DataSetResolver;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExecutorUtils {

	public static Map<String, List<String>> getApiFileNames(
			DataSetResolver dataSetResolver, String profile, Method testMethod, String[] fileNames, String suffix) {
		Map<String, List<String>> apiFileNames = new LinkedHashMap<>();
		List<URL> urls = dataSetResolver.resolve(profile, testMethod, suffix, fileNames);
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
		return apiFileNames;
	}

	public static StopWatch startStopWatch(Logger log) {
		StopWatch stopWatch = null;
		if (log.isDebugEnabled()) {
			stopWatch = new StopWatch();
			stopWatch.start();
		}
		return stopWatch;
	}
}
