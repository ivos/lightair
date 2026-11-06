package test.support;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base class for performing templating tests - tests where a generated file is
 * compared to a fixed template.
 * <p>
 * Regex replacement during comparing is supported to eliminate from comparison
 * minor parts that may dynamically change (like e.g. time stamps).
 * <p>
 * The test may also instead of comparing the generated file to the template
 * alternatively replace the whole contents of the template file by the current
 * generated file to allow automatic batch updates of all template files on
 * cross-cutting changes.
 * <p>
 * The default templates base directory is "src/test/template/".
 * <p>
 * The default base directory for generated files is "target/".
 */
public class TemplatingTestBase {

	private final boolean replaceTemplates;
	private final String templatesBaseDir;
	private final String generatedBaseDir;

	protected final static String DEFAULT_TEMPLATES_BASE_DIR = "src/test/template/";
	protected final static String DEFAULT_GENERATED_BASE_DIR = "target/";
	protected final static String DEFAULT_REPLACEMENT = "REPLACED";

	/**
	 * Use this as regex to ignore XML namespaces in comparison.
	 */
	protected final static String XML_NAMESPACES_REGEX = "(?s)(xmlns|xsi)[^>]*>";

	/**
	 * Default constructor. The test uses default directories for templates and
	 * for generated files. The test performs comparison.
	 */
	public TemplatingTestBase() {
		this(false);
	}

	/**
	 * TemplatesTestBase constructor.
	 *
	 * @param replaceTemplates Should the template file be replaced instead of compared?
	 */
	public TemplatingTestBase(boolean replaceTemplates) {
		this(replaceTemplates, DEFAULT_TEMPLATES_BASE_DIR, DEFAULT_GENERATED_BASE_DIR);
	}

	/**
	 * TemplatesTestBase constructor.
	 *
	 * @param replaceTemplates Should the template file be replaced instead of compared?
	 * @param templatesBaseDir Custom base directory for templates
	 * @param generatedBaseDir Custom base directory for generated files
	 */
	public TemplatingTestBase(boolean replaceTemplates, String templatesBaseDir, String generatedBaseDir) {
		this.replaceTemplates = replaceTemplates;
		this.templatesBaseDir = templatesBaseDir;
		this.generatedBaseDir = generatedBaseDir;
	}

	/**
	 * Perform the templating test. Compares the contents of the generated file
	 * to the contents of the template file.
	 * <p>
	 * Alternatively, if the <code>replaceTemplates</code> flag is set, replaces
	 * the contents of the template file with the contents of the generated
	 * file, when they differ.
	 *
	 * @param template  The template file name, relative to the templates base
	 *                  directory
	 * @param generated The generated file name, relative to the base directory of
	 *                  generated files
	 * @throws IOException on reading or writing test data files
	 */
	protected void performTest(String template, String generated) throws IOException {
		performTest(template, generated, null);
	}

	/**
	 * Perform the templating test. Compares the contents of the generated file
	 * to the contents of the template file, replacing parts that match the
	 * supplied regular expression with the default replacement ("REPLACED").
	 * <p>
	 * Alternatively, if the <code>replaceTemplates</code> flag is set, replaces
	 * the contents of the template file with the contents of the generated
	 * file, when they differ.
	 *
	 * @param template  The template file name, relative to the templates base
	 *                  directory
	 * @param generated The generated file name, relative to the base directory of
	 *                  generated files
	 * @param regex     Regular expression to match the parts to be replaced. When
	 *                  <code>null</code>, no regex matching is done.
	 * @throws IOException on reading or writing test data files
	 */
	protected void performTest(String template, String generated, String regex) throws IOException {
		performTest(template, generated, regex, DEFAULT_REPLACEMENT);
	}

	/**
	 * Perform the templating test. Compares the contents of the generated file
	 * to the contents of the template file, replacing parts that match the
	 * supplied regular expression with the replacement.
	 * <p>
	 * Alternatively, if the <code>replaceTemplates</code> flag is set, replaces
	 * the contents of the template file with the contents of the generated
	 * file, when they differ.
	 *
	 * @param template    The template file name, relative to the templates base
	 *                    directory
	 * @param generated   The generated file name, relative to the base directory of
	 *                    generated files
	 * @param regex       Regular expression to match the parts to be replaced. When
	 *                    <code>null</code>, no regex matching is done.
	 * @param replacement The text that is placed instead of the parts matching the
	 *                    regular expression
	 * @throws IOException on reading or writing test data files
	 */
	protected void performTest(String template, String generated, String regex, String replacement) throws IOException {
		File templateFile = new File(templatesBaseDir + template);
		File generatedFile = new File(generatedBaseDir + generated);
		assertTrue(templateFile.exists(), "Template exists: " + templateFile);
		assertTrue(generatedFile.exists(), "Generated exists: " + generatedFile);
		String templateFileContent = FileUtils.readFileToString(templateFile, Charset.defaultCharset());
		String generatedFileContent = FileUtils.readFileToString(generatedFile, Charset.defaultCharset());
		if (replaceTemplates) {
			if (!templateFileContent.equals(generatedFileContent)) {
				String msg = "WARNING: Replacing content of template file: "
					+ templateFile;
				System.err.println(msg);
				FileUtils.writeStringToFile(templateFile, generatedFileContent, Charset.defaultCharset());
			}
		} else {
			assertEquals(
				replace(templateFileContent, regex, replacement),
				replace(generatedFileContent, regex, replacement),
				"Content equals");
		}
	}

	private String replace(String text, String regex, String replacement) {
		if (null != regex)
			return text.replaceAll(regex, replacement);
		return text;
	}

	/**
	 * Perform the templating test. Sorts the lines of the generated file and
	 * compares the sorted lines to the contents of the template file.
	 * <p>
	 * Alternatively, if the <code>replaceTemplates</code> flag is set, replaces
	 * the contents of the template file with the contents of the generated
	 * file, when they differ.
	 *
	 * @param template  The template file name, relative to the templates base
	 *                  directory
	 * @param generated The generated file name, relative to the base directory of
	 *                  generated files
	 * @throws IOException on reading or writing test data files
	 */
	protected void performSortTest(String template, String generated)
		throws IOException {
		File templateFile = new File(templatesBaseDir + template);
		File generatedFile = new File(generatedBaseDir + generated);
		assertTrue(templateFile.exists(), "Template exists: " + templateFile);
		assertTrue(generatedFile.exists(), "Generated exists: " + generatedFile);
		String templateFileContent = readFileSortLines(templateFile);
		String generatedFileContent = readFileSortLines(generatedFile);
		if (replaceTemplates) {
			if (!templateFileContent.equals(generatedFileContent)) {
				String msg = "WARNING: Replacing content of template file: "
					+ templateFile;
				System.err.println(msg);
				FileUtils.writeStringToFile(templateFile, generatedFileContent, Charset.defaultCharset());
			}
		} else {
			assertEquals(templateFileContent, generatedFileContent, "Content equals");
		}
	}

	private String readFileSortLines(File f) throws IOException {
		List<String> lines = FileUtils.readLines(f, Charset.defaultCharset());
		Collections.sort(lines);
		return StringUtils.join(lines, "\n") + "\n";
	}
}
