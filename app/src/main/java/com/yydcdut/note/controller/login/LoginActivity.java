package com.yydcdut.note.controller.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.LollipopCompat;
import com.yydcdut.note.view.CircleProgressBarLayout;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yuyidong on 15-3-25.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final int MESSAGE_LOGIN_QQ_OK = 1;

    private Tencent mTencent;

    private Handler mHandler;

    private CircleProgressBarLayout mCircleProgressBar;

    @Override
    public int setContentView() {
        return R.layout.activity_login;
    }

    @Override
    public void initUiAndListener() {
        initToolBarUI();
        initLoginButtonListener();
        initTencent();
        mHandler = new Handler(this);
        mCircleProgressBar = (CircleProgressBarLayout) findViewById(R.id.layout_progress);
    }

    private void initToolBarUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_login));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        LollipopCompat.setElevation(toolbar, getResources().getDimension(R.dimen.ui_elevation));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    /**
     * 初始化腾讯的接口
     */
    private void initTencent() {
        mTencent = Tencent.createInstance("1104732115", getApplicationContext());
    }

    private void initLoginButtonListener() {
        findViewById(R.id.btn_login_qq).setOnClickListener(this);
        findViewById(R.id.btn_login_sina).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //todo 网络
        switch (v.getId()) {
            case R.id.btn_login_qq:
                mTencent.login(LoginActivity.this, "all", new BaseUiListener());
                break;
            case R.id.btn_login_sina:
                Toast.makeText(this, getResources().getString(R.string.toast_not_support), Toast.LENGTH_SHORT).show();
//                mSsoHandler.authorize(new SinaAuthListener());
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_LOGIN_QQ_OK:
                mCircleProgressBar.setVisibility(View.GONE);
                Intent intent = new Intent();
                intent.putExtra(Const.USER, UserCenter.USER_TYPE_QQ);
                setResult(RESULT_DATA, intent);
                finish();
                break;
        }
        return false;
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
            UserInfo info = new UserInfo(getApplicationContext(), qqToken);
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
                    mCircleProgressBar.setVisibility(View.VISIBLE);
                    final String finalImage = image;
                    final String finalName = name;
                    NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (UserCenter.getInstance().set(UserCenter.USER_TYPE_QQ, finalOpenid,
                                    finalAccessToken, finalName, finalImage, true)) {
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