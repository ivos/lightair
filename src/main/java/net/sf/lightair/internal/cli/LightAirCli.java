package net.sf.lightair.internal.cli;

import net.sf.lightair.LightAirApi;

import java.util.List;
import java.util.Map;

public class LightAirCli {

	public static void generateXsd(String propertiesFileName) {
		LightAirApi.initialize(propertiesFileName);
		LightAirApi.generateXsd();
		LightAirApi.shutdown();
	}

	public static void setup(String propertiesFileName, Map<String, List<String>> fileNames) {
		LightAirApi.initialize(propertiesFileName);
		LightAirApi.setup(fileNames);
		LightAirApi.shutdown();
	}

	public static void verify(String propertiesFileName, Map<String, List<String>> fileNames) {
		LightAirApi.initialize(propertiesFileName);
		LightAirApi.verify(fileNames);
		LightAirApi.shutdown();
	}

	public static void await(String propertiesFileName, Map<String, List<String>> fileNames) {
		LightAirApi.initialize(propertiesFileName);
		LightAirApi.await(fileNames);
		LightAirApi.shutdown();
	}

}
