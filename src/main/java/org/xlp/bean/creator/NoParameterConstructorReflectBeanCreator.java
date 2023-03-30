package org.xlp.bean.creator;

import org.xlp.assertion.AssertUtils;
import org.xlp.bean.base.IBeanCreator;
import org.xlp.bean.exception.BeanBaseException;
import org.xlp.bean.util.ClassForNameUtils;

import java.lang.reflect.Modifier;

/**
 * 使用反射创建指定类型的bean对象创建器，只适合无参构造函数
 * 如果是抽象类或接口则会抛出{@link BeanBaseException}异常
 */
public class NoParameterConstructorReflectBeanCreator implements IBeanCreator {
    /**
     * bean 类型
     */
    private final Class<?> beanClass;

    /**
     * 构造器
     *
     * @param beanClass bean类型
     * @throws NullPointerException 假如参数为null，则抛出该异常
     * @throws BeanBaseException    假如代理失败，则抛出该异常
     */
    public <T> NoParameterConstructorReflectBeanCreator(Class<T> beanClass) {
        AssertUtils.isNotNull(beanClass, "beanClass parameter is null!");
        if (Modifier.isAbstract(beanClass.getModifiers())){
           throw new BeanBaseException("该类型【" + beanClass.getName() + "】是抽象类或接口，不能被反射创建实例！");
        }
        this.beanClass = beanClass;
    }

    /**
     * 构造函数
     *
     * @param className 类名称
     * @throws NullPointerException 假如参数为null或空 则抛出该异常
     * @throws BeanBaseException    假如获取beanClassName对应的Class对象出错，则抛出该异常
     */
    public NoParameterConstructorReflectBeanCreator(String className) {
        this(ClassForNameUtils.forName(className));
    }

    /**
     * 获取目标bean类型
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Class<T> getBeanClass() {
        return (Class<T>) beanClass;
    }

    /**
     * 创建bean对象
     *
     * @return
     * @throws BeanBaseException 假如创建bean实例失败，则抛出该异常
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T createBean() throws BeanBaseException{
        try {
            return (T) beanClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BeanBaseException(e);
        }
    }
}
