package org.rlk.annotation;

import java.lang.annotation.*;

/**
 * @author: rlk
 * @date: 2022/8/26
 * Description: @Component注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    //Bean的名称
    String value() default  "";
}
