package net.sf.lightair.internal.cli;

import net.sf.lightair.LightAirApi;

import java.util.List;
import java.util.Map;

public class LightAirCli {

	public static void generateXsd(String propertiesFileName) {
		LightAirApi.initialize(propertiesFileName);
		try {
			LightAirApi.generateXsd();
		} finally {
			LightAirApi.shutdown();
		}
	}

	public static void setup(String propertiesFileName, Map<String, List<String>> fileNames) {
		LightAirApi.initialize(propertiesFileName);
		try {
			LightAirApi.setup(fileNames);
		} finally {
			LightAirApi.shutdown();
		}
	}

	public static void verify(String propertiesFileName, Map<String, List<String>> fileNames) {
		LightAirApi.initialize(propertiesFileName);
		try {
			LightAirApi.verify(fileNames);
		} finally {
			LightAirApi.shutdown();
		}
	}

	public static void await(String propertiesFileName, Map<String, List<String>> fileNames) {
		LightAirApi.initialize(propertiesFileName);
		try {
			LightAirApi.await(fileNames);
		} finally {
			LightAirApi.shutdown();
		}
	}

}
