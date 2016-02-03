package com.yydcdut.note.adapter;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.views.note.impl.DetailFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yyd on 15-3-29.
 */
public class DetailPagerAdapter extends PagerAdapter {
    private Map<Integer, View> mViewCacheView;
    private List<PhotoNote> mPhotoNoteGroup;
    private FragmentManager mFragmentManager;

    private int mComparator;

    public DetailPagerAdapter(List<PhotoNote> list, FragmentManager fragmentManager, int comparatorFactory) {
        this.mPhotoNoteGroup = list;
        this.mFragmentManager = fragmentManager;
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
        DetailFragment fragment = DetailFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putInt(Const.CATEGORY_ID_4_PHOTNOTES, mPhotoNoteGroup.get(position).getCategoryId());
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
        mViewCacheView.remove(position);
    }

    public View getItemView(int position) {
        return mViewCacheView.get(position);
    }
}
