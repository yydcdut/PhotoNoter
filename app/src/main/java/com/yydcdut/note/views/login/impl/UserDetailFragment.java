package com.yydcdut.note.views.login.impl;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yydcdut.note.R;
import com.yydcdut.note.presenters.login.impl.UserDetailFragPresenterImpl;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.views.BaseFragment;
import com.yydcdut.note.views.login.IUserDetailFragView;
import com.yydcdut.note.widget.CircleProgressBarLayout;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 15/10/22.
 */
public class UserDetailFragment extends BaseFragment implements IUserDetailFragView,
        View.OnClickListener {
    private static final String TAG_QQ = "tag_qq";
    private static final String TAG_EVERNOTE = "tag_evernote";

    @Bind(R.id.layout_user_detail)
    LinearLayout mLinearLayout;

    private TextView mLocationView;

    @Inject
    UserDetailFragPresenterImpl mUserDetailFragPresenter;

    public static UserDetailFragment newInstance() {
        return new UserDetailFragment();
    }

    @Override
    public void getBundle(Bundle bundle) {
        mUserDetailFragPresenter.bindData(bundle.getInt(Const.USER_DETAIL_TYPE));
    }

    @Override
    public View inflateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.frag_user_detail, null);
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
        mIPresenter = mUserDetailFragPresenter;
    }

    @Override
    public void initUI(View view) {
        ButterKnife.bind(this, view);
        mUserDetailFragPresenter.attachView(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void initUserDetail(String location, String useAge, String phone, String android, String storage) {
        View citeView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) citeView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_pin_drop_white_24dp);
        ((TextView) citeView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_city));
        mLocationView = (TextView) citeView.findViewById(R.id.txt_item_user);
        mLocationView.setText(location);
        mLocationView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mLocationView.setMarqueeRepeatLimit(-1);
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
    public void addView() {
        View qqView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_image, null);
        mLinearLayout.addView(qqView, 0);
        View evernoteView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_image, null);
        mLinearLayout.addView(evernoteView, 1);
        View folderView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        mLinearLayout.addView(folderView, 2);
        View noteView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        mLinearLayout.addView(noteView, 3);
        View sandboxView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        mLinearLayout.addView(sandboxView, 4);
        View wordView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        mLinearLayout.addView(wordView, 5);
        View cloudView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        mLinearLayout.addView(cloudView, 6);
    }

    @Override
    public void addQQView(boolean isQQLogin, String QQName) {
        View qqView = mLinearLayout.getChildAt(0);
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
    }

    @Override
    public void addEvernoteView(boolean isEvernoteLogin, String evernoteName) {
        View evernoteView = mLinearLayout.getChildAt(1);
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
    }

    @Override
    public void addUseStorageView(String useStorage) {
        View folderView = mLinearLayout.getChildAt(2);
        ((ImageView) folderView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_folder_open_white_24dp);
        ((TextView) folderView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_folder));
        ((TextView) folderView.findViewById(R.id.txt_item_user)).setText(useStorage);
    }

    @Override
    public void addNoteNumberView(String noteNumber) {
        View noteView = mLinearLayout.getChildAt(3);
        ((ImageView) noteView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_content_paste_white_24dp);
        ((TextView) noteView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_notes));
        ((TextView) noteView.findViewById(R.id.txt_item_user)).setText(noteNumber);
    }

    @Override
    public void addSandBoxNumber(String sandboxNumber) {
        View sandboxView = mLinearLayout.getChildAt(4);
        ((ImageView) sandboxView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_crop_original_white_24dp);
        ((TextView) sandboxView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_sandbox));
        ((TextView) sandboxView.findViewById(R.id.txt_item_user)).setText(sandboxNumber);
    }

    @Override
    public void addWordNumber(String wordNumber) {
        View wordView = mLinearLayout.getChildAt(5);
        ((ImageView) wordView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_text_format_white_24dp);
        ((TextView) wordView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_words));
        ((TextView) wordView.findViewById(R.id.txt_item_user)).setText(wordNumber);
    }

    @Override
    public void addCloud(String cloud) {
        View cloudView = mLinearLayout.getChildAt(6);
        ((ImageView) cloudView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_cloud_circle_white_24dp);
        ((TextView) cloudView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_cloud));
        ((TextView) cloudView.findViewById(R.id.txt_item_user)).setText(cloud);
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
