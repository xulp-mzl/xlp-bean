package org.xlp.bean.impl;

import org.xlp.bean.base.IBeanDefinition;
import org.xlp.bean.base.IBeanField;
import org.xlp.bean.exception.BeanBaseException;
import org.xlp.utils.XLPStringUtil;

import java.util.Map;

/**
 * bean定义查找工具
 */
class BeanDefinitionFinder {
    /**
     * 根据 IBeanField 对象查找其属性对应的IBeanDefinition对象
     * @param beanField
     * @param beanIdBeanDefinitionMap
     * @param beanClassNameBeanDefinitionMap
     * @return
     * @throws BeanBaseException 假如未找到，抛出该异常
     */
     static IBeanDefinition find(IBeanField beanField,
                                       Map<String, IBeanDefinition> beanIdBeanDefinitionMap,
                                       Map<String, IBeanDefinition> beanClassNameBeanDefinitionMap){
        IBeanDefinition beanDefinition;
        String beanId = beanField.getRefBeanId();
        String errorMsg;
        if (XLPStringUtil.isEmpty(beanId)){
            beanId = beanField.getName();
            beanDefinition = beanIdBeanDefinitionMap.get(beanId);
            String className = "";
            if (beanDefinition == null){
                className = beanField.getRefBeanClassName();
                className = XLPStringUtil.isEmpty(className) ? beanField.getFieldClassName() : className;
                beanDefinition = beanClassNameBeanDefinitionMap.get(className);
            }
            errorMsg = "id或beanClassName为【" + beanId + "/" + className + "】的bean定义为找到";
        } else {
            beanDefinition = beanIdBeanDefinitionMap.get(beanId);
            errorMsg = "id为【" + beanId + "】的bean定义为找到";
        }
        if (beanDefinition == null){
            throw new BeanBaseException(errorMsg);
        }
        return beanDefinition;
    }
}
