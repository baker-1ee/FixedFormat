package com.example.fixedformat.aop;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FixedFormatColumn {

    int size() default -1;

    boolean nullable() default true;

    String name() default "";
}
