package net.sf.lightair.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

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
 * By default, system locates a file named &lt;test class name>.&lt;test method
 * name>-verify.xml in the package of the class. If no such file is found,
 * system locates file named &lt;test class name>-verify.xml in the package of
 * the class.
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
 * format, which means there is a root &lt;dataset /> element, which contains
 * one element for each row, where the name of the row element specifies the
 * database table and its elements specify the table columns. So, for example:
 * 
 * <pre>
 * &lt;dataset>
 * 	&lt;person name="Joe" />
 * &lt;/dataset>
 * </pre>
 * <p>
 * System verifies all datasets located against the database.
 */
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
public @interface Verify {

	/**
	 * Names of files with datasets.
	 * 
	 * @return
	 */
	String[] value() default {};

}
