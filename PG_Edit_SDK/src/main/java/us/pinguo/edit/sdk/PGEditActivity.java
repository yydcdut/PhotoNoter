package us.pinguo.edit.sdk;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.nostra13.universalimageloader.core.ImageLoader;

import us.pinguo.edit.sdk.base.controller.PGEditController;
import us.pinguo.edit.sdk.base.utils.ApiHelper;
import us.pinguo.edit.sdk.base.view.IPGEditView;
import us.pinguo.edit.sdk.view.PGEditView;

/**
 * Created by hlf on 14-2-12.
 */
public class PGEditActivity extends FragmentActivity {

    private PGEditController mPGEditController;
    private PGEditView mPGEditView;
    private IPGEditView mEditView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 进编辑页即释放掉ImageLoader的缓存，尽量增大可用内存
        ImageLoader.getInstance().clearMemoryCache();

        mEditView = new PGEditView();
        mEditView.initView(this);

        mPGEditController = new PGEditController();
        mPGEditController.setActivity(this);
        mPGEditController.onCreate(mEditView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPGEditController.onPause();
        if (ApiHelper.AFTER_ICE_CREAM_SANDWICH) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPGEditController.onResume();
        // 让系统导航栏dim显示
        if (ApiHelper.AFTER_ICE_CREAM_SANDWICH) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }

    @Override
    protected void onDestroy() {

        mPGEditController.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mPGEditController.keyBack();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


}
