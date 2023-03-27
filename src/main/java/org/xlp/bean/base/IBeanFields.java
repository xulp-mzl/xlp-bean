package org.xlp.bean.base;

/**
 * 获取一个类中特殊注解标记的字段对应的IBeanField对象接口
 * @see IBeanField
 * @see org.xlp.bean.annotation.Component
 */
public interface IBeanFields{
    /**
     * 获取IBeanField对象
     * @return
     */
    IBeanField[] getBeanFields();

    /**
     * 获取bean类型
     * @return
     */
    Class<?> getBeanClass();
}
