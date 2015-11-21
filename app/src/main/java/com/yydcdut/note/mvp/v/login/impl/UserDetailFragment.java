package com.yydcdut.note.mvp.v.login.impl;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yydcdut.note.R;
import com.yydcdut.note.adapter.FrequentImageAdapter;
import com.yydcdut.note.model.CategoryDBModel;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.mvp.p.login.IUserDetailFragPresenter;
import com.yydcdut.note.mvp.p.login.impl.UserDetailFragPresenterImpl;
import com.yydcdut.note.mvp.v.BaseFragment;
import com.yydcdut.note.mvp.v.login.IUserDetailFragView;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.view.CircleProgressBarLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuyidong on 15/10/22.
 */
public class UserDetailFragment extends BaseFragment implements IUserDetailFragView,
        View.OnClickListener {
    private static final String TAG_QQ = "tag_qq";
    private static final String TAG_EVERNOTE = "tag_evernote";

    @InjectView(R.id.layout_user_detail)
    LinearLayout mLinearLayout;

    private TextView mLocationView;

    private IUserDetailFragPresenter mUserDetailFragPresenter;

    public static UserDetailFragment newInstance() {
        return new UserDetailFragment();
    }

    @Override
    public void getBundle(Bundle bundle) {
        mUserDetailFragPresenter = new UserDetailFragPresenterImpl(getActivity(),
                bundle.getInt(Const.USER_DETAIL_TYPE));

    }

    @Override
    public View inflateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.frag_user_detail, null);
    }

    @Override
    public void initUI(View view) {
        ButterKnife.inject(this, view);
        mUserDetailFragPresenter.attachView(this);
    }

    @Override
    public void initUserDetail(String location, String useAge, String phone, String android, String storage) {
        View citeView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) citeView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_pin_drop_white_24dp);
        ((TextView) citeView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_city));
        mLocationView = (TextView) citeView.findViewById(R.id.txt_item_user);
        mLocationView.setText(location);
        mLinearLayout.addView(citeView);

        View usageView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) usageView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_person_pin_white_24dp);
        ((TextView) usageView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_usage_age));
        ((TextView) usageView.findViewById(R.id.txt_item_user)).setText(useAge);
        mLinearLayout.addView(usageView);

        View phoneView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) phoneView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_phone_android_white_24dp);
        ((TextView) phoneView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_phone));
        ((TextView) phoneView.findViewById(R.id.txt_item_user)).setText(phone);
        mLinearLayout.addView(phoneView);

        View androidView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) androidView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_android_white_24dp);
        ((TextView) androidView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_android));
        ((TextView) androidView.findViewById(R.id.txt_item_user)).setText(android);
        mLinearLayout.addView(androidView);

        View storageView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) storageView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_sd_storage_white_24dp);
        ((TextView) storageView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_storage));
        ((TextView) storageView.findViewById(R.id.txt_item_user)).setText(storage);
        mLinearLayout.addView(storageView);
    }

    @Override
    public void updateLocation(String location) {
        mLocationView.setText(location);
    }

    @Override
    public void initUserImage() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_image, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_item_user_center);
        recyclerView.setAdapter(new FrequentImageAdapter(getContext(),
                PhotoNoteDBModel.getInstance().findByCategoryLabel(CategoryDBModel.getInstance().findAll().get(0).getLabel(), -1)));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        mLinearLayout.addView(view);
    }

    @Override
    public void initUserInfo(boolean isQQLogin, String QQName, boolean isEvernoteLogin,
                             String evernoteName, String useStorage, String noteNumber,
                             String sandboxNumber, String wordNumber, String cloud) {
        View qqView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_image, null);
        ((ImageView) qqView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_person_info_qq);
        if (isQQLogin) {
            ((TextView) qqView.findViewById(R.id.txt_item_column)).setText(QQName);
            ((ImageView) qqView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_clear_white_24dp);
        } else {
            ((TextView) qqView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.not_login));
            ((ImageView) qqView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_link_white_24dp);
        }
        qqView.findViewById(R.id.img_item_user).setOnClickListener(this);
        qqView.findViewById(R.id.img_item_user).setTag(TAG_QQ);
        mLinearLayout.addView(qqView);

        View evernoteView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_image, null);
        ((ImageView) evernoteView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_evernote_fab);
        if (isEvernoteLogin) {
            ((TextView) evernoteView.findViewById(R.id.txt_item_column)).setText(evernoteName);
            ((ImageView) evernoteView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_clear_white_24dp);
        } else {
            ((TextView) evernoteView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.not_login));
            ((ImageView) evernoteView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_link_white_24dp);
        }
        evernoteView.findViewById(R.id.img_item_user).setOnClickListener(this);
        evernoteView.findViewById(R.id.img_item_user).setTag(TAG_EVERNOTE);
        mLinearLayout.addView(evernoteView);

        View folderView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) folderView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_folder_open_white_24dp);
        ((TextView) folderView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_folder));
        ((TextView) folderView.findViewById(R.id.txt_item_user)).setText(useStorage);
        mLinearLayout.addView(folderView);

        View noteView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) noteView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_content_paste_white_24dp);
        ((TextView) noteView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_notes));
        ((TextView) noteView.findViewById(R.id.txt_item_user)).setText(noteNumber);
        mLinearLayout.addView(noteView);

        View sandboxView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) sandboxView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_crop_original_white_24dp);
        ((TextView) sandboxView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_sanbox));
        ((TextView) sandboxView.findViewById(R.id.txt_item_user)).setText(sandboxNumber);
        mLinearLayout.addView(sandboxView);

        View wordView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) wordView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_text_format_white_24dp);
        ((TextView) wordView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_words));
        ((TextView) wordView.findViewById(R.id.txt_item_user)).setText(wordNumber);
        mLinearLayout.addView(wordView);

        View cloudView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) cloudView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_cloud_circle_white_24dp);
        ((TextView) cloudView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_cloud));
        ((TextView) cloudView.findViewById(R.id.txt_item_user)).setText(cloud);
        mLinearLayout.addView(cloudView);
    }

    @Override
    public void logoutQQ() {
        View qqView = mLinearLayout.getChildAt(0);
        ((TextView) qqView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.not_login));
        ((ImageView) qqView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_link_white_24dp);
        ((ImageView) getActivity().findViewById(R.id.img_user)).setImageResource(R.drawable.ic_no_user);
        getActivity().findViewById(R.id.txt_name).setVisibility(View.INVISIBLE);
    }

    @Override
    public void logoutEvernote() {
        ((ImageView) getActivity().findViewById(R.id.img_user_two)).setImageResource(R.drawable.ic_evernote_gray);
        View evernoteView = mLinearLayout.getChildAt(1);
        ((TextView) evernoteView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.not_login));
        ((ImageView) evernoteView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_link_white_24dp);
    }

    @Override
    public void showProgressBar() {
        ((CircleProgressBarLayout) getActivity().findViewById(R.id.layout_progress)).show();
    }

    @Override
    public void hideProgressBar() {
        ((CircleProgressBarLayout) getActivity().findViewById(R.id.layout_progress)).hide();
    }

    @Override
    public void showQQ(String name, String path) {
        ImageLoaderManager.displayImage(path, ((ImageView) getActivity().findViewById(R.id.img_user)));
        getActivity().findViewById(R.id.txt_name).setVisibility(View.VISIBLE);
        ((TextView) getActivity().findViewById(R.id.txt_name)).setText(name);

        LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.layout_user_detail);
        View qqView = linearLayout.getChildAt(0);
        ((TextView) qqView.findViewById(R.id.txt_item_column)).setText(name);
        ((ImageView) qqView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_clear_white_24dp);

    }

    @Override
    public void showSnakebar(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void initListener(View view) {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        boolean hasNet = mUserDetailFragPresenter.checkInternet();
        if (!hasNet) {
            return;
        }
        switch (((String) v.getTag())) {
            case TAG_QQ:
                mUserDetailFragPresenter.loginOrOutQQ();
                break;
            case TAG_EVERNOTE:
                mUserDetailFragPresenter.loginOrOutEvernote();
                break;
        }
    }

}
