package org.xlp.bean.util;

import org.xlp.assertion.AssertUtils;
import org.xlp.bean.base.IBeanField;
import org.xlp.bean.exception.BeanBaseException;
import org.xlp.javabean.utils.MethodNameUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 执行方法的工具类
 */
public class MethodUtils {
    /**
     * 使用反射执行目标对象对应的IBeanField属性所对应的方法
     * @param target
     * @param beanField
     * @throws NullPointerException 假如参数为null，则抛出该异常
     * @throws BeanBaseException 假如执行失败则抛出该异常
     */
    public static void invoke(Object target, IBeanField beanField, Object... parameters){
        AssertUtils.isNotNull(target, "target parameter is null!");
        AssertUtils.isNotNull(beanField, "beanField parameter isnull！");
        String setMethodName = MethodNameUtil.createSetterMethodName(beanField.getName());
        try {
            Method method = beanField.getBeanClass().getMethod(setMethodName, beanField.getFieldClass());
            method.invoke(target, parameters);
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanBaseException(e);
        }
    }
}
