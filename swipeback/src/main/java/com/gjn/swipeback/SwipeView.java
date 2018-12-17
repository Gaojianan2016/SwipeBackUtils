package com.gjn.swipeback;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * @author gjn
 * @time 2018/12/17 17:21
 */

public class SwipeView extends FrameLayout {

    private Activity activity;
    private ViewDragHelper mViewDragHelper;

    private View mClild;
    private int mDistance;
    private int screenWidth;

    public SwipeView(@NonNull Context context) {
        this(context, null);
    }

    public SwipeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        mViewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                return false;
            }

            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                //左边界点击并且不是最底层activity
                if (ViewDragHelper.EDGE_LEFT == edgeFlags && !activity.isTaskRoot()) {
                    mViewDragHelper.captureChildView(mClild, pointerId);
                }
            }

            @Override
            public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
                //距离不能少于0
                mDistance = left < 0 ? 0 : left;
                return mDistance;
            }

            @Override
            public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
                if (mDistance <= screenWidth / 3) {
                    mViewDragHelper.settleCapturedViewAt(0, 0);
                } else {
                    mViewDragHelper.settleCapturedViewAt(screenWidth, 0);
                    if (activity != null && !activity.isFinishing()) {
                        activity.finish();
                        //去除关闭动画
                        activity.overridePendingTransition(0,0);
                    }
                }
                invalidate();
            }
        });
        //运行左边界拖动
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    public void bindActivity(Activity activity){
        this.activity = activity;
        //获取decorView
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        mClild = decorView.getChildAt(0);
        ViewGroup parent = (ViewGroup) mClild.getParent();
        if (parent != null) {
            parent.removeView(mClild);
        }
        //window背景会被设置为透明所以设置默认白色背景
        mClild.setBackgroundResource(android.R.color.white);
        addView(mClild);
        decorView.addView(this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }
}
