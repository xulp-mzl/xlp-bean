package org.xlp.bean.creator;

import org.xlp.assertion.AssertUtils;
import org.xlp.bean.base.IBeanCreator;
import org.xlp.bean.base.IBeanDefinition;

/**
 * 获取bean生成器工厂
 */
public class BeanCreatorFactory {
    /**
     * 根据bean定义获取相应的bean创建器
     * @param definition bean定义
     * @return
     * @throws NullPointerException 假如参数为null，则抛出该异常
     * @throws org.xlp.bean.exception.BeanBaseException 假如获取bean创建器失败，则抛出该异常
     */
    public static IBeanCreator getBeanCreator(IBeanDefinition definition){
        AssertUtils.isNotNull(definition, "definition parameter is null!");
        Class<?> beanClass = definition.getBeanClass();
        // 判断类是否需要被代理
        // 假如目标类型是IBeanWrapper的子类也需要被代理
        if (definition.isProxy()){
            return new ClassProxyBeanCreator(beanClass);
        } else {
            return new NoParameterConstructorReflectBeanCreator(beanClass);
        }
    }
}
