package com.yydcdut.note.view;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;

import com.yydcdut.note.R;

import java.util.List;

/**
 * Created by yuyidong on 15/8/1.
 */
public class SlideAndDragListView<T> extends ListView implements Handler.Callback, View.OnDragListener, OnClickListener {
    private static final int MSG_WHAT_LONG_CLICK = 1;
    /* 时间 */
    private static final long DELAY_TIME = 1000;//1s
    private static final int SCROLL_TIME = 500;//500ms
    private static final int SCROLL_QUICK_TIME = 200;//200ms
    /* onTouch里面的状态 */
    private static final int STATE_NOTHING = -1;//抬起状态
    private static final int STATE_DOWN = 0;//按下状态
    private static final int STATE_LONG_CLICK = 1;//长点击状态
    private static final int STATE_SCROLL = 2;//SCROLL状态
    private static final int STATE_LONG_CLICK_FINISH = 3;//长点击已经触发完成
    private int mState = STATE_NOTHING;

    /* Scroller 滑动的 */
    private Scroller mScroller;
    /* 振动 */
    private Vibrator mVibrator;
    /* handler */
    private Handler mHandler;
    /* 滑动的目标对象 */
    private View mSwipeTargetView;
    /* 要滑动的目标对象位置 */
    private int mSwipeTargetPosition;
    private int mBackPostion;
    /* 手指放下的坐标 */
    private int mXDown;
    private int mYDown;
    /* X方向滑动了多少 */
    private int mXScrollDistance;
    /* 监听器 */
    private OnItemLongClickListener mLongClickListener;
    /* 那两个button的长度 */
    private int mBGWidth = (int) getContext().getResources().getDimension(R.dimen.slv_item_bg_width) * 2;//因为有2个

    /* 判断drag往上还是往下 */
    private boolean mUp = false;
    /* 当前drag所在listview中的位置 */
    private int mCurrentPosition;
    /* 之前drag所在listview中的位置 */
    private int mBeforeCurrentPosition;
    /* 之前之前drag所在listview中的位置 */
    private int mBeforeBeforePosition;
    /* 适配器 */
    private BaseAdapter mBaseAdapter;
    /* 监听器 */
    private OnDragListener mOnDragListener;
    /* 数据 */
    private List<T> mDataList;

    private boolean mIsScrolling = false;


    public SlideAndDragListView(Context context) {
        this(context, null);
    }

