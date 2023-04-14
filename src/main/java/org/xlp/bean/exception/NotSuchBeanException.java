package org.xlp.bean.exception;

public class NotSuchBeanException extends BeanBaseException{
    private static final long serialVersionUID = 7712536929455043834L;

    public NotSuchBeanException(String msg) {
        super(msg);
    }
}
