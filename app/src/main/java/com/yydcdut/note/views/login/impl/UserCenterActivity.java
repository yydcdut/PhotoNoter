package com.yydcdut.note.views.login.impl;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evernote.client.android.EvernoteSession;
import com.yydcdut.note.R;
import com.yydcdut.note.adapter.UserCenterFragmentAdapter;
import com.yydcdut.note.presenters.login.impl.UserCenterPresenterImpl;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.login.IUserCenterView;
import com.yydcdut.note.widget.CircleProgressBarLayout;
import com.yydcdut.note.widget.RoundedImageView;
import com.yydcdut.note.widget.UserCenterArrowView;
import com.yydcdut.note.widget.fab2.snack.OnSnackBarActionListener;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

/**
 * Created by yuyidong on 15/8/26.
 */
public class UserCenterActivity extends BaseActivity implements IUserCenterView {
    @Inject
    UserCenterPresenterImpl mUserCenterPresenter;

    @Bind(R.id.layout_user_vp_bg)
    View mBackgroundImage;
    @Bind(R.id.view_arrow)
    UserCenterArrowView mUserCenterArrowView;
    @Bind(R.id.layout_progress)
    CircleProgressBarLayout mCircleProgressBarLayout;
    @Bind(R.id.img_user)
    RoundedImageView mQQImageView;
    @Bind(R.id.txt_name)
    TextView mQQTextView;
    @Bind(R.id.img_user_two)
    ImageView mEvernoteImageView;
    @Bind(R.id.vp_user)
    ViewPager mViewPager;

    private int[] mColorArray;
    private static final int INTENTION_LEFT = -1;
    private static final int INTENTION_RIGHT = 1;
    private static final int INTENTION_STOP = 0;
    private int mIntention = INTENTION_STOP;
    private float mLastTimePositionOffset = -1;

    private FragmentPagerAdapter mPagerAdapter;

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
    public void initInjector() {
        mActivityComponent.inject(this);
        mIPresenter = mUserCenterPresenter;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        mUserCenterPresenter.attachView(this);
        if (AppCompat.AFTER_LOLLIPOP) {
            findViewById(R.id.layout_status).setBackgroundColor(AppCompat.getColor(android.R.color.darker_gray, this));
        }
        initToolBarUI();
        initOtherViewAndData();
        initViewPager();
    }

    private void initOtherViewAndData() {
        mColorArray = new int[]{AppCompat.getColor(R.color.blue_colorPrimary, this),
                AppCompat.getColor(R.color.amber_colorPrimary, this)};
        mScrollWidth = getResources().getDimension(R.dimen.dimen_24dip) * 3 / 2;
        mUserCenterArrowView.setColorAndMarginWidth(mColorArray[0], (int) -mScrollWidth);
    }

