package de.f4bii.commandlib.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Command {
    String value();

    String permissionPrefix() default "";
}
