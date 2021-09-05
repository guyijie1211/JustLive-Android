package com.sunnyweather.android.ui.customerUIs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.customview.widget.ViewDragHelper;

/**
 * 垂直DrawerLayout
 * 实现步骤：
 * 1.使用静态方法来构ViewDragHelper,需要传入一个ViewDragHelper.Callback对象.
 * 2.重写onInterceptTouchEvent和onTouchEvent回调ViewDragHelper中对应方法.
 * 3.在ViewDragHelper.Callback中对视图做操作.
 * 4.使用ViewDragHelper.smoothSlideViewTo()方法平滑滚动.
 * 5.自定义一些交互逻辑的自由实现.
 */
public class VerticalDrawerLayout extends ViewGroup {
    private ViewDragHelper mTopViewDragHelper;

    private View mContentView;
    private View mDrawerView;

    private int mCurTop = 0;

    private boolean mIsOpen = true;

    public VerticalDrawerLayout(Context context) {
        super(context);
        init();
    }

    public VerticalDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VerticalDrawerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //Step1：使用静态方法构造ViewDragHelper,其中需要传入一个ViewDragHelper.Callback回调对象.
        mTopViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelperCallBack());
        mTopViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_TOP);
    }

    //Step2：定义一个ViewDragHelper.Callback回调实现类
    private class ViewDragHelperCallBack extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //返回ture则表示可以捕获该view,手指摸上一瞬间调运
            return child == mDrawerView;
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            //setEdgeTrackingEnabled设置的边界滑动时触发
            //captureChildView是为了让tryCaptureView返回false依旧生效
            mTopViewDragHelper.captureChildView(mDrawerView, pointerId);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //手指触摸移动时实时回调, left表示要到的x位置
            return super.clampViewPositionHorizontal(child, left, dx);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            //手指触摸移动时实时回调, top表示要到的y位置
            //保证手指挪动时只能向上，向下最大到0
            //top是动态的值 0 为下边界   -mDrawerView.getHeight()为上边界
            return Math.max(Math.min(top, 0), -mDrawerView.getHeight());
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            //手指释放时回调
            float movePrecent = (releasedChild.getHeight() + releasedChild.getTop()) / (float) releasedChild.getHeight();
            int finalTop = (xvel >= 0 && movePrecent > 0.5f) ? 0 : -releasedChild.getHeight();
            mTopViewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), finalTop);
            invalidate();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            //mDrawerView完全挪出屏幕则防止过度绘制
            mDrawerView.setVisibility((changedView.getHeight()+top == 0)? View.GONE : View.VISIBLE);
            mCurTop = top;
            requestLayout();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            if (mDrawerView == null) return 0;
            return (mDrawerView == child) ? mDrawerView.getHeight() : 0;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (state == ViewDragHelper.STATE_IDLE) {
                mIsOpen = (mDrawerView.getTop() == 0);
            }
        }
    }

    @Override
    public void computeScroll() {
        if (mTopViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    public void closeDrawer() {
        if (mIsOpen) {
            mTopViewDragHelper.smoothSlideViewTo(mDrawerView, mDrawerView.getLeft(), -mDrawerView.getHeight());
            invalidate();
        }
    }

    public void openDrawer() {
        if (!mIsOpen) {
            mTopViewDragHelper.smoothSlideViewTo(mDrawerView, mDrawerView.getLeft(), -1550);
            invalidate();
        }
    }

    public boolean isDrawerOpened() {
        return mIsOpen;
    }

    //Step3：重写onInterceptTouchEvent回调ViewDragHelper中对应的方法.
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mTopViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    //Step3：重写onTouchEvent回调ViewDragHelper中对应的方法.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mTopViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(measureWidth, measureHeight);

        mContentView = getChildAt(0);
        mDrawerView = getChildAt(1);

        MarginLayoutParams params = (MarginLayoutParams) mContentView.getLayoutParams();
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                measureWidth- (params.leftMargin + params.rightMargin), MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                measureHeight - (params.topMargin + params.bottomMargin), MeasureSpec.EXACTLY);
        mContentView.measure(childWidthMeasureSpec, childHeightMeasureSpec);

        mDrawerView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            MarginLayoutParams params = (MarginLayoutParams) mContentView.getLayoutParams();
            mContentView.layout(params.leftMargin, params.topMargin,
                    mContentView.getMeasuredWidth() + params.leftMargin,
                    mContentView.getMeasuredHeight() + params.topMargin);

            params = (MarginLayoutParams) mDrawerView.getLayoutParams();
            mDrawerView.layout(params.leftMargin, mCurTop + params.topMargin,
                    mDrawerView.getMeasuredWidth() + params.leftMargin,
                    mCurTop + mDrawerView.getMeasuredHeight() + params.topMargin);
        }
    }
}