    private void initToolBarUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.personal_page));
        toolbar.setTitleTextColor(AppCompat.getColor(R.color.txt_gray, this));
        toolbar.setBackgroundColor(AppCompat.getColor(android.R.color.white, this));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_gray_24dp);
        toolbar.setOnMenuItemClickListener(onToolBarMenuItemClick);
    }

    private void initViewPager() {
        mPagerAdapter = new UserCenterFragmentAdapter(getFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
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
//                    Toast.makeText(UserCenterActivity.this, ":11111", Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mUserCenterPresenter.finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        mUserCenterPresenter.finish();
        super.onBackPressed();
    }

    @OnClick(R.id.img_user_detail)
    public void clickImageDetail(View v) {
        if (mViewPager.getCurrentItem() != 0) {
            mViewPager.setCurrentItem(0, true);
        }
    }

    @OnClick(R.id.img_user_person)
    public void clickUserPerson(View v) {
        if (mViewPager.getCurrentItem() != 1) {
            mViewPager.setCurrentItem(1, true);
        }
    }

    @OnClick(R.id.img_user)
    public void clickUserQQ() {
        if (mUserCenterPresenter.checkInternet()) {
            mUserCenterPresenter.loginQQ();
        }
    }

    @OnClick(R.id.img_user_two)
    public void clickUserEvernote(View v) {
        if (mUserCenterPresenter.checkInternet()) {
            mUserCenterPresenter.loginEvernote();
        }
    }

    @OnPageChange(
            value = R.id.vp_user,
            callback = OnPageChange.Callback.PAGE_SCROLLED
    )
    public void viewPagerScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mIntention == INTENTION_STOP) {
            if (mLastTimePositionOffset == -1) {
                mLastTimePositionOffset = positionOffset;
            } else {
                mIntention = positionOffset - mLastTimePositionOffset >= 0 ? INTENTION_RIGHT : INTENTION_LEFT;
            }
        } else if (mIntention == INTENTION_RIGHT && positionOffset < 0.99) {//right
            if (position >= 1) {
                return;
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
            mUserCenterArrowView.setColorAndMarginWidth(newColor, (int) (mScrollWidth * positionOffset * 2 - mScrollWidth));
        } else if (mIntention == INTENTION_LEFT && positionOffset > 0.01) {//left
            if (position < 0) {
                return;
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
            mUserCenterArrowView.setColorAndMarginWidth(newColor, (int) ((mScrollWidth * positionOffset) * 2 - mScrollWidth))
            ;
        }
        if (positionOffset < 0.01 || positionOffset > 0.99) {
            //重新计算方向
            mIntention = INTENTION_STOP;
            mLastTimePositionOffset = -1;
        }
    }

    @OnPageChange(
            value = R.id.vp_user,
            callback = OnPageChange.Callback.PAGE_SCROLL_STATE_CHANGED
    )
    public void viewPagerScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            mIntention = INTENTION_STOP;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EvernoteSession.REQUEST_CODE_LOGIN:
                if (resultCode == RESULT_OK) {
                    // handle success
                    mUserCenterPresenter.onEvernoteLoginFinished(true);
                } else {
                    // handle failure
                    mUserCenterPresenter.onEvernoteLoginFinished(false);
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void showQQInfo(String name, String imagePath) {
        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(imagePath)) {
            mQQImageView.setImageResource(R.drawable.ic_no_user);
            mQQTextView.setVisibility(View.INVISIBLE);
        } else {
            mQQTextView.setVisibility(View.VISIBLE);
            mQQTextView.setText(name);
            ImageLoaderManager.displayImage(imagePath, mQQImageView);
        }
    }

    @Override
    public void showEvernote(boolean login) {
        if (login) {
            mEvernoteImageView.setImageResource(R.drawable.ic_evernote_color);
        } else {
            mEvernoteImageView.setImageResource(R.drawable.ic_evernote_gray);
        }
    }

    @Override
    public void showQQInfoInFrag(String name) {
        LinearLayout linearLayout = (LinearLayout) mPagerAdapter.getItem(2).getView().findViewById(R.id.layout_user_detail);
        View qqView = linearLayout.getChildAt(0);
        ((TextView) qqView.findViewById(R.id.txt_item_column)).setText(name);
        ((ImageView) qqView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_clear_white_24dp);
        mCircleProgressBarLayout.hide();
    }

    @Override
    public void showEvernoteInFrag(boolean login, String userName) {
        LinearLayout linearLayout2 = (LinearLayout) mPagerAdapter.getItem(2).getView().findViewById(R.id.layout_user_detail);
        View evernoteView = linearLayout2.getChildAt(1);
        if (login) {
            ((TextView) evernoteView.findViewById(R.id.txt_item_column)).setText(userName);
        } else {
            ((TextView) evernoteView.findViewById(R.id.txt_item_column)).setText(getResources().getString(R.string.user_failed));
        }
        ((ImageView) evernoteView.findViewById(R.id.img_item_user)).setImageResource(R.drawable.ic_clear_white_24dp);
    }

    @Override
    public void showProgressBar() {
        mCircleProgressBarLayout.show();
    }

    @Override
    public void hideProgressBar() {
        mCircleProgressBarLayout.hide();
    }

    @Override
    public void finishActivityWithResult(int result) {
        if (result > 0) {
            setResult(result);
        }
        finish();
    }

    @Override
    public void showSnackBar(String message) {
        Snackbar.make(mViewPager, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showSnackBarWithAction(String message, String action, final OnSnackBarActionListener listener) {
        Snackbar.make(mViewPager, message, Snackbar.LENGTH_SHORT)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onClick();
                        }
                    }
                }).show();
    }

}
