package com.leaps.scaffold.net;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Url注解，添加到Api的Service上
 * 对应的是动态域名的Key值
 * 如：
 * @ApiUrl("App")
 * interface UserService {
 *
 * }
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiUrl {
    String value() default "";
}
