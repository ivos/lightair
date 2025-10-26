package net.sf.lightair.internal.cli;

import picocli.CommandLine;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class ManifestVersionProvider implements CommandLine.IVersionProvider {

	@Override
	public String[] getVersion() throws Exception {
		String classPath = ManifestVersionProvider.class.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.getPath();
		Manifest manifest = new Manifest(
				new java.net.URL("jar:file:" + classPath + "!/META-INF/MANIFEST.MF").openStream()
		);
		Attributes attributes = manifest.getMainAttributes();
		String version = attributes.getValue("Implementation-Version");
		String name = attributes.getValue("Implementation-Title");
		if (version == null) {
			return new String[]{
					"Development Version (MANIFEST not found or missing attributes)"
			};
		}
		return new String[]{
				(name != null ? name + " " : "") + "Version: " + version,
				"Built with Picocli"
		};
	}
}
