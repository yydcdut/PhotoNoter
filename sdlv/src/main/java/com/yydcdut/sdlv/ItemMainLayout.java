package com.yydcdut.sdlv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by yuyidong on 15/9/24.
 */
class ItemMainLayout extends FrameLayout {
    private static final int INTENTION_LEFT_OPEN = 1;
    private static final int INTENTION_LEFT_CLOSE = 2;
    private static final int INTENTION_LEFT_ALREADY_OPEN = 3;
    private static final int INTENTION_RIGHT_OPEN = -1;
    private static final int INTENTION_RIGHT_CLOSE = -2;
    private static final int INTENTION_RIGHT_ALREADY_OPEN = -3;
    private static final int INTENTION_ZERO = 0;
    private int mIntention = INTENTION_ZERO;

    private static final int SCROLL_STATE_OPEN = 1;
    private static final int SCROLL_STATE_CLOSE = 0;
    private int mScrollState = SCROLL_STATE_CLOSE;
    /* 时间 */
    private static final int SCROLL_TIME = 500;//500ms
    private static final int SCROLL_QUICK_TIME = 200;//200ms
    /* 控件高度 */
    private int mHeight;
    /* 子控件中button的总宽度 */
    private int mBtnLeftTotalWidth;
    private int mBtnRightTotalWidth;
    /* 子view */
    private ItemBackGroundLayout mItemLeftBackGroundLayout;
    private ItemBackGroundLayout mItemRightBackGroundLayout;
    private ItemCustomLayout mItemCustomLayout;
    /* Scroller */
    private Scroller mScroller;
    /* 控件是否滑动 */
    private boolean mIsMoving = false;
    /* 是不是要滑过(over) */
    private boolean mWannaOver = true;
    /* 坐标 */
    private float mXDown;
    private float mYDown;
    /* X方向滑动距离 */
    private float mXScrollDistance;
    /* 滑动的监听器 */
    private OnItemSlideListenerProxy mOnItemSlideListenerProxy;

    public ItemMainLayout(Context context) {
        this(context, null);
    }

