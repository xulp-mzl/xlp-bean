package org.xlp.bean.creator;

import org.xlp.assertion.AssertUtils;
import org.xlp.bean.base.IBeanCreator;
import org.xlp.bean.exception.BeanBaseException;
import org.xlp.bean.proxy.XLPProxy;
import org.xlp.bean.util.ClassForNameUtils;

/**
 * 使用cglib代理生成指定类型的bean创建器
 */
public class ClassProxyBeanCreator implements IBeanCreator {
    /**
     * cglib代理bean操作类
     */
    private final XLPProxy proxy;

    /**
     * 构造器
     * @param beanClass bean类型
     * @throws NullPointerException 假如参数为null，则抛出该异常
     * @throws BeanBaseException 假如代理失败，则抛出该异常
     */
    public <T> ClassProxyBeanCreator(Class<T> beanClass){
        AssertUtils.isNotNull(beanClass, "beanClass parameter is null!");
        if (beanClass.isInterface()){
            throw new BeanBaseException("该类型【" + beanClass.getName() + "】是接口，不支持代理！");
        }
        proxy = new XLPProxy(beanClass);
    }

    /**
     * 构造函数
     * @param className 类名称
     * @throws NullPointerException 假如参数为null或空 则抛出该异常
     * @throws BeanBaseException 假如获取beanClassName对应的Class对象出错，则抛出该异常
     */
    public ClassProxyBeanCreator(String className){
        this(ClassForNameUtils.forName(className));
    }

    /**
     * 获取目标bean类型
     *
     * @return
     * @throws BeanBaseException 假如创建bean实例失败，则抛出该异常
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Class<T> getBeanClass() {
        return (Class<T>) proxy.getTargetClass();
    }

    /**
     * 创建bean对象
     *
     * @return
     * @throws BeanBaseException 假如创建bean实例失败，则抛出该异常
     */
    @Override
    public <T> T createBean() throws BeanBaseException{
        return proxy.createProxy();
    }
}
