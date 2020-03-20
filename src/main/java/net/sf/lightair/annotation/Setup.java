package net.sf.lightair.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Sets up database before test.
 * <p>
 * Annotate test method with @Setup to setup database before the test method
 * executes. Annotate the whole test class to have all its test methods set up
 * database. For example:
 * 
 * <pre>
 * &#064;Test &#064;Setup
 * public void myTestMethod {
 * }
 * </pre>
 * <p>
 * By default, system locates sequentially the following files in the package of
 * the class, the first file found wins:
 * <ol>
 * <li>&lt;test class name&gt;.&lt;test method name&gt;.xml</li>
 * <li>&lt;test method name&gt;.xml</li>
 * <li>&lt;test class name&gt;.xml</li>
 * </ol>
 * <p>
 * Specify the names of files explicitly in the annotation to override the
 * default file name convention. Multiple files may be specified, system uses
 * all of them. The files are located in the package of the class. For example:
 * 
 * <pre>
 * &#064;Test
 * &#064;Setup({ &quot;custom-setup-1.xml&quot;, &quot;custom-setup-2.xml&quot;, &quot;custom-setup-3.xml&quot; })
 * public void myTestMethod {
 * }
 * </pre>
 * <p>
 * The setup files must conform to the DBunit's "flat XML dataset" format, which
 * means there is a root &lt;dataset /&gt; element, which contains one element for
 * each row, where the name of the row element specifies the database table and
 * its elements specify the table columns. So, for example:
 * 
 * <pre>
 * &lt;dataset&gt;
 * 	&lt;person name="Joe" /&gt;
 * &lt;/dataset&gt;
 * </pre>
 * <p>
 * System performs "clean insert" database operation with all datasets located,
 * which means all tables in the datasets are deleted in reverse order, then all
 * rows are inserted in the order specified.
 * <p>
 * Use @{@link Setup.List} to define multiple <code>@Setup</code> annotations on
 * the same element.
 *
 * <p>
 * See <a href="http://lightair.sourceforge.net/features/setup.html">http://lightair.sourceforge.net/features/setup.html</a>
 * for more info.
 * </p>
 *
 * @see Setup.List
 */
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@Documented
public @interface Setup {

	/**
	 * Names of files with datasets.
	 * @return datasets
	 */
	String[] value() default {};

	/**
	 * Name of the profile.
	 * <p>
	 * A profile lets you connect to a different database. A profile must be
	 * defined in LightAir properties file and must have its own properties file
	 * which defines the connection to the profile database.
	 * @return profile
	 */
	String profile() default "";

	/**
	 * Defines several @{@link Setup} annotations on the same element.
	 * 
	 * @see Setup
	 */
	@Target({ TYPE, METHOD })
	@Retention(RUNTIME)
	@Documented
	@interface List {
		Setup[] value();
	}
}
