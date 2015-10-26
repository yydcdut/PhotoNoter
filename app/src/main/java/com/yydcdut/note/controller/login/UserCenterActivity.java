package com.yydcdut.note.controller.login;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yydcdut.note.R;
import com.yydcdut.note.adapter.UserCenterFragmentAdapter;
import com.yydcdut.note.bean.IUser;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.view.RoundedImageView;
import com.yydcdut.note.view.UserCenterArrowView;

import java.io.File;

/**
 * Created by yuyidong on 15/8/26.
 */
public class UserCenterActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private View mBackgroundImage;
    private int[] mColorArray;
    private static final int INTENTION_LEFT = -1;
    private static final int INTENTION_RIGHT = 1;
    private static final int INTENTION_STOP = 0;
    private int mIntention = INTENTION_STOP;
    private float mLastTimePositionOffset = -1;
    private UserCenterArrowView mUserCenterArrowView;
    private ViewPager mViewPager;

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
        findViewById(R.id.layout_status).setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        initToolBarUI();
        initOtherView();
        initQQ();
        initEvernote();
        initViewPager();
        mUserCenterArrowView.setColorAndMarginWidth(getResources().getColor(R.color.green_colorPrimary), (int) -mScrollWidth);
    }

    private void initOtherView() {
        mBackgroundImage = findViewById(R.id.layout_user_vp_bg);
        mUserCenterArrowView = (UserCenterArrowView) findViewById(R.id.view_arrow);
        findViewById(R.id.img_user_detail).setOnClickListener(this);
        findViewById(R.id.img_user_image).setOnClickListener(this);
        findViewById(R.id.img_user_person).setOnClickListener(this);
        mColorArray = new int[]{getResources().getColor(R.color.green_colorPrimary),
                getResources().getColor(R.color.blue_colorPrimary),
                getResources().getColor(R.color.amber_colorPrimary)};
        mScrollWidth = getResources().getDimension(R.dimen.dimen_36dip) + getResources().getDimension(R.dimen.dimen_24dip);
        mUserCenterArrowView.setColorAndMarginWidth(mColorArray[0], (int) -mScrollWidth);
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
        RoundedImageView imageView = (RoundedImageView) findViewById(R.id.img_user);
        TextView textView = (TextView) findViewById(R.id.txt_name);
        if (UserCenter.getInstance().isLoginQQ() && UserCenter.getInstance().getQQ() != null) {
            IUser qqUser = UserCenter.getInstance().getQQ();
            if (new File(FilePathUtils.getQQImagePath()).exists()) {
                ImageLoaderManager.displayImage("file://" + FilePathUtils.getQQImagePath(), imageView);
            } else {
                ImageLoaderManager.displayImage(qqUser.getNetImagePath(), imageView);
            }
            textView.setText(qqUser.getName());
        } else {
            imageView.setImageResource(R.drawable.ic_no_user);
            textView.setText(getResources().getString(R.string.not_login));
        }
    }

    private void initEvernote() {
        ImageView imageView = (ImageView) findViewById(R.id.img_user_two);
        if (UserCenter.getInstance().isLoginEvernote()) {
            imageView.setImageResource(R.drawable.ic_evernote_color);
        } else {
            imageView.setImageResource(R.drawable.ic_evernote_gray);
        }
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.vp_user);
        mViewPager.setAdapter(new UserCenterFragmentAdapter(getSupportFragmentManager()));
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
                finish();
                break;
        }
        return true;
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
}
