package com.watayouxiang.aptdemo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 复习注解
 * <p>
 * 注解，单独是没有意义的。如下是具体应用场景：
 * <p>
 * 注解+APT：			用于生成一些java文件，如：butterknife, dagger2, hilt, databinding
 * 注解+代码埋点：			AspactJ, ARouter
 * 注解+反射+动态代理：	XUtils, Lifecycle
 */
@Target({ElementType.FIELD, ElementType.METHOD})// 作用在什么地方
@Retention(RetentionPolicy.RUNTIME)// 作用范围
public @interface TestAnnotation {
    String value();

    int id();
}

//
//public enum ElementType {
//    TYPE,               /* 类、接口（包括注释类型）或枚举声明 */
//    FIELD,              /* 字段声明（包括枚举常量）*/
//    METHOD,             /* 方法声明 */
//    PARAMETER,          /* 参数声明 */
//    CONSTRUCTOR,        /* 构造方法声明 */
//    LOCAL_VARIABLE,     /* 局部变量声明 */
//    ANNOTATION_TYPE,    /* 注释类型声明 */
//    PACKAGE             /* 包声明 */
//}
//
//public enum RetentionPolicy {
//    SOURCE,            /* Annotation信息仅存在于编译器处理期间，编译器处理完之后就没有该Annotation信息了 */
//    CLASS,             /* 编译器将Annotation存储于类对应的.class文件中。默认行为 */
//    RUNTIME            /* 编译器将Annotation存储于class文件中，并且可由JVM读入 */
//}
//