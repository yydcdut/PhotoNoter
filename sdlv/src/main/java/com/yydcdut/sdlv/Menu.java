package com.yydcdut.sdlv;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/10/8.
 */
public final class Menu {
    private List<MenuItem> mLeftMenuItems;
    private List<MenuItem> mRightMenuItems;

    private int mItemHeight;
    private Drawable itemBackGroundDrawable;
    private boolean mWannaOver = true;

    public Menu(int itemHeight, Drawable itemBackGroundDrawable) {
        this(itemHeight, itemBackGroundDrawable, true);
    }

    public Menu(int itemHeight, Drawable itemBackGroundDrawable, boolean wannaOver) {
        this.mItemHeight = itemHeight;
        this.itemBackGroundDrawable = itemBackGroundDrawable;
        this.mWannaOver = wannaOver;
        mLeftMenuItems = new ArrayList<>();
        mRightMenuItems = new ArrayList<>();
    }

    public int getItemHeight() {
        return mItemHeight;
    }

    public Drawable getItemBackGroundDrawable() {
        return itemBackGroundDrawable;
    }

    public boolean isWannaOver() {
        return mWannaOver;
    }

    public void addItem(MenuItem menuItem) {
        if (menuItem.direction == MenuItem.DIRECTION_LEFT) {
            mLeftMenuItems.add(menuItem);
        } else {
            mRightMenuItems.add(menuItem);
        }
    }

    public void addItem(MenuItem menuItem, int position) {
        if (menuItem.direction == MenuItem.DIRECTION_LEFT) {
            mLeftMenuItems.add(position, menuItem);
        } else {
            mRightMenuItems.add(position, menuItem);
        }
    }

    public boolean removeItem(MenuItem menuItem) {
        if (menuItem.direction == MenuItem.DIRECTION_LEFT) {
            return mLeftMenuItems.remove(menuItem);
        } else {
            return mRightMenuItems.remove(menuItem);
        }
    }

    protected int getTotalBtnLength(int direction) {
        int total = 0;
        if (direction == MenuItem.DIRECTION_LEFT) {
            for (MenuItem menuItem : mLeftMenuItems) {
                total += menuItem.width;
            }
            return total;
        } else {
            for (MenuItem menuItem : mRightMenuItems) {
                total += menuItem.width;
            }
            return total;
        }
    }

    /**
     * 这个函数并不是十分安全，因为获取到List之后自己操作add或者remove的话btn总长度不会有操作变化
     *
     * @param direction
     * @return
     */
    protected List<MenuItem> getMenuItems(int direction) {
        if (direction == MenuItem.DIRECTION_LEFT) {
            return mLeftMenuItems;
        } else {
            return mRightMenuItems;
        }
    }

}
