package org.xlp.bean.annotation;

import java.lang.annotation.*;

/**
 * 该注解用来标记哪些类放在容器里进行管理
 * @see java.lang.annotation.Retention
 * @see java.lang.annotation.Target
 * @see java.lang.annotation.Documented
 * @author 徐龙平
 *         <p>
 *         2023-3-21
 *         </p>
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Component {
    /**
     * 管理bean的id，该值必须唯一
     */
    String id() default "";

    /**
     * 管理bean的描述
     */
    String description() default "";

    /**
     * bean的实例是否是单例，默认为单例
     */
    boolean singleton() default true;

    /**
     * 创建bean实例，是否通过代理创建，默认否
     */
    boolean proxy() default false;

    /**
     * 是否延迟实例化对象，只对单例有效，默认非延迟加载
     */
    boolean lazy() default false;
}
