package org.xlp.bean.annotation;

import org.xlp.bean.base.IMethodProxy;

import java.lang.annotation.*;

/**
 * 标记实现<code>{@link org.xlp.bean.base.IBeanWrapper}</code>接口的类的方法是否需要执行该接口的增强方法
 * 更加细粒度的控制指定方法是否需要增强
 *
 * @author xlp
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface MethodProxy {
    /**
     * 标记方法是否需要增强，默认是
     */
    boolean proxy() default true;

    /**
     * 用来处理更复杂场景下的方法增强情况，当该值已配置时，{@link #proxy} 的设置不生效
     */
    Class<? extends IMethodProxy>[] methodProxy() default {};
}
