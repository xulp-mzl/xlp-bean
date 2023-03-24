package org.xlp.bean.exception;

public class BeanExistException extends BeanBaseException{

    private static final long serialVersionUID = -2908416062086277665L;

    /**
     * bean类全路径名称
     */
    private final String beanClassName;

    public BeanExistException(Object bean) {
        super(bean == null ? "参数为null" : bean.getClass().getName() + " 对应的bean已存在，不能再次添加。");
        assert bean != null;
        beanClassName = bean.getClass().getName();
    }

    public BeanExistException(String id, Object bean) {
        super("id为【" + id + "】对应的bean已存在，不能再次添加。");
        assert bean != null;
        beanClassName = bean.getClass().getName();
    }

    public BeanExistException(Class<?> beanClass){
        super("【" + beanClass.getName() + "】对应的bean定义已存在，不能再次添加。");
        beanClassName = beanClass.getName();
    }

    public String getBeanClassName() {
        return beanClassName;
    }
}
