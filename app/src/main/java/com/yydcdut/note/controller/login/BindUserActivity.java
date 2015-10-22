package com.yydcdut.note.controller.login;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.yydcdut.note.R;
import com.yydcdut.note.bean.IUser;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.utils.LollipopCompat;

/**
 * Created by yuyidong on 15/8/26.
 */
public class BindUserActivity extends BaseActivity implements View.OnClickListener {
    private IUser mUser;

    @Override
    public boolean setStatusBar() {
        return true;
    }

    @Override
    public int setContentView() {
        return R.layout.activity_bind_user;
    }

    @Override
    public void initUiAndListener() {
        initToolBarUI();
//        mUser = UserCenter.getInstance().userFactory(UserCenter.getInstance().getFirstUserType());
//        RoundedImageView imageView = (RoundedImageView) findViewById(R.id.img_user);
//        if (mUser.getType().equals(UserCenter.USER_TYPE_QQ)) {
//            if (new File(FilePathUtils.getQQImagePath()).exists()) {
//                ImageLoaderManager.displayImage("file://" + FilePathUtils.getQQImagePath(), imageView);
//            } else {
//                ImageLoaderManager.displayImage(mUser.getNetImagePath(), imageView);
//            }
//        } else {
//            if (new File(FilePathUtils.getSinaImagePath()).exists()) {
//                ImageLoaderManager.displayImage("file://" + FilePathUtils.getSinaImagePath(), imageView);
//            } else {
//                ImageLoaderManager.displayImage(mUser.getNetImagePath(), imageView);
//            }
//        }
//        ((TextView) findViewById(R.id.txt_name)).setText(mUser.getName());
//        if (mUser.getType().equals(UserCenter.USER_TYPE_QQ)) {
//            bindQQ();
//            if (UserCenter.getInstance().existUserNumber() == 1) {
//                unbindSina();
//            } else {
//                bindSina();
//            }
//        } else {
//            bindSina();
//            if (UserCenter.getInstance().existUserNumber() == 1) {
//                unbindQQ();
//            } else {
//                bindQQ();
//            }
//        }
//        findViewById(R.id.btn_loginout).setOnClickListener(this);
    }

    private void bindQQ() {
        TextView textView = (TextView) findViewById(R.id.txt_qq);
        textView.setTextColor(getResources().getColor(R.color.txt_alpha_gray));
        textView.setText(getResources().getString(R.string.unbind_qq));
        textView.setClickable(false);
    }

    private void unbindQQ() {
        TextView textView = (TextView) findViewById(R.id.txt_qq);
        textView.setTextColor(getResources().getColor(R.color.color_loginout_not_exist));
        textView.setText(getResources().getString(R.string.bind_qq));
        textView.setClickable(true);
        textView.setOnClickListener(this);
    }

    private void bindSina() {
        TextView textView = (TextView) findViewById(R.id.txt_sina);
        textView.setTextColor(getResources().getColor(R.color.txt_alpha_gray));
        textView.setText(getResources().getString(R.string.unbind_sina));
        textView.setClickable(false);
    }

    private void unbindSina() {
        TextView textView = (TextView) findViewById(R.id.txt_sina);
        textView.setTextColor(getResources().getColor(R.color.color_loginout_not_exist));
        textView.setText(getResources().getString(R.string.bind_sina));
        textView.setClickable(true);
        textView.setOnClickListener(this);
    }

    private void initToolBarUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.personal_page));
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

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.txt_qq:
//                break;
//            case R.id.txt_sina:
//                Toast.makeText(this, getResources().getString(R.string.toast_not_support), Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.btn_loginout:
//                UserCenter.getInstance().logoutQQ();
//                Intent intent = new Intent();
//                intent.putExtra(Const.USER, UserCenter.USER_TYPE_QQ);
//                setResult(RESULT_DATA, intent);
//                finish();
//                break;
//        }
    }
}
