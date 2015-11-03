package com.yydcdut.note.controller.setting;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.yydcdut.note.R;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.model.FeedbackModel;
import com.yydcdut.note.utils.LollipopCompat;

/**
 * Created by yuyidong on 15/11/3.
 */
public class FeedbackActivity extends BaseActivity implements View.OnClickListener {
    private EditText mEditText;

    @Override
    public boolean setStatusBar() {
        return true;
    }

    @Override
    public int setContentView() {
        return R.layout.activity_feedback;

    }


    @Override
    public void initUiAndListener() {
        initToolBarUI();
        initSendAndEditView();
    }

    private void initToolBarUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.feedback));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        LollipopCompat.setElevation(toolbar, getResources().getDimension(R.dimen.ui_elevation));
    }

    private void initSendAndEditView() {
        findViewById(R.id.fab_send).setOnClickListener(this);
        mEditText = (EditText) findViewById(R.id.et_feedback);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_send:
                FeedbackModel.getInstance().sendFeedback("15777", "6666666wo6ni");
                break;
        }
    }
}
