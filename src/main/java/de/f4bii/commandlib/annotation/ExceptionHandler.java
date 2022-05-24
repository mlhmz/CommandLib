package de.f4bii.commandlib.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ExceptionHandler {
    Class<? extends Exception>[] value();

    String message() default "";

    String method() default "";
}
