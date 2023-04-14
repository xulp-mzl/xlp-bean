package org.xlp;

import org.xlp.bean.annotation.MethodProxy;
import org.xlp.bean.base.IBeanWrapper;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class A<T, C , z extends T> implements IBeanWrapper {
    private int age;

    private List<String> list;

    private Map<String, Object> map;

    List<T> list1;

    List<? super String> list2;

    List<? extends T> list3;

    List<List<String>> list4;

    List<? extends String> list5;

    B b;

    private void fun1(){
        System.out.println("fun1");
    }

    public final void fun2(){
        System.out.println("fun2");
    }

    public static void fun3(){
        System.out.println("fun3");
    }

    public void fun5(){
        System.out.println("fun5");
    }

    public void fun4(){
        System.out.println("fun4");
    }
    @MethodProxy(proxy = false)
    public void fun6(){}

    /**
     * 代理对象函数执行前需要执行的操作
     *
     * @param method 被代理对象的方法对象
     * @param params 被代理对象方法的参数
     */
    @Override
    public void beforeExecute(Method method, Object[] params) {
        System.out.println(BEFORE_EXECUTE_METHOD_NAME + "--" + method.getName());
    }

    /**
     * 代理对象函数执行后需要执行的操作
     *
     * @param method 被代理对象的方法对象
     * @param params 被代理对象方法的参数
     */
    @Override
    public void afterExecute(Method method, Object[] params) {
        System.out.println(AFTER_EXECUTE_METHOD_NAME + "--" + method.getName());
    }

    /**
     * 代理对象函数执行抛出异常是需要执行的操作
     *
     * @param method    被代理对象的方法对象
     * @param params    被代理对象方法的参数
     * @param throwable 被代理对象方法抛出的异常
     */
    @Override
    public void throwExecute(Method method, Object[] params, Throwable throwable) {
        System.out.println(THROW_EXECUTE_METHOD_NAME + "--" + method.getName());
    }
}
