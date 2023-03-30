package org.xlp.bean.exception;

public class BeanDefinitionExistException extends BeanBaseException{

    private static final long serialVersionUID = -2908416062086277666L;

    public BeanDefinitionExistException(String beanIdOrClassName) {
        super("[" + beanIdOrClassName + "] 对应的bean定义已存在，不能再次添加。");
    }
}
