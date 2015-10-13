package us.pinguo.edit.sdk.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import us.pinguo.edit.sdk.R;
import us.pinguo.edit.sdk.widget.PGEditMenuItemView;

public class PGEditMenuItemWithValueView extends PGEditMenuItemView {

    private TextView mValueTv;

    public PGEditMenuItemWithValueView(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResources(Context context) {
        return R.layout.pg_sdk_edit_menu_item_with_value;
    }

    protected void init(Context context) {
        super.init(context);
        mValueTv = (TextView) findViewById(R.id.value);

    }

    public void setIcon(Drawable drawable) {
        if (mIconImageView != null) {
            mIconImageView.setImageDrawable(drawable);
        }
    }

    @Override
    public void setValue(String value) {
        if (null != mValueTv) {
            mValueTv.setText(value);
            mValueTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideValue() {
        if (null != mValueTv) {
            mValueTv.setVisibility(View.INVISIBLE);
        }
    }

    public void hideName() {
        findViewById(R.id.name).setVisibility(View.GONE);
    }

    public void setItemBg(int color) {
        findViewById(R.id.bg_view).setBackgroundColor(color);
    }

}