    public SlideAndDragListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideAndDragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(getContext());
        mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        mHandler = new Handler(this);
        setOnDragListener(this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_WHAT_LONG_CLICK:
                if (mState == STATE_LONG_CLICK) {
                    mState = STATE_LONG_CLICK_FINISH;
                    int position = msg.arg1;
                    View view = getChildAt(mSwipeTargetPosition - getFirstVisiblePosition());
                    if (mLongClickListener != null) {
                        scrollBack();
                        mVibrator.vibrate(100);
                        mLongClickListener.onItemLongClick(view, position);
                    }
                    mCurrentPosition = position;
                    mBeforeCurrentPosition = position;
                    mBeforeBeforePosition = position;
                    view.findViewById(R.id.layout_item_edit_category_txt).setVisibility(INVISIBLE);
                    view.findViewById(R.id.img_item_edit_category_bg).setVisibility(INVISIBLE);
                    ClipData.Item item = new ClipData.Item("1");
                    ClipData data = new ClipData("1", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                    view.startDrag(data, new View.DragShadowBuilder(view), null, 0);
                    mBaseAdapter.notifyDataSetChanged();
                }
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mSwipeTargetView != null) {
                mIsScrolling = true;
                mSwipeTargetView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
                postInvalidate();
                if (mScroller.isFinished()) {
                    mSwipeTargetView = null;
                }
            } else {
                mIsScrolling = false;
            }
        }
        super.computeScroll();
    }

    private OnScrollListener mOnScrollListener;

    public void setOnScrollListener(OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    public interface OnScrollListener {
        void onScrollOpen(View view, int position);

        void onScrollClose(View view, int position);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mIsScrolling) {//scroll正在滑动的话就不要做其他处理了
                    return super.onTouchEvent(ev);
                }
                //获取出坐标来
                mXDown = (int) ev.getX();
                mYDown = (int) ev.getY();

                //通过坐标找到在ListView中的位置
                mBackPostion = mSwipeTargetPosition;
                mSwipeTargetPosition = pointToPosition(mXDown, mYDown);
                if (mSwipeTargetPosition == AdapterView.INVALID_POSITION) {
                    return super.onTouchEvent(ev);
                }

                //通过位置找到要swipe的view
                View view = getChildAt(mSwipeTargetPosition - getFirstVisiblePosition());
                if (view == null) {
                    return super.onTouchEvent(ev);
                }
                mSwipeTargetView = view.findViewById(R.id.layout_item_edit_category);
                if (mSwipeTargetView != null) {
                    mXScrollDistance = mSwipeTargetView.getScrollX();
                } else {
                    mXScrollDistance = 0;
                }

                mState = STATE_DOWN;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsScrolling) {//scroll正在滑动的话就不要做其他处理了
                    return super.onTouchEvent(ev);
                }
                if (fingerNotMove(ev)) {//手指的范围在50以内
                    if (mState != STATE_SCROLL && mState != STATE_LONG_CLICK_FINISH) {//状态不为滑动状态且已经触发完成
                        sendLongClickMessage();
                        mState = STATE_LONG_CLICK;
                    } else if (mState == STATE_SCROLL) {//当为滑动状态的时候
                        //有滑动，那么不再触发长点击
                        removeLongClickMessage();
                    }
                } else if (fingerLeftAndRightMove(ev) && mSwipeTargetView != null) {//上下范围在50
                    boolean bool = false;
                    //这次位置与上一次的不一样，那么要滑这个之前把之前的归位
                    if (mBackPostion != mSwipeTargetPosition) {
                        mBackPostion = mSwipeTargetPosition;
                        bool = scrollBack();
                    }
                    //如果有scroll归位的话的话先跳过这次move
                    if (bool) {
                        return super.onTouchEvent(ev);
                    }
                    //scroll当前的View
                    int moveDistance = (int) ev.getX() - mXDown;//这个往右是正，往左是负
                    int distance = mXScrollDistance - moveDistance < 0 ? mXScrollDistance - moveDistance : 0;
                    mSwipeTargetView.scrollTo(distance, 0);
                    mState = STATE_SCROLL;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsScrolling) {//scroll正在滑动的话就不要做其他处理了
                    return super.onTouchEvent(ev);
                }
                if (mSwipeTargetView != null) {
                    //如果滑出的话，那么就滑到固定位置(只要滑出了 mBGWidth / 2 ，就算滑出去了)
                    if (Math.abs(mSwipeTargetView.getScrollX()) > mBGWidth / 2) {
                        if (mOnScrollListener != null) {
                            mOnScrollListener.onScrollOpen(mSwipeTargetView, mSwipeTargetPosition);
                        }
                        //滑出
                        int delta = 0;
                        if (Math.abs(mSwipeTargetView.getScrollX()) < mBGWidth) {
                            delta = mBGWidth - Math.abs(mSwipeTargetView.getScrollX());
                            mScroller.startScroll(mSwipeTargetView.getScrollX(), 0, -delta, 0, SCROLL_QUICK_TIME);
                        } else {
                            delta = mBGWidth - Math.abs(mSwipeTargetView.getScrollX());
                            mScroller.startScroll(mSwipeTargetView.getScrollX(), 0, -delta, 0, SCROLL_TIME);
                        }
                        postInvalidate();
                    } else {
                        //滑回去,归位
                        if (mOnScrollListener != null) {
                            mOnScrollListener.onScrollClose(mSwipeTargetView, mSwipeTargetPosition);
                        }
                        mScroller.startScroll(mSwipeTargetView.getScrollX(), 0, -mSwipeTargetView.getScrollX(), 0, SCROLL_QUICK_TIME);
                        postInvalidate();
                    }
                }
                mState = STATE_NOTHING;
                removeLongClickMessage();
                break;
            default:
                removeLongClickMessage();
                mState = STATE_NOTHING;
                break;
        }
        return super.onTouchEvent(ev);
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

    /**
     * remove掉message
     */
    private void removeLongClickMessage() {
        if (mHandler.hasMessages(MSG_WHAT_LONG_CLICK)) {
            mHandler.removeMessages(MSG_WHAT_LONG_CLICK);
        }
    }

    /**
     * sendMessage
     */
    private void sendLongClickMessage() {
        if (!mHandler.hasMessages(MSG_WHAT_LONG_CLICK)) {
            Message message = new Message();
            message.what = MSG_WHAT_LONG_CLICK;
            message.arg1 = mSwipeTargetPosition;
            mHandler.sendMessageDelayed(message, DELAY_TIME);
        }
    }

    /**
     * 展开的都scroll归位
     *
     * @return
     */
    private boolean scrollBack() {
        boolean bool = false;
        //计算当前listview上有多少个item
        int total = getLastVisiblePosition() - getFirstVisiblePosition();
        for (int i = 0; i < total; i++) {
            View backLayoutView = getChildAt(i);
            View backView = backLayoutView.findViewById(R.id.layout_item_edit_category);
            //判断当前这个view有没有scroll过
            if (backView.getScrollX() == 0) {
                continue;
            } else {//这里scroll回去不要动画也挺连贯了
                //如果scroll过的话就scroll到0,0
                backView.scrollTo(0, 0);
                if (mOnScrollListener != null) {
                    mOnScrollListener.onScrollClose(backView, i);
                }
                bool = true;
            }
        }
        return bool;
    }

    @Override
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        //do nothing
    }

    @Override
    public void onClick(View v) {
        //do nothing
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongClickListener = listener;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        final int action = event.getAction();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                return true;
            case DragEvent.ACTION_DRAG_LOCATION:
                //当前移动的item在listview中的position
                int position = pointToPosition((int) event.getX(), (int) event.getY());
                //如果位置发生了改变
                if (mBeforeCurrentPosition != position) {
                    //有时候得到的position是-1(AdapterView.INVALID_POSITION)，忽略掉
                    if (position >= 0) {
                        //判断是往上了还是往下了
                        mUp = position - mBeforeCurrentPosition > 0 ? false : true;
                        //记录移动之后上一次的位置
                        mBeforeBeforePosition = mBeforeCurrentPosition;
                        //记录当前位置
                        mBeforeCurrentPosition = position;
                    }
                }
                moveListViewUpOrDown(position);
                //有时候为-1(AdapterView.INVALID_POSITION)的情况，忽略掉
                if (position >= 0) {
                    //判断是不是已经换过位置了，如果没有换过，则进去换
                    if (position != mCurrentPosition) {
                        if (mUp) {//往上
                            //只是移动了一格
                            if (position - mBeforeBeforePosition == -1) {
                                T t = mDataList.get(position);
                                mDataList.set(position, mDataList.get(position + 1));
                                mDataList.set(position + 1, t);
                            } else {//一下子移动了好几个位置，其实可以和上面那个方法合并起来的
                                T t = mDataList.get(mBeforeBeforePosition);
                                for (int i = mBeforeBeforePosition; i > position; i--) {
                                    mDataList.set(i, mDataList.get(i - 1));
                                }
                                mDataList.set(position, t);
                            }
                        } else {
                            if (position - mBeforeBeforePosition == 1) {
                                T t = mDataList.get(position);
                                mDataList.set(position, mDataList.get(position - 1));
                                mDataList.set(position - 1, t);
                            } else {
                                T t = mDataList.get(mBeforeBeforePosition);
                                for (int i = mBeforeBeforePosition; i < position; i++) {
                                    mDataList.set(i, mDataList.get(i + 1));
                                }
                                mDataList.set(position, t);
                            }
                        }
                        mBaseAdapter.notifyDataSetChanged();
                        mCurrentPosition = position;
                    }
                }
                if (mOnDragListener != null) {
                    mOnDragListener.onDragViewMoving(mCurrentPosition);
                }
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                return true;
            case DragEvent.ACTION_DROP:
                mBaseAdapter.notifyDataSetChanged();
                if (mOnDragListener != null) {
                    mOnDragListener.onDragViewDown(mCurrentPosition);
                }
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                return true;
            default:
                break;
        }
        return false;
    }


    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        mBaseAdapter = (BaseAdapter) adapter;
    }

    public void setData(List<T> list) {
        mDataList = list;
    }

    /**
     * 如果到了两端，判断listview是往上滑动还是listview往下滑动
     *
     * @param position
     */
    private void moveListViewUpOrDown(int position) {
        //listview中最上面的显示的位置
        int firstPosition = getFirstVisiblePosition();
        //listview中最下面的显示的位置
        int lastPosition = getLastVisiblePosition();
        //能够往上的话往上
        if ((position == firstPosition || position == firstPosition + 1) && firstPosition != 0) {
            smoothScrollToPosition(firstPosition - 1);
        }
        //能够往下的话往下
        if ((position == lastPosition || position == lastPosition - 1) && lastPosition != getCount() - 1) {
            smoothScrollToPosition(lastPosition + 1);
        }
    }

    public interface OnDragListener {
        void onDragViewMoving(int position);

        void onDragViewDown(int position);
    }

    public void setOnDragListener(OnDragListener listenr) {
        mOnDragListener = listenr;
    }


}
