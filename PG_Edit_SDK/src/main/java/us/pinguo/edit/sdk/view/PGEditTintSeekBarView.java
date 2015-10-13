package us.pinguo.edit.sdk.view;

import android.content.Context;

import us.pinguo.edit.sdk.R;

/**
 * Created by litao on 14/12/9.
 */
public class PGEditTintSeekBarView extends PGEditThreeSeekBarView {

    @Override
    public String[] getTextNameArray(Context context) {

        String textNameGreen = context.getResources().getString(R.string.pg_sdk_edit_tint_green);
        String textNameRed = context.getResources().getString(R.string.pg_sdk_edit_tint_red);
        String textNameYellow = context.getResources().getString(R.string.pg_sdk_edit_tint_yellow);
        return new String[]{textNameGreen, textNameRed, textNameYellow};
    }
}
