package com.yydcdut.note.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.controller.note.DetailImageFragment;
import com.yydcdut.note.controller.note.DetailTextFragment;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.utils.Const;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yyd on 15-3-29.
 */
public class DetailPagerAdapter extends PagerAdapter {
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_IMAGE = 0;

    private int mType;


    private Map<Integer, View> mViewCacheView;
    private List<PhotoNote> mPhotoNoteGroup;
    private FragmentManager mFragmentManager;

    private int mComparator;

    public DetailPagerAdapter(FragmentManager fragmentManager, String categoryName, int comparatorFactory, int type) {
        this.mPhotoNoteGroup = PhotoNoteDBModel.getInstance().findByCategoryLabel(categoryName, comparatorFactory);
        this.mFragmentManager = fragmentManager;
        this.mType = type;
        this.mComparator = comparatorFactory;
        mViewCacheView = new HashMap<Integer, View>();
    }

    @Override
    public int getCount() {
        return mPhotoNoteGroup.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment;
        switch (mType) {
            case TYPE_IMAGE:
                fragment = DetailImageFragment.newInstance();
                break;
            case TYPE_TEXT:
            default:
                fragment = DetailTextFragment.newInstance();
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putString(Const.CATEGORY_LABEL, mPhotoNoteGroup.get(position).getCategoryLabel());
        bundle.putInt(Const.PHOTO_POSITION, position);
        bundle.putInt(Const.COMPARATOR_FACTORY, mComparator);
        fragment.setArguments(bundle);
        if (!fragment.isAdded()) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.add(fragment, String.valueOf(position));
            ft.commit();
            mFragmentManager.executePendingTransactions();
        }
        if (fragment.getView().getParent() == null) {
            container.addView(fragment.getView()); // 为viewpager增加布局
        }
        mViewCacheView.put(position, fragment.getView());
        return fragment.getView();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewCacheView.get(position)); // 移出viewpager两边之外的page布局
    }

}
