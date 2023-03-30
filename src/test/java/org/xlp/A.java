package org.xlp;

import java.util.List;
import java.util.Map;

public abstract class A<T> {
    private int age;

    private List<String> list;

    private Map<String, Object> map;

    List<T> list1;

    List<List<T>> list2;

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
        fun1();

        System.out.println("fun4");

        fun5();
    }
    
    public abstract void fun6();
}
