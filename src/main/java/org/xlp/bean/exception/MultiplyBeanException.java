package org.xlp.bean.exception;

public class MultiplyBeanException extends BeanBaseException{
    private static final long serialVersionUID = 4575230961363489777L;

    public MultiplyBeanException(Class<?> beanClass) {
        this(beanClass == null ? "" : beanClass.getName());
    }

    public MultiplyBeanException(String beanClass){
        super("类名为【" + beanClass + "】bean实例在容器中找到多个。");
    }
}
