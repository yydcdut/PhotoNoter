package com.yydcdut.note.controller.login;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginFragment;
import com.evernote.edam.type.User;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yydcdut.note.BuildConfig;
import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.adapter.UserCenterFragmentAdapter;
import com.yydcdut.note.bean.IUser;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.LollipopCompat;
import com.yydcdut.note.view.CircleProgressBarLayout;
import com.yydcdut.note.view.RoundedImageView;
import com.yydcdut.note.view.UserCenterArrowView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by yuyidong on 15/8/26.
 */
public class UserCenterActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener,
        EvernoteLoginFragment.ResultCallback, Handler.Callback {
    private View mBackgroundImage;
    private int[] mColorArray;
    private static final int INTENTION_LEFT = -1;
    private static final int INTENTION_RIGHT = 1;
    private static final int INTENTION_STOP = 0;
    private int mIntention = INTENTION_STOP;
    private float mLastTimePositionOffset = -1;
    private UserCenterArrowView mUserCenterArrowView;
    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private static final int MESSAGE_LOGIN_QQ_OK = 1;
    private static final int MESSAGE_LOGIN_QQ_FAILED = 3;
    private static final int MESSAGE_LOGIN_EVERNOTE_OK = 2;
    private static final int MESSAGE_LOGIN_EVERNOTE_FAILED = 4;
    private Handler mHandler;

    private Tencent mTencent;
    private RoundedImageView mQQImageView;
    private TextView mQQTextView;
    private ImageView mEvernoteImageView;
    private CircleProgressBarLayout mCircleProgressBarLayout;

    private boolean mInitQQState = false;
    private boolean minitEvernoteState = false;

    private float mScrollWidth = 0f;

    @Override
    public boolean setStatusBar() {
        return true;
    }

    @Override
    public int setContentView() {
        return R.layout.activity_user_center;
    }

    @Override
    public void initUiAndListener() {
        if (LollipopCompat.AFTER_LOLLIPOP) {
            findViewById(R.id.layout_status).setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        }
        initToolBarUI();
        initOtherViewAndData();
        initQQ();
        initEvernote();
        initViewPager();
        mHandler = new Handler(this);
    }

    private void initOtherViewAndData() {
        mBackgroundImage = findViewById(R.id.layout_user_vp_bg);
        mCircleProgressBarLayout = (CircleProgressBarLayout) findViewById(R.id.layout_progress);
        mUserCenterArrowView = (UserCenterArrowView) findViewById(R.id.view_arrow);
        findViewById(R.id.img_user_detail).setOnClickListener(this);
        findViewById(R.id.img_user_image).setOnClickListener(this);
        findViewById(R.id.img_user_person).setOnClickListener(this);
        mColorArray = new int[]{getResources().getColor(R.color.green_colorPrimary),
                getResources().getColor(R.color.blue_colorPrimary),
                getResources().getColor(R.color.amber_colorPrimary)};
        mScrollWidth = getResources().getDimension(R.dimen.dimen_36dip) + getResources().getDimension(R.dimen.dimen_24dip);
        mUserCenterArrowView.setColorAndMarginWidth(mColorArray[0], (int) -mScrollWidth);
        mUserCenterArrowView.setColorAndMarginWidth(getResources().getColor(R.color.green_colorPrimary), (int) -mScrollWidth);
        mInitQQState = UserCenter.getInstance().isLoginQQ();
        minitEvernoteState = UserCenter.getInstance().isLoginEvernote();
    }

    private void initToolBarUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.personal_page));
        toolbar.setTitleTextColor(getResources().getColor(R.color.txt_gray));
        toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_gray_24dp);
        toolbar.setOnMenuItemClickListener(onToolBarMenuItemClick);
    }

    private void initQQ() {
        mQQImageView = (RoundedImageView) findViewById(R.id.img_user);
        mQQTextView = (TextView) findViewById(R.id.txt_name);
        if (UserCenter.getInstance().isLoginQQ() && UserCenter.getInstance().getQQ() != null) {
            IUser qqUser = UserCenter.getInstance().getQQ();
            if (new File(FilePathUtils.getQQImagePath()).exists()) {
                ImageLoaderManager.displayImage("file://" + FilePathUtils.getQQImagePath(), mQQImageView);
            } else {
                ImageLoaderManager.displayImage(qqUser.getNetImagePath(), mQQImageView);
                FilePathUtils.saveImage(FilePathUtils.getQQImagePath(), ImageLoaderManager.loadImageSync(qqUser.getNetImagePath()));
            }
            mQQTextView.setVisibility(View.VISIBLE);
            mQQTextView.setText(qqUser.getName());
        } else {
            mQQImageView.setImageResource(R.drawable.ic_no_user);
            mQQTextView.setVisibility(View.INVISIBLE);
        }
        mQQImageView.setOnClickListener(this);
    }

    private void initEvernote() {
        mEvernoteImageView = (ImageView) findViewById(R.id.img_user_two);
        if (UserCenter.getInstance().isLoginEvernote()) {
            mEvernoteImageView.setImageResource(R.drawable.ic_evernote_color);
        } else {
            mEvernoteImageView.setImageResource(R.drawable.ic_evernote_gray);
        }
        mEvernoteImageView.setOnClickListener(this);
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.vp_user);
        mPagerAdapter = new UserCenterFragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_center, menu);
        return true;
    }

    private Toolbar.OnMenuItemClickListener onToolBarMenuItemClick = new Toolbar.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_share:
                    Toast.makeText(UserCenterActivity.this, ":11111", Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mInitQQState != UserCenter.getInstance().isLoginQQ() || minitEvernoteState != UserCenter.getInstance().isLoginEvernote()) {
                    setResult(RESULT_DATA_USER);
                }
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mInitQQState != UserCenter.getInstance().isLoginQQ() || minitEvernoteState != UserCenter.getInstance().isLoginEvernote()) {
            setResult(RESULT_DATA_USER);
        }
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int index = mViewPager.getCurrentItem();
        switch (v.getId()) {
            case R.id.img_user_detail:
                if (index == 0) {
                    break;
                }
                mViewPager.setCurrentItem(0, true);
                break;
            case R.id.img_user_image:
                if (index == 1) {
                    break;
                }
                mViewPager.setCurrentItem(1, true);
                break;
            case R.id.img_user_person:
                if (index == 2) {
                    break;
                }
                mViewPager.setCurrentItem(2, true);
                break;
            case R.id.img_user:
                if (!UserCenter.getInstance().isLoginQQ()) {
                    mTencent = Tencent.createInstance(BuildConfig.TENCENT_KEY, getApplicationContext());
                    mTencent.login(this, "all", new BaseUiListener());
                }
                break;
            case R.id.img_user_two:
                if (!UserCenter.getInstance().isLoginEvernote()) {
                    EvernoteSession.getInstance().authenticate(this);
                }
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mIntention == INTENTION_STOP) {
            if (mLastTimePositionOffset == -1) {
                mLastTimePositionOffset = positionOffset;
            } else {
                mIntention = positionOffset - mLastTimePositionOffset >= 0 ? INTENTION_RIGHT : INTENTION_LEFT;
            }
        } else if (mIntention == INTENTION_RIGHT && positionOffset < 0.99) {//right
            if (position >= 2) {
                position = 1;
            }
            int r2 = Color.red(mColorArray[position + 1]);
            int r1 = Color.red(mColorArray[position]);
            int g2 = Color.green(mColorArray[position + 1]);
            int g1 = Color.green(mColorArray[position]);
            int b2 = Color.blue(mColorArray[position + 1]);
            int b1 = Color.blue(mColorArray[position]);
            int deltaR = r1 - r2;
            int deltaG = g1 - g2;
            int deltaB = b1 - b2;
            int newR = (int) (r1 - deltaR * positionOffset);
            int newG = (int) (g1 - deltaG * positionOffset);
            int newB = (int) (b1 - deltaB * positionOffset);
            int newColor = Color.rgb(newR, newG, newB);
            mBackgroundImage.setBackgroundColor(newColor);
            mUserCenterArrowView.setColorAndMarginWidth(newColor, (int) (mScrollWidth * positionOffset + position * mScrollWidth - mScrollWidth));
        } else if (mIntention == INTENTION_LEFT && positionOffset > 0.01) {//left
            if (position <= 0) {
                position = 1;
            }
            int r0 = Color.red(mColorArray[position]);
            int r1 = Color.red(mColorArray[position + 1]);
            int g0 = Color.green(mColorArray[position]);
            int g1 = Color.green(mColorArray[position + 1]);
            int b0 = Color.blue(mColorArray[position]);
            int b1 = Color.blue(mColorArray[position + 1]);
            int deltaR = r1 - r0;
            int deltaG = g1 - g0;
            int deltaB = b1 - b0;
            int newR = (int) (r1 - deltaR * (1 - positionOffset));
            int newG = (int) (g1 - deltaG * (1 - positionOffset));
            int newB = (int) (b1 - deltaB * (1 - positionOffset));
            int newColor = Color.rgb(newR, newG, newB);
            mBackgroundImage.setBackgroundColor(newColor);
            mUserCenterArrowView.setColorAndMarginWidth(newColor, (int) ((position + 1) * mScrollWidth - (mScrollWidth * (1 - positionOffset)) - mScrollWidth));
        }
        if (positionOffset < 0.01 || positionOffset > 0.99) {
            //重新计算方向
            mIntention = INTENTION_STOP;
            mLastTimePositionOffset = -1;
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            mIntention = INTENTION_STOP;
        }
    }


    @Override
    public void onLoginFinished(boolean successful) {
        Log.i("yuyidong", " onLoginFinished    successful--->" + successful);
        if (successful) {
            mCircleProgressBarLayout.show();
            UserCenter.getInstance().LoginEvernote();
            mHandler.sendEmptyMessage(MESSAGE_LOGIN_EVERNOTE_OK);
        } else {
            mHandler.sendEmptyMessage(MESSAGE_LOGIN_EVERNOTE_FAILED);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_LOGIN_QQ_OK:
                IUser qqUser = UserCenter.getInstance().getQQ();
                if (new File(FilePathUtils.getQQImagePath()).exists()) {
                    ImageLoaderManager.displayImage("file://" + FilePathUtils.getQQImagePath(), mQQImageView);
                } else {
                    ImageLoaderManager.displayImage(qqUser.getNetImagePath(), mQQImageView);
                    FilePathUtils.saveImage(FilePathUtils.getQQImagePath(), ImageLoaderManager.loadImageSync(qqUser.getNetImagePath()));
                }
                mQQTextView.setVisibility(View.VISIBLE);
                mQQTextView.setText(qqUser.getName());

                LinearLayout linearLayout = (LinearLayout) mPagerAdapter.getItem(2).getView().findViewById(R.id.layout_user_detail);
                View qqView = linearLayout.getChildAt(0);
                ((TextView) qqView.findViewById(R.id.txt_item_column)).setText(qqUser.getName());
                ((ImageView) qqView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_clear_white_24dp);
                mCircleProgressBarLayout.hide();
                Snackbar.make(mViewPager, getResources().getString(R.string.toast_success), Snackbar.LENGTH_SHORT).show();
                break;
            case MESSAGE_LOGIN_EVERNOTE_OK:
                mEvernoteImageView.setImageResource(R.drawable.ic_evernote_color);

                LinearLayout linearLayout2 = (LinearLayout) mPagerAdapter.getItem(2).getView().findViewById(R.id.layout_user_detail);
                View evernoteView = linearLayout2.getChildAt(1);
                User evernoteUser = UserCenter.getInstance().getEvernote();
                if (evernoteUser != null) {
                    ((TextView) evernoteView.findViewById(R.id.txt_item_column)).setText(evernoteUser.getUsername());
                } else {
                    ((TextView) evernoteView.findViewById(R.id.txt_item_column)).setText(getResources().getString(R.string.user_failed));
                }
                ((ImageView) evernoteView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_clear_white_24dp);
                mCircleProgressBarLayout.hide();
                Snackbar.make(mViewPager, getResources().getString(R.string.toast_success), Snackbar.LENGTH_SHORT).show();
                break;
            case MESSAGE_LOGIN_EVERNOTE_FAILED:
                mCircleProgressBarLayout.hide();
                Snackbar.make(mViewPager, getResources().getString(R.string.toast_fail), Snackbar.LENGTH_SHORT)
                        .setAction(getResources().getString(R.string.toast_retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EvernoteSession.getInstance().authenticate(UserCenterActivity.this);
                            }
                        }).show();
                break;
            case MESSAGE_LOGIN_QQ_FAILED:
                Snackbar.make(mViewPager, getResources().getString(R.string.toast_fail), Snackbar.LENGTH_SHORT)
                        .setAction(getResources().getString(R.string.toast_retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTencent = Tencent.createInstance(BuildConfig.TENCENT_KEY, getApplicationContext());
                                mTencent.login(UserCenterActivity.this, "all", new BaseUiListener());
                            }
                        }).show();
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
            UserInfo info = new UserInfo(UserCenterActivity.this.getApplicationContext(), qqToken);
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
                    mCircleProgressBarLayout.show();
                    final String finalImage = image;
                    final String finalName = name;
                    NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (UserCenter.getInstance().LoginQQ(finalOpenid,
                                    finalAccessToken, finalName, finalImage)) {
                                Bitmap bitmap = ImageLoaderManager.loadImageSync(finalImage);
                                FilePathUtils.saveImage(FilePathUtils.getQQImagePath(), bitmap);
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
