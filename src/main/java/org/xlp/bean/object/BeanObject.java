package org.xlp.bean.object;

import org.xlp.bean.base.IBeanDefinition;
import org.xlp.bean.base.IWrapper;
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
     * 原始对象
     */
    private Object object;

    /**
     * 对象对应的bean定义对象
     */
    private IBeanDefinition beanDefinition;

    public BeanObject(){}

    public BeanObject(Type[] types, Object object, IBeanDefinition beanDefinition) {
        this.types = types;
        this.object = object;
        this.beanDefinition = beanDefinition;
    }

    public BeanObject(Object object, IBeanDefinition beanDefinition) {
        this.object = object;
        this.beanDefinition = beanDefinition;
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

    public void setTypes(Type[] types) {
        this.types = types;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public IBeanDefinition getBeanDefinition() {
        return beanDefinition;
    }

    public void setBeanDefinition(IBeanDefinition beanDefinition) {
        this.beanDefinition = beanDefinition;
    }

    /**
     * 对比泛型类型是否一致
     * @param types
     * @return true: 一致，false：不一致
     */
    public boolean compareTypes(Type[] types){
        if (XLPArrayUtil.isEmpty(this.types) || XLPArrayUtil.isEmpty(types)){
            return true;
        }
        // 判断泛型信息长度是否相同，不相同则返回false
        if (types.length != this.types.length){
            return false;
        }

        int len = types.length;


        return false;
    }
}
