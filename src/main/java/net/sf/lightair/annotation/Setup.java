package net.sf.lightair.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

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
 * By default, system locates a file named &lt;test class name>.&lt;test method
 * name>.xml in the package of the class. If no such file is found, system
 * locates file named &lt;test class name>.xml in the package of the class.
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
 * means there is a root &lt;dataset /> element, which contains one element for
 * each row, where the name of the row element specifies the database table and
 * its elements specify the table columns. So, for example:
 * 
 * <pre>
 * &lt;dataset>
 * 	&lt;person name="Joe" />
 * &lt;/dataset>
 * </pre>
 * <p>
 * System performs "clean insert" database operation with all datasets located,
 * which means all tables in the datasets are deleted in reverse order, then all
 * rows are inserted in the order specified.
 * <p>
 * Use {@link Setup.List} to define multiple <code>@Setup</code> annotations on
 * the same element.
 * 
 * @see Setup.List
 */
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@Documented
public @interface Setup {

	/**
	 * Names of files with datasets.
	 * 
	 * @return
	 */
	String[] value() default {};

	/**
	 * Name of the profile.
	 * <p>
	 * A profile lets you connect to a different database. A profile must be
	 * defined in LightAir properties file and must have its own properties file
	 * which defines the connection to the profile database.
	 * 
	 * @return
	 */
	String profile() default "";

	/**
	 * Defines several <code>@Setup</code> annotations on the same element.
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
