package net.sf.lightair.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ TYPE, METHOD })
@Retention(RUNTIME)
public @interface Setup {

}
