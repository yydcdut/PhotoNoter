package com.yydcdut.note.controller.login;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yydcdut.note.R;
import com.yydcdut.note.adapter.FrequentImageAdapter;
import com.yydcdut.note.controller.BaseFragment;
import com.yydcdut.note.model.CategoryDBModel;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.utils.Const;

/**
 * Created by yuyidong on 15/10/22.
 */
public class UserDetailFragment extends BaseFragment {
    private int type = 0;

    public static UserDetailFragment newInstance() {
        return new UserDetailFragment();
    }

    @Override
    public void getBundle(Bundle bundle) {
        type = bundle.getInt(Const.USER_DETAIL_TYPE);
    }

    @Override
    public void initSetting() {

    }

    @Override
    public View inflateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.frag_user_detail, null);
    }

    @Override
    public void initUI(View view) {
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.layout_user_detail);
        switch (type) {
            case 0:
                initUserDetail(linearLayout);
                break;
            case 1:
                initUserImage(linearLayout);
                break;
            case 2:
                initUserDetail(linearLayout);
                break;
        }
    }

    private void initUserDetail(LinearLayout linearLayout) {
        View citeView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail, null);
        ((ImageView) citeView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_pin_drop_white_24dp);
        ((TextView) citeView.findViewById(R.id.txt_item_column)).setText("City");
        ((TextView) citeView.findViewById(R.id.txt_item_user)).setText("Unkonwn");
        linearLayout.addView(citeView);

        View usageView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail, null);
        ((ImageView) usageView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_person_pin_white_24dp);
        ((TextView) usageView.findViewById(R.id.txt_item_column)).setText("Use Age");
        ((TextView) usageView.findViewById(R.id.txt_item_user)).setText("Unkonwn");
        linearLayout.addView(usageView);

        View phoneView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail, null);
        ((ImageView) phoneView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_phone_android_white_24dp);
        ((TextView) phoneView.findViewById(R.id.txt_item_column)).setText("Phone");
        ((TextView) phoneView.findViewById(R.id.txt_item_user)).setText("Unkonwn");
        linearLayout.addView(phoneView);

        View androidView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail, null);
        ((ImageView) androidView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_android_white_24dp);
        ((TextView) androidView.findViewById(R.id.txt_item_column)).setText("Android");
        ((TextView) androidView.findViewById(R.id.txt_item_user)).setText("Unkonwn");
        linearLayout.addView(androidView);

        View storageView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail, null);
        ((ImageView) storageView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_sd_storage_white_24dp);
        ((TextView) storageView.findViewById(R.id.txt_item_column)).setText("Storage");
        ((TextView) storageView.findViewById(R.id.txt_item_user)).setText("Unkonwn");
        linearLayout.addView(storageView);

        View cloudView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail, null);
        ((ImageView) cloudView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_cloud_circle_white_24dp);
        ((TextView) cloudView.findViewById(R.id.txt_item_column)).setText("Cloud");
        ((TextView) cloudView.findViewById(R.id.txt_item_user)).setText("Unkonwn");
        linearLayout.addView(cloudView);
    }

    private void initUserImage(LinearLayout linearLayout) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_image, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_item_user_center);
        recyclerView.setAdapter(new FrequentImageAdapter(getContext(),
                PhotoNoteDBModel.getInstance().findByCategoryLabel(CategoryDBModel.getInstance().findAll().get(0).getLabel(), -1)));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        linearLayout.addView(view);
    }

    @Override
    public void initListener(View view) {

    }

    @Override
    public void initData() {

    }

    @Override
    public void saveSettingWhenPausing() {

    }
}
