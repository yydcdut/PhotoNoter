package com.yydcdut.sdlv;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by yuyidong on 15/9/30.
 */
class DragListView<T> extends ListView implements View.OnDragListener {
    /* 判断drag往上还是往下 */
    private boolean mUp = false;
    /* 当前drag所在ListView中的位置 */
    private int mCurrentPosition;
    /* 之前drag所在ListView中的位置 */
    private int mBeforeCurrentPosition;
    /* 之前之前drag所在ListView中的位置 */
    private int mBeforeBeforePosition;
    /* 适配器 */
    private BaseAdapter mSDAdapter;
    /* 数据 */
    private List<T> mDataList;
    /* 监听器 */
    private OnDragListener mOnDragListener;

    public DragListView(Context context) {
        this(context, null);
    }

    public DragListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setOnDragListener(this);
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
                //当前移动的item在ListView中的position
                int position = pointToPosition((int) event.getX(), (int) event.getY());
                //如果位置发生了改变
                if (mBeforeCurrentPosition != position) {
                    //有时候得到的position是-1(AdapterView.INVALID_POSITION)，忽略掉
                    if (position >= 0) {
                        //判断是往上了还是往下了
                        mUp = position - mBeforeCurrentPosition <= 0;
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
                        mSDAdapter.notifyDataSetChanged();
                        //更新位置
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
                mSDAdapter.notifyDataSetChanged();
                for (int i = 0; i < getLastVisiblePosition() - getFirstVisiblePosition(); i++) {
                    ItemMainLayout view = (ItemMainLayout) getChildAt(i);
                    setItemVisible(view);
                }
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

    /**
     * 将透明的那部分变回来
     *
     * @param itemMainLayout
     */
    private void setItemVisible(ItemMainLayout itemMainLayout) {
        if (itemMainLayout.getItemLeftBackGroundLayout().getVisibility() != View.VISIBLE) {
            itemMainLayout.getItemLeftBackGroundLayout().setVisibility(View.VISIBLE);
        }
        if (itemMainLayout.getItemRightBackGroundLayout().getVisibility() != View.VISIBLE) {
            itemMainLayout.getItemRightBackGroundLayout().setVisibility(View.VISIBLE);
        }
        if (itemMainLayout.getItemCustomLayout().getBackGroundImage().getVisibility() != View.VISIBLE) {
            itemMainLayout.getItemCustomLayout().getBackGroundImage().setVisibility(View.VISIBLE);
        }
    }


    /**
     * 如果到了两端，判断ListView是往上滑动还是ListView往下滑动
     *
     * @param position
     */
    private void moveListViewUpOrDown(int position) {
        //ListView中最上面的显示的位置
        int firstPosition = getFirstVisiblePosition();
        //ListView中最下面的显示的位置
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

    protected void setRawAdapter(ListAdapter adapter) {
        mSDAdapter = (BaseAdapter) adapter;
    }

    protected void setDragPosition(int position) {
        mCurrentPosition = position;
        mBeforeCurrentPosition = position;
        mBeforeBeforePosition = position;
        if (mOnDragListener != null) {
            ItemMainLayout view = (ItemMainLayout) getChildAt(position - getFirstVisiblePosition());
            view.getItemCustomLayout().getBackGroundImage().setVisibility(GONE);
            view.getItemLeftBackGroundLayout().setVisibility(GONE);
            view.getItemRightBackGroundLayout().setVisibility(GONE);
            ClipData.Item item = new ClipData.Item("1");
            ClipData data = new ClipData("1", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
            view.startDrag(data, new View.DragShadowBuilder(view), null, 0);
            mOnDragListener.onDragViewStart(position);
        }
    }

    @Deprecated
    @Override
    public void setOnDragListener(View.OnDragListener l) {
    }

    /**
     * 设置drag的监听器，加入数据
     *
     * @param onDragListener
     * @param dataList
     */
    public void setOnDragListener(OnDragListener onDragListener, List<T> dataList) {
        mOnDragListener = onDragListener;
        mDataList = dataList;
    }

    /**
     * 更新数据
     *
     * @param dataList
     */
    public void updateDataList(List<T> dataList) {
        mDataList = dataList;
    }

    /**
     * 当发生drag的时候触发的监听器
     */
    public interface OnDragListener {
        /**
         * 开始drag
         *
         * @param position
         */
        void onDragViewStart(int position);

        /**
         * drag的正在移动
         *
         * @param position
         */
        void onDragViewMoving(int position);

        /**
         * drag的放下了
         *
         * @param position
         */
        void onDragViewDown(int position);
    }
}
