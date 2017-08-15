package com.bidanet.log.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xuejike on 2017/8/3.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD,ElementType.ANNOTATION_TYPE })
public @interface BdLog {
    Class<? extends LogHandler>[] value() default {};
}
