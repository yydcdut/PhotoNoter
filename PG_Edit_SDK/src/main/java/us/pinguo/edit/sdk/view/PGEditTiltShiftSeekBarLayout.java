package us.pinguo.edit.sdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import us.pinguo.edit.sdk.R;

/**
 * Created by pinguo on 14-9-1.
 */
public class PGEditTiltShiftSeekBarLayout extends PGEditSeekbarLayout {

    public PGEditTiltShiftSeekBarLayout(Context context) {
        super(context);
    }

    public PGEditTiltShiftSeekBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        LayoutInflater.from(getContext().getApplicationContext())
                .inflate(R.layout.pg_sdk_edit_tiltshift_seekbar_layout, this, true);
    }

}
