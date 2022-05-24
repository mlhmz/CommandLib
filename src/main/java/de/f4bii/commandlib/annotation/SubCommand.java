package de.f4bii.commandlib.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface SubCommand {
    String value() default "";

    String permission() default "";

    boolean needsOp() default false;
}
