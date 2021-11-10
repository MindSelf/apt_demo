package com.watayouxiang.aptdemo.annotation;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 *     author : TaoWang
 *     e-mail : watayouxiang@qq.com
 *     time   : 2021/11/10
 *     desc   : 注解代替枚举
 * </pre>
 */
public class Test {
    public static final int SUNDAY = 0;
    public static final int MONDAY = 1;

    // 注解代替枚举，可以节省内存空间
    @IntDef({SUNDAY, MONDAY})
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    public @interface WeekDay {
    }

    public static void main(String[] args) {
        setCurrDay(SUNDAY);
    }

    private static void setCurrDay(@WeekDay int sunday) {

    }
}

