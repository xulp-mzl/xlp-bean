package org.xlp.bean.base;

import org.xlp.bean.exception.BeanBaseException;

/**
 * bean创建接口
 */
public interface IBeanCreator {
    /**
     * 获取目标bean类型
     * @param <T>
     * @return
     */
    <T> Class<T> getBeanClass();

    /**
     * 创建bean对象
     * @param <T>
     * @return
     * @throws BeanBaseException 假如创建bean实例失败，则抛出该异常
     */
    <T> T createBean() throws BeanBaseException;
}
