package com.yydcdut.note.controller.login;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginFragment;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yydcdut.note.BuildConfig;
import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.adapter.FrequentImageAdapter;
import com.yydcdut.note.bean.IUser;
import com.yydcdut.note.controller.BaseFragment;
import com.yydcdut.note.model.CategoryDBModel;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.model.SandBoxDBModel;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.TimeDecoder;
import com.yydcdut.note.view.CircleProgressBarLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by yuyidong on 15/10/22.
 */
public class UserDetailFragment extends BaseFragment implements View.OnClickListener, Handler.Callback,
        EvernoteLoginFragment.ResultCallback {
    private static final String TAG_QQ = "tag_qq";
    private static final String TAG_EVERNOTE = "tag_evernote";

    private static final int MESSAGE_LOGIN_QQ_OK = 1;
    private static final int MESSAGE_LOGIN_EVERNOTE_OK = 2;
    private Handler mHandler;

    private Tencent mTencent;

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
                mHandler = new Handler(this);
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
                DecimalFormat decimalFormat = new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足1位,会以0补足
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
        ((ImageView) qqView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_person_info_qq);
        if (UserCenter.getInstance().isLoginQQ()) {
            ((TextView) qqView.findViewById(R.id.txt_item_column)).setText(UserCenter.getInstance().getQQ().getName());
            ((ImageView) qqView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_clear_white_24dp);
        } else {
            ((TextView) qqView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.not_login));
            ((ImageView) qqView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_link_white_24dp);
        }
        qqView.findViewById(R.id.img_item_user).setOnClickListener(this);
        qqView.findViewById(R.id.img_item_user).setTag(TAG_QQ);
        linearLayout.addView(qqView);

        View evernoteView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_image, null);
        ((ImageView) evernoteView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_evernote_fab);
        if (UserCenter.getInstance().isLoginEvernote()) {
            ((TextView) evernoteView.findViewById(R.id.txt_item_column)).setText(UserCenter.getInstance().getEvernote().getUsername());
            ((ImageView) evernoteView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_clear_white_24dp);
        } else {
            ((TextView) evernoteView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.not_login));
            ((ImageView) evernoteView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_link_white_24dp);
        }
        evernoteView.findViewById(R.id.img_item_user).setOnClickListener(this);
        evernoteView.findViewById(R.id.img_item_user).setTag(TAG_EVERNOTE);
        linearLayout.addView(evernoteView);

        View folderView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) folderView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_folder_open_white_24dp);
        ((TextView) folderView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_folder));
        long storage = FilePathUtils.getFolderStorage();
        if (storage == -1) {
            ((TextView) folderView.findViewById(R.id.txt_item_user)).setText(getContext().getResources().getString(R.string.uc_unkown));
        } else {
            if (storage > 1024) {
                float storageF = storage / 1024.0f;
                DecimalFormat decimalFormat = new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足1位,会以0补足
                ((TextView) folderView.findViewById(R.id.txt_item_user)).setText(decimalFormat.format(storageF) + "G");
            } else {
                ((TextView) folderView.findViewById(R.id.txt_item_user)).setText(storage + "M");
            }
        }
        linearLayout.addView(folderView);

        View noteView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) noteView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_content_paste_white_24dp);
        ((TextView) noteView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_notes));
        ((TextView) noteView.findViewById(R.id.txt_item_user)).setText(PhotoNoteDBModel.getInstance().getAllNumber() + "");
        linearLayout.addView(noteView);

        View sandboxView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) sandboxView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_crop_original_white_24dp);
        ((TextView) sandboxView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_sanbox));
        ((TextView) sandboxView.findViewById(R.id.txt_item_user)).setText(SandBoxDBModel.getInstance().getAllNumber() + "");
        linearLayout.addView(sandboxView);

        View wordView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_center_detail_text, null);
        ((ImageView) wordView.findViewById(R.id.img_item_icon)).setImageResource(R.drawable.ic_text_format_white_24dp);
        ((TextView) wordView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.uc_words));
        ((TextView) wordView.findViewById(R.id.txt_item_user)).setText(getContext().getResources().getString(R.string.uc_unkown));
        linearLayout.addView(wordView);

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
        if (mType == 2) {
            switch (((String) v.getTag())) {
                case TAG_QQ:
                    if (UserCenter.getInstance().isLoginQQ()) {
                        UserCenter.getInstance().logoutQQ();
                        LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.layout_user_detail);
                        View qqView = linearLayout.getChildAt(0);
                        ((TextView) qqView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.not_login));
                        ((ImageView) qqView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_link_white_24dp);

                        ((ImageView) getActivity().findViewById(R.id.img_user)).setImageResource(R.drawable.ic_no_user);
                        getActivity().findViewById(R.id.txt_name).setVisibility(View.INVISIBLE);
                    } else {
                        mTencent = Tencent.createInstance(BuildConfig.TENCENT_KEY, getActivity().getApplicationContext());
                        mTencent.login(this, "all", new BaseUiListener());
                    }
                    break;
                case TAG_EVERNOTE:
                    if (UserCenter.getInstance().isLoginEvernote()) {
                        UserCenter.getInstance().logoutEvernote();
                        LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.layout_user_detail);
                        View evernoteView = linearLayout.getChildAt(1);
                        ((TextView) evernoteView.findViewById(R.id.txt_item_column)).setText(getContext().getResources().getString(R.string.not_login));
                        ((ImageView) evernoteView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_link_white_24dp);
                    } else {
                        EvernoteSession.getInstance().authenticate(getActivity());
                    }
                    break;
            }
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_LOGIN_QQ_OK:
                IUser qqUser = UserCenter.getInstance().getQQ();
                if (new File(FilePathUtils.getQQImagePath()).exists()) {
                    ImageLoaderManager.displayImage("file://" + FilePathUtils.getQQImagePath(),
                            ((ImageView) getActivity().findViewById(R.id.img_user)));
                } else {
                    ImageLoaderManager.displayImage(qqUser.getNetImagePath(),
                            ((ImageView) getActivity().findViewById(R.id.img_user)));
                }
                getActivity().findViewById(R.id.txt_name).setVisibility(View.VISIBLE);
                ((TextView) getActivity().findViewById(R.id.txt_name)).setText(qqUser.getName());

                LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.layout_user_detail);
                View qqView = linearLayout.getChildAt(0);
                ((TextView) qqView.findViewById(R.id.txt_item_column)).setText(qqUser.getName());
                ((ImageView) qqView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_clear_white_24dp);
                ((CircleProgressBarLayout) getActivity().findViewById(R.id.layout_progress)).hide();
                break;
            case MESSAGE_LOGIN_EVERNOTE_OK:
                ((ImageView) getActivity().findViewById(R.id.img_user_two)).setImageResource(R.drawable.ic_evernote_color);

                LinearLayout linearLayout2 = (LinearLayout) getView().findViewById(R.id.layout_user_detail);
                View evernoteView = linearLayout2.getChildAt(1);
                ((TextView) evernoteView.findViewById(R.id.txt_item_column)).setText(UserCenter.getInstance().getEvernote().getUsername());
                ((ImageView) evernoteView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_clear_white_24dp);
                ((CircleProgressBarLayout) getActivity().findViewById(R.id.layout_progress)).hide();
                break;
        }
        return false;
    }

    @Override
    public void onLoginFinished(boolean successful) {
        if (successful) {
            ((CircleProgressBarLayout) getActivity().findViewById(R.id.layout_progress)).show();
            UserCenter.getInstance().LoginEvernote();
            mHandler.sendEmptyMessage(MESSAGE_LOGIN_EVERNOTE_OK);
        }
    }

    /**
     * 当自定义的监听器实现IUiListener接口后，必须要实现接口的三个方法，
     * onComplete  onCancel onError
     * 分别表示第三方登录成功，取消 ，错误。
     */
    private class BaseUiListener implements IUiListener {

        public void onCancel() {
        }

        /*
            {
                "access_token": "15D69FFB81BC403D9DB3DFACCF2FDDFF",
	            "authority_cost": 2490,
	            "expires_in": 7776000,
	            "login_cost": 775,
	            "msg": "",
	            "openid": "563559BEF3E2F97B693A6F88308F8D21",
	            "pay_token": "0E13A21128EAFB5E39048E5DE9478AD4",
	            "pf": "desktop_m_qq-10000144-android-2002-",
	            "pfkey": "11157020df5d6a8ebeaa150e2a7c68ce",
	            "query_authority_cost": 788,
	            "ret": 0
            }
        */
        public void onComplete(Object response) {
            String openid = null;
            String accessToken = null;
            try {
                openid = ((JSONObject) response).getString("openid");
                accessToken = ((JSONObject) response).getString("access_token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*
              到此已经获得OpenID以及其他你想获得的内容了
              QQ登录成功了，我们还想获取一些QQ的基本信息，比如昵称，头像
              sdk给我们提供了一个类UserInfo，这个类中封装了QQ用户的一些信息，我么可以通过这个类拿到这些信息
             */
            QQToken qqToken = mTencent.getQQToken();
            UserInfo info = new UserInfo(UserDetailFragment.this.getActivity().getApplicationContext(), qqToken);
            //这样我们就拿到这个类了，之后的操作就跟上面的一样了，同样是解析JSON
            final String finalOpenid = openid;
            final String finalAccessToken = accessToken;
            info.getUserInfo(new IUiListener() {
                /*
                  {
	                 "city": "成都",
	                 "figureurl": "http://qzapp.qlogo.cn/qzapp/1104732115/563559BEF3E2F97B693A6F88308F8D21/30",
	                 "figureurl_1": "http://qzapp.qlogo.cn/qzapp/1104732115/563559BEF3E2F97B693A6F88308F8D21/50",
	                 "figureurl_2": "http://qzapp.qlogo.cn/qzapp/1104732115/563559BEF3E2F97B693A6F88308F8D21/100",
	                 "figureurl_qq_1": "http://q.qlogo.cn/qqapp/1104732115/563559BEF3E2F97B693A6F88308F8D21/40",
	                 "figureurl_qq_2": "http://q.qlogo.cn/qqapp/1104732115/563559BEF3E2F97B693A6F88308F8D21/100",
	                 "gender": "男",
	                 "is_lost": 0,
	                 "is_yellow_vip": "0",
	                 "is_yellow_year_vip": "0",
	                 "level": "0",
	                 "msg": "",
	                 "nickname": "生命短暂，快乐至上。",
	                 "province": "四川",
	                 "ret": 0,
	                 "vip": "0",
	                 "yellow_vip_level": "0"
                    }
                 */
                public void onComplete(final Object response) {

                    JSONObject json = (JSONObject) response;
                    String name = null;
                    String image = null;
                    try {
                        name = json.getString("nickname");
                        image = json.getString("figureurl_qq_2");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ((CircleProgressBarLayout) getActivity().findViewById(R.id.layout_progress)).show();
                    final String finalImage = image;
                    final String finalName = name;
                    NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (UserCenter.getInstance().LoginQQ(finalOpenid,
                                    finalAccessToken, finalName, finalImage)) {
                                Bitmap bitmap = ImageLoaderManager.loadImageSync(finalImage);
                                FilePathUtils.saveOtherImage(FilePathUtils.getQQImagePath(), bitmap);
                                //登录成功
                                mHandler.sendEmptyMessage(MESSAGE_LOGIN_QQ_OK);
                            }
                        }
                    });
                }

                public void onCancel() {
                }

                public void onError(UiError arg0) {
                }

            });
        }

        @Override
        public void onError(UiError uiError) {
        }
    }

}
