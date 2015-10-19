package com.yydcdut.note.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yydcdut.note.R;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuyidong on 15-5-7.
 */
public class IntroducePagerAdapter extends PagerAdapter {

    private String[] mUrl = new String[]{"assets://img_introduce_0.png", "assets://img_introduce_1.png", "assets://img_introduce_2.png",
            "assets://img_introduce_3.png", "assets://img_introduce_4.png", "assets://img_introduce_5.png"};
    private Context mContext;

    private Map<Integer, View> mViewMap;

    public IntroducePagerAdapter(Context context) {
        this.mContext = context;
        mViewMap = new HashMap<>();
    }

    @Override
    public int getCount() {
        return mUrl.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_vp_img, null);
        ImageView imageView = (ImageView) v.findViewById(R.id.img_vp_login);
        ImageLoaderManager.displayImage(mUrl[position], imageView);
        mViewMap.put(position, v);
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewMap.get(position));
    }
}
