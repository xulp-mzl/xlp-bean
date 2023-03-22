package org.xlp.bean.exception;

public class NotSuchBeanException extends BeanBaseException{
    private static final long serialVersionUID = 7712536929455043834L;

    private String beanIdOrClassName;

    public NotSuchBeanException(String beanIdOrClassName) {
        super(beanIdOrClassName + "：：该id或类型bean的未找到");
        this.beanIdOrClassName = beanIdOrClassName;
    }

    public String getBeanIdOrClassName() {
        return beanIdOrClassName;
    }

    public void setBeanIdOrClassName(String beanIdOrClassName) {
        this.beanIdOrClassName = beanIdOrClassName;
    }
}
