package org.xlp.bean.exception;

/**
 * bean操作时的基本异常
 */
public class BeanBaseException extends RuntimeException{
    private static final long serialVersionUID = 2937311004764951532L;

    public BeanBaseException(String message) {
        super(message);
    }

    public BeanBaseException(Throwable cause) {
        super(cause);
    }

    public BeanBaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
