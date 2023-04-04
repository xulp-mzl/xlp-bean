package org.xlp.bean.annotation;
import java.lang.annotation.*;

/**
 * 表被管理bean的属性是否需要自动设置
 * tips：该注解需要与<code>{@link Component}</code>配合使用，该标记的字段需要有get方法，字段属性类型不能是常规类型
 * 查找优先级是 refId > refClassName
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface AutoFill {
    /**
     * 引用bean id
     */
    String refId() default "";

    /**
     * 引用bean类全路径名称
     */
    String refClassName() default "";

    /**
     * 标记是否必须赋值
     */
    boolean required() default true;
}
