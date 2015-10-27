package com.yydcdut.note.controller.setting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.yydcdut.note.R;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.utils.LollipopCompat;

/**
 * Created by yuyidong on 15/10/26.
 */
public class FloatingEditActivity extends BaseActivity {
    private Fragment mAlbumEditFragment = null;
    private Fragment mEditTextEditFragment = null;

    @Override
    public boolean setStatusBar() {
        return true;
    }

    @Override
    public int setContentView() {
        return R.layout.activity_floating_edit;
    }

    @Override
    public void initUiAndListener() {
        initToolBar();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_check_white_24dp);
        LollipopCompat.setElevation(toolbar, getResources().getDimension(R.dimen.ui_elevation));
        setSupportActionBar(toolbar);
        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.floating_edit_navigation,
                android.R.layout.simple_spinner_dropdown_item);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);//导航模式必须设为NAVIGATION_MODE_LIST
        getSupportActionBar().setListNavigationCallbacks(mSpinnerAdapter,
                mOnNavigationListener);
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

    private ActionBar.OnNavigationListener mOnNavigationListener = new ActionBar.OnNavigationListener() {

        @Override
        public boolean onNavigationItemSelected(int position, long itemId) {
            switch (position) {
                case 0:
                    if (mAlbumEditFragment == null) {
                        Bundle bundle = new Bundle();
                        mAlbumEditFragment = FloatingEditFragment.getInstance();
                        bundle.putInt(FloatingEditFragment.TYPE, 0);
                        mAlbumEditFragment.setArguments(bundle);
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.layout_edit_floating, mAlbumEditFragment, null)
                            .commit();
                    break;
                case 1:
                    if (mEditTextEditFragment == null) {
                        Bundle bundle = new Bundle();
                        mEditTextEditFragment = FloatingEditFragment.getInstance();
                        bundle.putInt(FloatingEditFragment.TYPE, 1);
                        mEditTextEditFragment.setArguments(bundle);
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.layout_edit_floating, mEditTextEditFragment, null)
                            .commit();
                    break;
                default:
                    break;
            }
            return true;
        }
    };
}
