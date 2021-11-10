package com.watayouxiang.apt_annotation;

/**
 * <pre>
 *     author : TaoWang
 *     e-mail : watayouxiang@qq.com
 *     time   : 2021/11/10
 *     desc   :
 * </pre>
 */
public interface IBinder<T> {
    void bind(T target);
}
