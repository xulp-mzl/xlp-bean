package org.xlp.bean.object;

import org.xlp.assertion.AssertUtils;
import org.xlp.bean.base.IBeanDefinition;
import org.xlp.bean.base.IWrapper;
import org.xlp.bean.util.ParameterizedTypeUtils;
import org.xlp.utils.XLPArrayUtil;

import java.lang.reflect.Type;

/**
 * bean包装对象
 */
public class BeanObject implements IWrapper {
    /**
     * 类的泛型信息
     */
    private Type[] types;

    /**
     * bean类型
     */
    private final Class<?> beanClass;

    /**
     * 原始对象
     */
    private final Object object;

    /**
     * Bean定义对象
     */
    private IBeanDefinition beanDefinition;

    /**
     * 标记是否递归查询泛型信息，默认递归查询，直到查到泛型信息后终止查询
     */
    private boolean deepFindTypes = true;

    /**
     * 构造函数
     * @param beanDefinition bean定义
     * @param bean 对应的bean对象
     * @throws NullPointerException 假如第一个参数为null，则抛出该异常
     */
    public BeanObject(IBeanDefinition beanDefinition, Object bean) {
        AssertUtils.isNotNull(beanDefinition, "beanDefinition parameter is null!");
        this.beanDefinition = beanDefinition;
        this.types = beanDefinition.getActualType();
        this.beanClass = beanDefinition.getBeanClass();
        this.object = bean;
    }

    /**
     * 构造函数
     * @param bean bean对象
     * @param types 相应的泛型信息
     */
    public BeanObject(Object bean, Type[] types) {
       this.types = types;
       this.object = bean;
       this.beanClass = bean.getClass();
       deepFindTypes = false;
    }

    /**
     * 获取初始对象
     *
     * @return
     */
    @Override
    public Object getRawObject() {
        return object;
    }

    public Type[] getTypes() {
        return types;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    /**
     * 获取Bean定义
     * @return
     */
    public IBeanDefinition getBeanDefinition() {
        return beanDefinition;
    }

    /**
     * 对比泛型类型是否一致
     * @param types
     * @return true: 一致，false：不一致
     */
    public boolean compareTypes(Type[] types){
        if (deepFindTypes && XLPArrayUtil.isEmpty(this.types)){
            this.types = ParameterizedTypeUtils.getClassTypes(this.beanClass);
        }

        if (XLPArrayUtil.isEmpty(this.types) || XLPArrayUtil.isEmpty(types)){
            return true;
        }

        return ParameterizedTypeUtils.equalsTypes(this.types, types);
    }

    /**
     * 比较类型和泛型是否相同
     * @param beanClass bean类型
     * @param types 泛型信息
     * @return true: 相同，false：不同
     */
    public boolean compareClassAndTypes(Class<?> beanClass, Type[] types){
        Class<?> _beanClass = getBeanClass();
        if (beanClass == null && _beanClass == null){
            return true;
        }
        if (beanClass == null || _beanClass == null){
            return false;
        }
        return beanClass.isAssignableFrom(_beanClass) &&  compareTypes(types);
    }
}