    public ItemMainLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemMainLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
        mItemRightBackGroundLayout = new ItemBackGroundLayout(context);
        addView(mItemRightBackGroundLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mItemLeftBackGroundLayout = new ItemBackGroundLayout(context);
        addView(mItemLeftBackGroundLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mItemCustomLayout = new ItemCustomLayout(context);
        addView(mItemCustomLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    /**
     * 得到CustomView
     *
     * @return
     */
    public ItemCustomLayout getItemCustomLayout() {
        return mItemCustomLayout;
    }

    /**
     * 得到左边的背景View
     *
     * @return
     */
    public ItemBackGroundLayout getItemLeftBackGroundLayout() {
        return mItemLeftBackGroundLayout;
    }

    /**
     * 得到右边的背景View
     *
     * @return
     */
    public ItemBackGroundLayout getItemRightBackGroundLayout() {
        return mItemRightBackGroundLayout;
    }

    /**
     * @param height
     * @param btnLeftTotalWidth
     * @param btnRightTotalWidth
     * @param wannaOver
     */
    public void setParams(int height, int btnLeftTotalWidth, int btnRightTotalWidth, boolean wannaOver) {
        mHeight = height;
        mBtnLeftTotalWidth = btnLeftTotalWidth;
        mBtnRightTotalWidth = btnRightTotalWidth;
        mWannaOver = wannaOver;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mHeight > 0) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mHeight);
            for (int i = 0; i < getChildCount(); i++) {
                measureChild(getChildAt(i), widthMeasureSpec, MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY));
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(false);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXDown = ev.getX();
                mYDown = ev.getY();
                //控件初始距离
                mXScrollDistance = mItemCustomLayout.getScrollX();
                //是否有要scroll的动向，目前没有
                mIsMoving = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (fingerNotMove(ev) && !mIsMoving) {//手指的范围在50以内
                    //执行ListView的手势操作
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else if (fingerLeftAndRightMove(ev) || mIsMoving) {//上下范围在50，主要检测左右滑动
                    //是否有要scroll的动向
                    mIsMoving = true;
                    //执行控件的手势操作
                    getParent().requestDisallowInterceptTouchEvent(true);
                    float moveDistance = ev.getX() - mXDown;//这个往右是正，往左是负
                    //判断意图
                    if (moveDistance > 0) {//往右
                        if (mXScrollDistance == 0) {//关闭状态
                            mIntention = INTENTION_LEFT_OPEN;
                            setBackGroundVisible(true, false);
                        } else if (mXScrollDistance > 0) {//右边的btn显示出来的
                            mIntention = INTENTION_RIGHT_CLOSE;
                        } else if (mXScrollDistance < 0) {//左边的btn显示出来的
                            mIntention = INTENTION_LEFT_ALREADY_OPEN;
                        }
                    } else if (moveDistance < 0) {//往左
                        if (mXScrollDistance == 0) {//关闭状态
                            mIntention = INTENTION_RIGHT_OPEN;
                            setBackGroundVisible(false, true);
                        } else if (mXScrollDistance > 0) {//右边的btn显示出来的
                            mIntention = INTENTION_RIGHT_ALREADY_OPEN;
                        } else if (mXScrollDistance < 0) {//左边的btn显示出来的
                            mIntention = INTENTION_LEFT_CLOSE;
                        }
                    }
                    //计算出距离
                    switch (mIntention) {
                        case INTENTION_LEFT_CLOSE:
                        case INTENTION_LEFT_OPEN:
                        case INTENTION_LEFT_ALREADY_OPEN:
                            float distanceLeft = mXScrollDistance - moveDistance < 0 ? mXScrollDistance - moveDistance : 0;
                            if (!mWannaOver) {
                                distanceLeft = distanceLeft < -mBtnLeftTotalWidth ? -mBtnLeftTotalWidth : distanceLeft;
                            }
                            //滑动
                            mItemCustomLayout.scrollTo((int) distanceLeft, 0);
                            break;
                        case INTENTION_RIGHT_CLOSE:
                        case INTENTION_RIGHT_OPEN:
                        case INTENTION_RIGHT_ALREADY_OPEN:
                            float distanceRight = mXScrollDistance - moveDistance > 0 ? mXScrollDistance - moveDistance : 0;
                            if (!mWannaOver) {
                                distanceRight = distanceRight < mBtnRightTotalWidth ? distanceRight : mBtnRightTotalWidth;
                            }
                            mItemCustomLayout.scrollTo((int) distanceRight, 0);
                            break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //todo 有待优化这里的代码
                switch (mIntention) {
                    case INTENTION_LEFT_CLOSE:
                    case INTENTION_LEFT_OPEN:
                    case INTENTION_LEFT_ALREADY_OPEN:
                        //如果滑出的话，那么就滑到固定位置(只要滑出了 mBtnLeftTotalWidth / 2 ，就算滑出去了)
                        if (Math.abs(mItemCustomLayout.getScrollX()) > mBtnLeftTotalWidth / 2) {
                            //滑出
                            int delta = mBtnLeftTotalWidth - Math.abs(mItemCustomLayout.getScrollX());
                            if (Math.abs(mItemCustomLayout.getScrollX()) < mBtnLeftTotalWidth) {
                                mScroller.startScroll(mItemCustomLayout.getScrollX(), 0, -delta, 0, SCROLL_QUICK_TIME);
                            } else {
                                mScroller.startScroll(mItemCustomLayout.getScrollX(), 0, -delta, 0, SCROLL_TIME);
                            }
                            if (mOnItemSlideListenerProxy != null && mScrollState != SCROLL_STATE_OPEN) {
                                mOnItemSlideListenerProxy.onSlideOpen(this, MenuItem.DIRECTION_LEFT);
                            }
                            mScrollState = SCROLL_STATE_OPEN;
                        } else {
                            mScroller.startScroll(mItemCustomLayout.getScrollX(), 0, -mItemCustomLayout.getScrollX(), 0, SCROLL_TIME);
                            //滑回去,归位
                            if (mOnItemSlideListenerProxy != null && mScrollState != SCROLL_STATE_CLOSE) {
                                mOnItemSlideListenerProxy.onSlideClose(this, MenuItem.DIRECTION_LEFT);
                            }
                            mScrollState = SCROLL_STATE_CLOSE;
                        }
                        break;
                    case INTENTION_RIGHT_CLOSE:
                    case INTENTION_RIGHT_OPEN:
                    case INTENTION_RIGHT_ALREADY_OPEN:
                        if (Math.abs(mItemCustomLayout.getScrollX()) > mBtnRightTotalWidth / 2) {
                            //滑出
                            int delta = mBtnRightTotalWidth - Math.abs(mItemCustomLayout.getScrollX());
                            if (Math.abs(mItemCustomLayout.getScrollX()) < mBtnRightTotalWidth) {
                                mScroller.startScroll(mItemCustomLayout.getScrollX(), 0, delta, 0, SCROLL_QUICK_TIME);
                            } else {
                                mScroller.startScroll(mItemCustomLayout.getScrollX(), 0, delta, 0, SCROLL_TIME);
                            }
                            if (mOnItemSlideListenerProxy != null && mScrollState != SCROLL_STATE_OPEN) {
                                mOnItemSlideListenerProxy.onSlideOpen(this, MenuItem.DIRECTION_RIGHT);
                            }
                            mScrollState = SCROLL_STATE_OPEN;
                        } else {
                            mScroller.startScroll(mItemCustomLayout.getScrollX(), 0, -mItemCustomLayout.getScrollX(), 0, SCROLL_TIME);
                            //滑回去,归位
                            if (mOnItemSlideListenerProxy != null && mScrollState != SCROLL_STATE_CLOSE) {
                                mOnItemSlideListenerProxy.onSlideClose(this, MenuItem.DIRECTION_RIGHT);
                            }
                            mScrollState = SCROLL_STATE_CLOSE;
                        }
                        break;
                }
                mIntention = INTENTION_ZERO;
                postInvalidate();
                mIsMoving = false;
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 设置哪边显示哪边不显示
     *
     * @param leftVisible
     * @param rightVisible
     */
    private void setBackGroundVisible(boolean leftVisible, boolean rightVisible) {
        if (leftVisible) {
            if (mItemLeftBackGroundLayout.getVisibility() != VISIBLE) {
                mItemLeftBackGroundLayout.setVisibility(VISIBLE);
            }
        } else {
            if (mItemLeftBackGroundLayout.getVisibility() == VISIBLE) {
                mItemLeftBackGroundLayout.setVisibility(GONE);
            }
        }
        if (rightVisible) {
            if (mItemRightBackGroundLayout.getVisibility() != VISIBLE) {
                mItemRightBackGroundLayout.setVisibility(VISIBLE);
            }
        } else {
            if (mItemRightBackGroundLayout.getVisibility() == VISIBLE) {
                mItemRightBackGroundLayout.setVisibility(GONE);
            }
        }
    }

    /**
     * 上下左右不能超出50
     *
     * @param ev
     * @return
     */
    private boolean fingerNotMove(MotionEvent ev) {
        return (mXDown - ev.getX() < 25 && mXDown - ev.getX() > -25 &&
                mYDown - ev.getY() < 25 && mYDown - ev.getY() > -25);
    }

    /**
     * 左右得超出50，上下不能超出50
     *
     * @param ev
     * @return
     */
    private boolean fingerLeftAndRightMove(MotionEvent ev) {
        return ((ev.getX() - mXDown > 25 || ev.getX() - mXDown < -25) &&
                ev.getY() - mYDown < 25 && ev.getY() - mYDown > -25);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mItemCustomLayout.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * 归位
     */
    protected void scrollBack() {
        mScroller.startScroll(mItemCustomLayout.getScrollX(), 0, -mItemCustomLayout.getScrollX(), 0, SCROLL_TIME);
        postInvalidate();
    }

    /**
     * 设置item滑动的监听器
     *
     * @param onItemSlideListenerProxy
     */
    protected void setOnItemSlideListenerProxy(OnItemSlideListenerProxy onItemSlideListenerProxy) {
        mOnItemSlideListenerProxy = onItemSlideListenerProxy;
    }

    protected interface OnItemSlideListenerProxy {
        void onSlideOpen(View view, int direction);

        void onSlideClose(View view, int direction);
    }
}
