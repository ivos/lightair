package net.sf.lightair.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Verifies database after test.
 * <p>
 * Annotate test method with @Verify to verify database after the test method
 * executed. Annotate the whole test class to have all its test methods verify
 * database. For example:
 * 
 * <pre>
 * &#064;Test &#064;Verify
 * public void myTestMethod {
 * }
 * </pre>
 * <p>
 * By default, system locates sequentially the following files in the package of
 * the class, the first file found wins:
 * <ol>
 * <li>&lt;test class name&gt;.&lt;test method name&gt;-verify.xml</li>
 * <li>&lt;test method name&gt;-verify.xml</li>
 * <li>&lt;test class name&gt;-verify.xml</li>
 * </ol>
 * <p>
 * Specify the names of files explicitly in the annotation to override the
 * default file name convention. Multiple files may be specified, system uses
 * all of them. The files are located in the package of the class. For example:
 * 
 * <pre>
 * &#064;Test
 * &#064;Verify({ "custom-verify-1.xml", "custom-verify-2.xml", "custom-verify-3.xml" })
 * public void myTestMethod {
 * }
 * </pre>
 * <p>
 * The verification files must conform to the DBunit's "flat XML dataset"
 * format, which means there is a root &lt;dataset /&gt; element, which contains
 * one element for each row, where the name of the row element specifies the
 * database table and its elements specify the table columns. So, for example:
 * 
 * <pre>
 * &lt;dataset&gt;
 * 	&lt;person name="Joe" /&gt;
 * &lt;/dataset&gt;
 * </pre>
 * <p>
 * System verifies all datasets located against the database.
 * <p>
 * Use @{@link Verify.List} to define multiple <code>@Verify</code> annotations
 * on the same element.
 *
 * <p>
 * See <a href="http://lightair.sourceforge.net/features/verify.html">http://lightair.sourceforge.net/features/verify.html</a>
 * for more info.
 * </p>
 *
 * @see Await
 * @see Verify.List
 */
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@Documented
public @interface Verify {

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
	 * Defines several @{@link Verify} annotations on the same element.
	 * 
	 * @see Verify
	 */
	@Target({ TYPE, METHOD })
	@Retention(RUNTIME)
	@Documented
	@interface List {
		Verify[] value();
	}
}
