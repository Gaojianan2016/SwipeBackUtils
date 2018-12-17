package com.gjn.swipeback;

import android.app.Activity;
import android.app.ActivityOptions;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author gjn
 * @time 2018/12/17 17:15
 */

public class SwipeHelper {

    private Activity activity;
    private SwipeView swipeView;
    private Class translucentConversionListenerClass;
    private Object translucentConversionListener;
    private boolean isTranslucent;

    public SwipeHelper(Activity activity) {
        this.activity = activity;
        this.swipeView = new SwipeView(activity);
        swipeView.bindActivity(activity);
    }

    public SwipeHelper(Activity activity, SwipeView swipeView) {
        this.activity = activity;
        this.swipeView = swipeView;
    }

    public void translucentWindowsBackground(){
        isTranslucent = false;
        try {
            //获取透明转换回调类
            if (translucentConversionListenerClass == null) {
                Class[] classArray = Activity.class.getDeclaredClasses();
                for (Class clz : classArray) {
                    if (clz.getSimpleName().contains("TranslucentConversionListener")) {
                        translucentConversionListenerClass = clz;
                    }
                }
            }
            //代理透明转换回调
            if (translucentConversionListener == null && translucentConversionListenerClass != null) {
                translucentConversionListener = Proxy.newProxyInstance(translucentConversionListenerClass.getClassLoader(),
                        new Class[]{translucentConversionListenerClass},
                        new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                isTranslucent = true;
                                return null;
                            }
                        });
            }
            //利用反射将窗口转为透明
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Object options = null;
                try {
                    Method getActivityOptions = Activity.class.getDeclaredMethod("getActivityOptions");
                    getActivityOptions.setAccessible(true);
                    options = getActivityOptions.invoke(this);
                } catch (Exception e) {}
                Method convertToTranslucent = Activity.class.getDeclaredMethod("convertToTranslucent",
                        translucentConversionListenerClass, ActivityOptions.class);
                convertToTranslucent.setAccessible(true);
                convertToTranslucent.invoke(activity, translucentConversionListener, options);
            }else {
                Method convertToTranslucent = Activity.class.getDeclaredMethod("convertToTranslucent",
                        translucentConversionListenerClass);
                convertToTranslucent.setAccessible(true);
                convertToTranslucent.invoke(activity, translucentConversionListener);
            }
        }catch (Exception e){
            isTranslucent = true;
        }
        if (translucentConversionListenerClass == null) {
            isTranslucent = true;
        }
        //去除窗口背景
        activity.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void recoveryWindowsBackground(){
        try{
            Method convertFromTranslucent = Activity.class.getDeclaredMethod("convertFromTranslucent");
            convertFromTranslucent.setAccessible(true);
            convertFromTranslucent.invoke(activity);
            isTranslucent = false;
        }catch (Exception e){}
    }

    public boolean isTranslucent() {
        return isTranslucent;
    }
}
