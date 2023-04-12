package org.xlp.bean.base;

import org.xlp.assertion.AssertUtils;
import org.xlp.bean.creator.BeanCreatorFactory;
import org.xlp.bean.exception.BeanBaseException;
import org.xlp.bean.impl.AutoFillBeanFields;
import org.xlp.bean.util.ClassForNameUtils;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 * bean定义抽象类
 * Create by xlp on 2023/3/25
 */
public abstract class AbstractBeanDefinition implements IBeanDefinition, IBeanFields{
    /**
     * bean的类型
     */
    protected Class<?> beanClass;

    /**
     * bean的类型全路径名
     */
    private final String beanClassName;

    /**
     * 用来获取IBeanField对象信息
     */
    private IBeanFields beanFields;

    /**
     * bean创建器，用来创建该bean定义相应的bean实例
     */
    private IBeanCreator beanCreator;

    /**
     * 构造函数
     * @param beanFields 获取IBeanField数组信息的对象
     * @throws NullPointerException 假如参数为null，则抛出该异常
     */
    public AbstractBeanDefinition(IBeanFields beanFields){
        AssertUtils.isNotNull(beanFields, "beanFields parameter is null!");
        this.beanFields = beanFields;
        this.beanClass = beanFields.getBeanClass();
        this.beanClassName = beanClass.getName();
    }

    /**
     * 构造函数
     * @param beanClass bean类型
     * @throws NullPointerException 假如参数为null 则抛出该异常
     */
    public AbstractBeanDefinition(Class<?> beanClass){
        AssertUtils.isNotNull(beanClass, "beanClass parameter is null!");
        this.beanClass = beanClass;
        this.beanClassName = beanClass.getName();
    }

    /**
     * 构造函数
     * @param beanClassName bean类型全路径名称
     * @throws NullPointerException 假如参数为null或空 则抛出该异常
     * @throws BeanBaseException 假如获取beanClassName对应的Class对象出错，则抛出该异常
     */
    public AbstractBeanDefinition(String beanClassName){
        this.beanClass = ClassForNameUtils.forName(beanClassName);
        this.beanClassName = beanClassName;
    }

    /**
     * 是否是接口
     *
     * @return true：是， false：否
     */
    @Override
    public boolean isInterface() {
        return beanClass.isInterface();
    }

    /**
     * 是否是抽象类或接口
     *
     * @return true：是， false：否
     */
    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(beanClass.getModifiers());
    }

    /**
     * 获取父类信息
     *
     * @return 超类信息
     */
    @Override
    public Class<?> getSupperClass() {
        return beanClass.getSuperclass();
    }

    /**
     * 获取bean class
     *
     * @return bean class对象
     */
    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    /**
     * 获取IBeanField对象
     *
     * @return
     */
    @Override
    public IBeanField[] getBeanFields() {
        beanFields = beanFields == null ? new AutoFillBeanFields(beanClass) : beanFields;
        return beanFields.getBeanFields();
    }

    /**
     * 获取类泛型信息，假如无泛型类型，则返回空数组
     *
     * @return
     */
    @Override
    public Type[] getActualType() {
        return beanClass.getTypeParameters();
    }

    /**
     * 获取bean类型全路径名称
     *
     * @return bean类型全路径名称
     */
    @Override
    public String getBeanClassName() {
        return beanClassName;
    }

    /**
     * 获取bean创建器
     *
     * @return
     * @throws BeanBaseException 假如获取bean创建器失败，则抛出该异常
     */
    @Override
    public IBeanCreator getBeanCreator() {
        // 假如bean创建器为空，则根据bean定义获取默认的创建器
        if (beanCreator == null){
            beanCreator = BeanCreatorFactory.getBeanCreator(this);
        }
        return beanCreator;
    }

    /**
     * 设置bean创建器
     *
     * @param beanCreator
     */
    @Override
    public void setBeanCreator(IBeanCreator beanCreator) {
        this.beanCreator = beanCreator;
    }
}
