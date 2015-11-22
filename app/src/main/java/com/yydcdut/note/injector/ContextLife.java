package com.yydcdut.note.injector;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;


import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by yuyidong on 15/11/22.
 */
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface ContextLife {
    String value() default "Application";
}
