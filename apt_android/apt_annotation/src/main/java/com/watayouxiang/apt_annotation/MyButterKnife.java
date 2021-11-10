package com.watayouxiang.apt_annotation;

/**
 * <pre>
 *     author : TaoWang
 *     e-mail : watayouxiang@qq.com
 *     time   : 2021/11/10
 *     desc   :
 * </pre>
 */
public class MyButterKnife {
    public static void bind(Object activity) {
        String name = activity.getClass().getName() + "_ViewBinding";
        try {
            Class<?> aClass = Class.forName(name);
            IBinder iBinder = (IBinder) aClass.newInstance();
            iBinder.bind(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


//package com.watayouxiang.aptdemo;
//import com.watayouxiang.apt_annotation.IBinder;
//
//public class MainActivity_ViewBinding implements IBinder<MainActivity> {
//    @Override
//    public void bind(com.watayouxiang.aptdemo.MainActivity target) {
//        target.textView = (android.widget.TextView) target.findViewById(2131231118);
//    }
//}