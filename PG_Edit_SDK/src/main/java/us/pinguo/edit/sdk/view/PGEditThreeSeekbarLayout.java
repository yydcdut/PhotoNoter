package us.pinguo.edit.sdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import us.pinguo.edit.sdk.R;

public class PGEditThreeSeekbarLayout extends PGEditSeekbarLayout {

    private TextView centerView;
    private TextView leftView;
    private TextView rightView;
    private OnClickListener mTextClickListener;

    public PGEditThreeSeekbarLayout(Context context) {
        super(context);

        init();
    }

    public PGEditThreeSeekbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    @Override
    protected void init() {
        LayoutInflater.from(getContext().getApplicationContext())
                .inflate(R.layout.pg_sdk_edit_three_seekbar_layout, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        centerView = (TextView) findViewById(R.id.center_tv);
        leftView = (TextView) findViewById(R.id.left_tv);
        rightView = (TextView) findViewById(R.id.right_tv);
    }

    public void setTextName(String leftName, String centerName, String rightName) {
        centerView.setText(centerName);
        leftView.setText(leftName);
        rightView.setText(rightName);

        centerView.setSelected(false);
        leftView.setSelected(false);
        rightView.setSelected(false);

        leftView.setVisibility(VISIBLE);
        rightView.setVisibility(VISIBLE);
    }

    public void setTextClickListener(OnClickListener onClickListener, String leftTag, String centerTag, String rightTag) {
        centerView.setOnClickListener(onClickListener);
        centerView.setTag(centerTag);
        leftView.setOnClickListener(onClickListener);
        leftView.setTag(leftTag);
        rightView.setOnClickListener(onClickListener);
        rightView.setTag(rightTag);

        mTextClickListener = onClickListener;
    }

    public void selectedFirstText() {

        if (leftView.getVisibility() == VISIBLE) {
            mTextClickListener.onClick(leftView);

        } else if (centerView.getVisibility() == VISIBLE) {
            mTextClickListener.onClick(centerView);

        } else if (rightView.getVisibility() == VISIBLE) {
            mTextClickListener.onClick(rightView);

        }
    }
}
