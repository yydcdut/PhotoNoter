package com.yydcdut.note.controller.login;

import android.os.Build;
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
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.TimeDecoder;

import java.text.DecimalFormat;

/**
 * Created by yuyidong on 15/10/22.
 */
public class UserDetailFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG_QQ = "tag_qq";
    private static final String TAG_EVERNOTE = "tag_evernote";

    private int mType = 0;

    public static UserDetailFragment newInstance() {
        return new UserDetailFragment();
    }

    @Override
    public void getBundle(Bundle bundle) {
        mType = bundle.getInt(Const.USER_DETAIL_TYPE);
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
        switch (mType) {
            case 0:
                initUserDetail(linearLayout);
                break;
            case 1:
                initUserImage(linearLayout);
                break;
            case 2:
                initUserInfo(linearLayout);
                break;
        }
    }

    private void initUserDetail(LinearLayout linearLayout) {
        View citeView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) citeView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_pin_drop_white_24dp);
        ((TextView) citeView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_city));
        ((TextView) citeView.findViewById(R.id.txt_item_user)).setText(getContext().getResources().getString(R.string.uc_unkown));
        linearLayout.addView(citeView);

        View usageView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) usageView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_person_pin_white_24dp);
        ((TextView) usageView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_usage_age));
        long startTime = LocalStorageUtils.getInstance().getStartUsageTime();
        long now = System.currentTimeMillis();
        ((TextView) usageView.findViewById(R.id.txt_item_user)).setText(TimeDecoder.calculateDeltaTime(now, startTime) + " " +
                getResources().getString(R.string.uc_usage_age_unit));
        linearLayout.addView(usageView);

        View phoneView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) phoneView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_phone_android_white_24dp);
        ((TextView) phoneView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_phone));
        ((TextView) phoneView.findViewById(R.id.txt_item_user)).setText(android.os.Build.MODEL + "");
        linearLayout.addView(phoneView);

        View androidView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) androidView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_android_white_24dp);
        ((TextView) androidView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_android));
        ((TextView) androidView.findViewById(R.id.txt_item_user)).setText(Build.VERSION.RELEASE + "");
        linearLayout.addView(androidView);

        View storageView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) storageView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_sd_storage_white_24dp);
        ((TextView) storageView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_storage));
        long[] storages = FilePathUtils.getSDCardStorage();
        if (storages[0] == -1) {
            ((TextView) storageView.findViewById(R.id.txt_item_user)).setText(getResources().getString(R.string.uc_no_sdcard));
        } else {
            if (storages[0] > 1024) {
                float avail = ((float) storages[0]) / 1024;
                float total = ((float) storages[1]) / 1024;
                DecimalFormat decimalFormat = new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足2位,会以0补足
                ((TextView) storageView.findViewById(R.id.txt_item_user)).setText((decimalFormat.format(avail) + "G / ") + (decimalFormat.format(total) + "G"));
            } else {
                ((TextView) storageView.findViewById(R.id.txt_item_user)).setText((storages[0] + "M / ") + (storages[1] + "M"));
            }
        }
        linearLayout.addView(storageView);

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

    private void initUserInfo(LinearLayout linearLayout) {
        View qqView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_image, null);
        qqView.setTag(TAG_QQ);
        ((ImageView) qqView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_person_info_qq);
        if (UserCenter.getInstance().isLoginQQ()) {
            ((TextView) qqView.findViewById(R.id.txt_item_column)).setText(UserCenter.getInstance().getQQ().getName());
            ((ImageView) qqView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_clear_white_24dp);
        } else {
            ((TextView) qqView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.not_login));
            ((ImageView) qqView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_link_white_24dp);
        }
        linearLayout.addView(qqView);

        View evernoteView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_image, null);
        evernoteView.setTag(TAG_EVERNOTE);
        ((ImageView) evernoteView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_evernote_fab);
        if (UserCenter.getInstance().isLoginEvernote()) {
            ((TextView) evernoteView.findViewById(R.id.txt_item_column)).setText(UserCenter.getInstance().getEvernote().getUsername());
            ((ImageView) qqView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_clear_white_24dp);
        } else {
            ((TextView) qqView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.not_login));
            ((ImageView) qqView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_link_white_24dp);
        }
        linearLayout.addView(evernoteView);

        View cloudView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) cloudView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_cloud_circle_white_24dp);
        ((TextView) cloudView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_cloud));
        ((TextView) cloudView.findViewById(R.id.txt_item_user)).setText(getContext().getResources().getString(R.string.uc_unkown));
        linearLayout.addView(cloudView);

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

    @Override
    public void onClick(View v) {
        switch (((String) v.getTag())) {
            case TAG_QQ:
                break;
            case TAG_EVERNOTE:
                break;
        }
    }
}
