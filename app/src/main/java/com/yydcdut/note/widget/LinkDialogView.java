package com.yydcdut.note.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.yydcdut.note.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 16/8/17.
 */
public class LinkDialogView extends LinearLayout {

    @BindView(R.id.edit_description_link)
    EditText mDescriptionEditText;
    @BindView(R.id.edit_link)
    EditText mLinkEditText;

    public LinkDialogView(Context context) {
        super(context);
        init(context);
    }

    public LinkDialogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public LinkDialogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_link, this, true);
        ButterKnife.bind(v, this);
    }

    public void clear() {
        mDescriptionEditText.setText("");
        mLinkEditText.setText("http://");
    }

    public String getDescription() {
        return mDescriptionEditText.getText().toString();
    }

    public String getLink() {
        return mLinkEditText.getText().toString();
    }

}
