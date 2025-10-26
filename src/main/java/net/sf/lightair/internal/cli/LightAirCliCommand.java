package net.sf.lightair.internal.cli;

import net.sf.lightair.internal.Keywords;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Command(
		name = "light-air",
		mixinStandardHelpOptions = true,
		versionProvider = ManifestVersionProvider.class,
		description = "Light Air: Test database applications declaratively"
)
public class LightAirCliCommand implements Callable<Integer>, Keywords {

	@CommandLine.Spec
	CommandLine.Model.CommandSpec spec;

	@Parameters(
			index = "0",
			description = "The action to perform."
	)
	private LightAirCliAction action;

	@Option(
			names = {"-p", "--properties"},
			description = "Light Air properties file.",
			defaultValue = "light-air.properties"
	)
	private String propertiesFileName;

	@Option(
			names = {"-f", "--file"},
			description = {
					"Light Air dataset file(s). Optionally prefix with profile and colon.",
					"Example, default profile: -f my-setup.xml",
					"Example, profile \"users\": -f users:users-setup.xml",
			}
	)
	private List<String> fileNames;

	@Override
	public Integer call() {
		switch (action) {
			case generateXsd:
				LightAirCli.generateXsd(propertiesFileName);
				break;
			case setup:
				LightAirCli.setup(propertiesFileName, toApiFileNames(fileNames));
				break;
			case verify:
				LightAirCli.verify(propertiesFileName, toApiFileNames(fileNames));
				break;
			case await:
				LightAirCli.await(propertiesFileName, toApiFileNames(fileNames));
				break;
			default:
				throw new IllegalArgumentException("Unknown action <" + action + ">");
		}
		return 0;
	}

	private Map<String, List<String>> toApiFileNames(List<String> fileNames) {
		if (fileNames == null) {
			throw new CommandLine.ParameterException(spec.commandLine(), "No dataset file(s) specified.");
		}
		Map<String, List<String>> apiFileNames = new LinkedHashMap<>();
		fileNames.forEach(fileName -> {
			String[] parts = fileName.split(":");
			if (parts.length > 2) {
				throw new IllegalArgumentException("Invalid file name <" + fileName + "> - more than one colon.");
			}
			String profile = parts.length == 1 || parts[0].isEmpty() ? DEFAULT_PROFILE : parts[0];
			String file = parts.length == 1 ? fileName : parts[1];

			apiFileNames.computeIfAbsent(profile, newProfile -> new ArrayList<>());
			List<String> profileFileNames = apiFileNames.get(profile);
			profileFileNames.add(file);
		});
		return apiFileNames;
	}

	public static void main(String... args) {
		int exitCode = new CommandLine(new LightAirCliCommand()).execute(args);
		System.exit(exitCode);
	}
}
